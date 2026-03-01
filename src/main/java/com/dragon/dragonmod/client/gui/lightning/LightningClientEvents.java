package com.dragon.dragonmod.client.gui.lightning;

import com.dragon.dragonmod.DragonRadarMod;
import com.dragon.dragonmod.ModItems;
import com.dragon.dragonmod.client.ClientRadarState;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = DragonRadarMod.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class LightningClientEvents {

    private static int tickCounter = 0;

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (tickCounter++ % 10 != 0) return;

        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;

        if (player == null || ClientRadarState.currentlyTrackedLightning == null) return;

        boolean hasRadar = false;

        for (ItemStack stack : player.getInventory().items) {
            if (stack.is(ModItems.LIGHTNING_RADAR.get())) {
                hasRadar = true;
                break;
            }
        }

        if (!hasRadar) {
            for (ItemStack stack : player.getInventory().offhand) {
                if (stack.is(ModItems.LIGHTNING_RADAR.get())) {
                    hasRadar = true;
                    break;
                }
            }
        }

        if (!hasRadar) {
            ClientRadarState.currentlyTrackedLightning = null;
            player.displayClientMessage(
                Component.literal("§cRadar Signal Lost: Lightning Radar removed from inventory."),
                true
            );
        }
    }
}