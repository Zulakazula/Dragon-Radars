package com.dragon.dragonmod.client;

import com.dragon.dragonmod.client.gui.fire.FireRadarSettings;
import com.dragon.dragonmod.client.gui.ice.IceRadarSettings;
import com.dragon.dragonmod.client.gui.lightning.LightningRadarSettings;
import com.dragon.dragonmod.client.gui.master.MasterRadarSettings;
import com.dragon.dragonmod.server.ServerDiskDragonScanner;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class DragonScanner {

    public static boolean isSearchComplete = false;

    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation("dragonmod", "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void registerPackets() {
        int id = 0;
        CHANNEL.registerMessage(id++, RequestDragonsPacket.class, RequestDragonsPacket::encode, RequestDragonsPacket::decode, RequestDragonsPacket::handle);
        CHANNEL.registerMessage(id++, ResponseDragonsPacket.class, ResponseDragonsPacket::encode, ResponseDragonsPacket::decode, ResponseDragonsPacket::handle);
        CHANNEL.registerMessage(id++, TeleportRequestPacket.class, TeleportRequestPacket::encode, TeleportRequestPacket::decode, TeleportRequestPacket::handle);
    }

    // --- CLIENT TO SERVER ---
    public static class RequestDragonsPacket {
        private final double radius;
        private final String radarType;
        
        public RequestDragonsPacket(double radius, String radarType) { 
            this.radius = radius;
            this.radarType = radarType;
        }

        public static void encode(RequestDragonsPacket packet, FriendlyByteBuf buf) {
            buf.writeDouble(packet.radius);
            buf.writeUtf(packet.radarType);
        }

        public static RequestDragonsPacket decode(FriendlyByteBuf buf) {
            return new RequestDragonsPacket(buf.readDouble(), buf.readUtf());
        }

        public static void handle(RequestDragonsPacket packet, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                ServerPlayer player = ctx.get().getSender();
                if (player == null) return;

                List<DragonInfo> foundDragons = new ArrayList<>();
                ServerLevel level = player.serverLevel();
                
                List<ServerDiskDragonScanner.DragonData> diskResults = 
                    ServerDiskDragonScanner.scanDragons(level, player.getX(), player.getZ(), packet.radius);
                
                for (ServerDiskDragonScanner.DragonData data : diskResults) {
                    foundDragons.add(new DragonInfo(data.stage, data.type, data.isMale, data.x, data.y, data.z));
                }

                CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new ResponseDragonsPacket(foundDragons, packet.radarType));
            });
            ctx.get().setPacketHandled(true);
        }
    }

    // --- SERVER TO CLIENT ---
    public static class ResponseDragonsPacket {
        private final List<DragonInfo> dragons;
        private final String radarType;
        
        public ResponseDragonsPacket(List<DragonInfo> dragons, String radarType) { 
            this.dragons = dragons;
            this.radarType = radarType;
        }

        public static void encode(ResponseDragonsPacket packet, FriendlyByteBuf buf) {
            buf.writeInt(packet.dragons.size());
            for (DragonInfo d : packet.dragons) {
                buf.writeInt(d.stage);
                buf.writeUtf(d.type);
                buf.writeBoolean(d.isMale);
                buf.writeDouble(d.x);
                buf.writeDouble(d.y);
                buf.writeDouble(d.z);
            }
            buf.writeUtf(packet.radarType);
        }

        public static ResponseDragonsPacket decode(FriendlyByteBuf buf) {
            int count = buf.readInt();
            List<DragonInfo> list = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                int stage = buf.readInt();
                String type = buf.readUtf();
                boolean isMale = buf.readBoolean();
                double x = buf.readDouble();
                double y = buf.readDouble();
                double z = buf.readDouble();
                list.add(new DragonInfo(stage, type, isMale, x, y, z));
            }
            String radarType = buf.readUtf();
            return new ResponseDragonsPacket(list, radarType);
        }

        public static void handle(ResponseDragonsPacket packet, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                // Route to correct radar based on type
                switch (packet.radarType) {
                    case "master":
                        MasterRadarSettings.INSTANCE.globalResults.clear();
                        MasterRadarSettings.INSTANCE.globalResults.addAll(packet.dragons);
                        MasterRadarSettings.INSTANCE.hasPerformedSearch = true;
                        break;
                    case "fire":
                        FireRadarSettings.INSTANCE.globalResults.clear();
                        FireRadarSettings.INSTANCE.globalResults.addAll(packet.dragons);
                        FireRadarSettings.INSTANCE.hasPerformedSearch = true;
                        break;
                    case "ice":
                        IceRadarSettings.INSTANCE.globalResults.clear();
                        IceRadarSettings.INSTANCE.globalResults.addAll(packet.dragons);
                        IceRadarSettings.INSTANCE.hasPerformedSearch = true;
                        break;
                    case "lightning":
                        LightningRadarSettings.INSTANCE.globalResults.clear();
                        LightningRadarSettings.INSTANCE.globalResults.addAll(packet.dragons);
                        LightningRadarSettings.INSTANCE.hasPerformedSearch = true;
                        break;
                    case "all":
                    case "dormant":
                        com.dragon.dragonmod.client.gui.dormant.DormantRadarSettings.INSTANCE.globalResults.clear();
                        com.dragon.dragonmod.client.gui.dormant.DormantRadarSettings.INSTANCE.globalResults.addAll(packet.dragons);
                        com.dragon.dragonmod.client.gui.dormant.DormantRadarSettings.INSTANCE.hasPerformedSearch = true;
                        break;
                }
                DragonScanner.isSearchComplete = true;
            });
            ctx.get().setPacketHandled(true);
        }
    }

    public static void requestServerSearch(double radius, String radarType) {
        isSearchComplete = false;
        CHANNEL.sendToServer(new RequestDragonsPacket(radius, radarType));
    }

    // -----TELEPORT PACKETS-----
    public static class TeleportRequestPacket {
        private final double x, y, z; 
       
        public TeleportRequestPacket(double x, double y, double z){
            this.x=x;
            this.y=y;
            this.z=z;
        }

        public static void encode(TeleportRequestPacket msg, FriendlyByteBuf buf){
            buf.writeDouble(msg.x);
            buf.writeDouble(msg.y);
            buf.writeDouble(msg.z);
        }

        public static TeleportRequestPacket decode(FriendlyByteBuf buf){  
            return new TeleportRequestPacket(buf.readDouble(), buf.readDouble(), buf.readDouble());
        }

        public static void handle(TeleportRequestPacket msg, Supplier<NetworkEvent.Context>ctx){
            ctx.get().enqueueWork(()->{
                ServerPlayer player = ctx.get().getSender();
                if (player != null && player.isCreative()) {
                    player.teleportTo(msg.x, msg.y, msg.z);
                }
            });
            ctx.get().setPacketHandled(true);
        }
    }
}