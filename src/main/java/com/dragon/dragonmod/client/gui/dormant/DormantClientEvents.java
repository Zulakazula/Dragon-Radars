package com.dragon.dragonmod.client.gui.dormant;

import com.dragon.dragonmod.DragonRadarMod;
import com.dragon.dragonmod.ModItems;
import com.dragon.dragonmod.items.ItemDormantRadar;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = DragonRadarMod.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DormantClientEvents {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            // Register HUD
            MinecraftForge.EVENT_BUS.register(new DormantRadarHud());
            
            // Register item properties
            registerDormantRadarProperties();
            
            System.out.println("Dragon Radar: Dormant Radar registered!");
        });
    }
    
    private static void registerDormantRadarProperties() {
        DormantRadarAngle dormantAngle = new DormantRadarAngle();
        
        // Spinning needle
        ItemProperties.register(ModItems.DORMANT_RADAR.get(),
            new ResourceLocation(DragonRadarMod.MODID, "angle"),
            (stack, level, entity, seed) -> dormantAngle.getAngle(stack, level, entity, seed)
        );
        
        // Fire state (0 = not merged, 1 = active, 2 = purified)
        ItemProperties.register(ModItems.DORMANT_RADAR.get(),
            new ResourceLocation(DragonRadarMod.MODID, "fire_state"),
            (stack, level, entity, seed) -> {
                if (ItemDormantRadar.hasKilledFire(stack)) return 2.0f;
                if (ItemDormantRadar.hasMergedFire(stack)) return 1.0f;
                return 0.0f;
            }
        );
        
        // Ice state
        ItemProperties.register(ModItems.DORMANT_RADAR.get(),
            new ResourceLocation(DragonRadarMod.MODID, "ice_state"),
            (stack, level, entity, seed) -> {
                if (ItemDormantRadar.hasKilledIce(stack)) return 2.0f;
                if (ItemDormantRadar.hasMergedIce(stack)) return 1.0f;
                return 0.0f;
            }
        );
        
        // Lightning state
        ItemProperties.register(ModItems.DORMANT_RADAR.get(),
            new ResourceLocation(DragonRadarMod.MODID, "lightning_state"),
            (stack, level, entity, seed) -> {
                if (ItemDormantRadar.hasKilledLightning(stack)) return 2.0f;
                if (ItemDormantRadar.hasMergedLightning(stack)) return 1.0f;
                return 0.0f;
            }
        );
    }

    @Mod.EventBusSubscriber(modid = DragonRadarMod.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ForgeEvents {
        private static int tickCounter = 0;
        
        @SubscribeEvent
        public static void onClientTick(TickEvent.ClientTickEvent event) {
            if (event.phase != TickEvent.Phase.END) return;
            if (tickCounter++ % 10 != 0) return;

            Minecraft mc = Minecraft.getInstance();
            Player player = mc.player;

            if (player == null || DormantRadarScreen.currentlyTrackedDormant == null) return;

            boolean hasRadar = false;
            boolean radarIsComplete = false;

            for (ItemStack stack : player.getInventory().items) {
                if (stack.is(ModItems.DORMANT_RADAR.get())) {
                    hasRadar = true;
                    if (ItemDormantRadar.isComplete(stack)) {
                        radarIsComplete = true;
                    }
                    break;
                }
            }

            if (!hasRadar) {
                for (ItemStack stack : player.getInventory().offhand) {
                    if (stack.is(ModItems.DORMANT_RADAR.get())) {
                        hasRadar = true;
                        if (ItemDormantRadar.isComplete(stack)) {
                            radarIsComplete = true;
                        }
                        break;
                    }
                }
            }

            // Stop tracking if radar is removed OR if it's now complete
            if (!hasRadar) {
                DormantRadarScreen.currentlyTrackedDormant = null;
                player.displayClientMessage(
                    Component.literal("§cRadar Signal Lost: Dormant Radar removed from inventory."),
                    true
                );
            } else if (radarIsComplete) {
                DormantRadarScreen.currentlyTrackedDormant = null;
                player.displayClientMessage(
                    Component.literal("§6Tracking stopped: Dormant Radar fully purified!"),
                    true
                );
            }
        }
    }
}