package com.dragon.dragonmod;

import com.dragon.dragonmod.items.ItemDormantRadar;
import com.github.alexthe666.iceandfire.entity.EntityDragonBase;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = DragonRadarMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class DragonKillHandler {

    @SubscribeEvent
    public static void onEntityDeath(LivingDeathEvent event) {
        // Check if killer is a player
        if (!(event.getSource().getEntity() instanceof Player player)) return;
        
        // Check if entity is a dragon
        if (!(event.getEntity() instanceof EntityDragonBase dragon)) return;
        
        // Check if dragon is Stage 4 or 5
        int stage = dragon.getDragonStage();
        if (stage < 4) return;
        
        // Check player's offhand for Dormant Radar
        ItemStack offhandItem = player.getOffhandItem();
        if (!offhandItem.is(ModItems.DORMANT_RADAR.get())) return;
        if (!ItemDormantRadar.isAllMerged(offhandItem)) return;
        
        // Determine dragon type and mark as purified
        String dragonType = dragon.getClass().getSimpleName();
        
        if (dragonType.contains("Fire")) {
            if (!ItemDormantRadar.hasKilledFire(offhandItem)) {
                ItemDormantRadar.setKilledFire(offhandItem);
                
                // Always show soul absorbed in action bar
                player.displayClientMessage(
                    net.minecraft.network.chat.Component.literal("§6✦ Fire Dragon Soul Absorbed! ✦")
                        .withStyle(net.minecraft.ChatFormatting.BOLD),
                    true  // ACTION BAR
                );
                
                // Check if this was the final dragon - show completion messages in BOTH
                if (ItemDormantRadar.isComplete(offhandItem)) {
                    player.displayClientMessage(
                        net.minecraft.network.chat.Component.literal("§6✦ RADAR FULLY PURIFIED! ✦")
                            .withStyle(net.minecraft.ChatFormatting.BOLD),
                        true  // ACTION BAR
                    );
                    player.displayClientMessage(
                        net.minecraft.network.chat.Component.literal("§6✦ RADAR FULLY PURIFIED! ✦")
                            .withStyle(net.minecraft.ChatFormatting.BOLD),
                        false  // CHAT
                    );
                    player.displayClientMessage(
                        net.minecraft.network.chat.Component.literal("§7Check tooltip for further instructions.")
                            .withStyle(net.minecraft.ChatFormatting.ITALIC),
                        false  // CHAT
                    );
                }
            }
        } else if (dragonType.contains("Ice")) {
            if (!ItemDormantRadar.hasKilledIce(offhandItem)) {
                ItemDormantRadar.setKilledIce(offhandItem);
                
                // Always show soul absorbed in action bar
                player.displayClientMessage(
                    net.minecraft.network.chat.Component.literal("§b✦ Ice Dragon Soul Absorbed! ✦")
                        .withStyle(net.minecraft.ChatFormatting.BOLD),
                    true  // ACTION BAR
                );
                
                // Check if this was the final dragon - show completion messages in BOTH
                if (ItemDormantRadar.isComplete(offhandItem)) {
                    player.displayClientMessage(
                        net.minecraft.network.chat.Component.literal("§6✦ RADAR FULLY PURIFIED! ✦")
                            .withStyle(net.minecraft.ChatFormatting.BOLD),
                        true  // ACTION BAR
                    );
                    player.displayClientMessage(
                        net.minecraft.network.chat.Component.literal("§6✦ RADAR FULLY PURIFIED! ✦")
                            .withStyle(net.minecraft.ChatFormatting.BOLD),
                        false  // CHAT
                    );
                    player.displayClientMessage(
                        net.minecraft.network.chat.Component.literal("§7Check tooltip for further instructions.")
                            .withStyle(net.minecraft.ChatFormatting.ITALIC),
                        false  // CHAT
                    );
                }
            }
        } else if (dragonType.contains("Lightning")) {
            if (!ItemDormantRadar.hasKilledLightning(offhandItem)) {
                ItemDormantRadar.setKilledLightning(offhandItem);
                
                // Always show soul absorbed in action bar
                player.displayClientMessage(
                    net.minecraft.network.chat.Component.literal("§d✦ Lightning Dragon Soul Absorbed! ✦")
                        .withStyle(net.minecraft.ChatFormatting.BOLD),
                    true  // ACTION BAR
                );
                
                // Check if this was the final dragon - show completion messages in BOTH
                if (ItemDormantRadar.isComplete(offhandItem)) {
                    player.displayClientMessage(
                        net.minecraft.network.chat.Component.literal("§6✦ RADAR FULLY PURIFIED! ✦")
                            .withStyle(net.minecraft.ChatFormatting.BOLD),
                        true  // ACTION BAR
                    );
                    player.displayClientMessage(
                        net.minecraft.network.chat.Component.literal("§6✦ RADAR FULLY PURIFIED! ✦")
                            .withStyle(net.minecraft.ChatFormatting.BOLD),
                        false  // CHAT
                    );
                    player.displayClientMessage(
                        net.minecraft.network.chat.Component.literal("§7Check tooltip for further instructions.")
                            .withStyle(net.minecraft.ChatFormatting.ITALIC),
                        false  // CHAT
                    );
                }
            }
        }
    }
}