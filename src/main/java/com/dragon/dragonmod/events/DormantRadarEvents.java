package com.dragon.dragonmod.events;

import com.dragon.dragonmod.DragonRadarMod;
import com.dragon.dragonmod.ModItems;
import com.dragon.dragonmod.items.ItemDormantRadar;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = DragonRadarMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class DormantRadarEvents {

    // -------------------------------------------------------
    // ANVIL MERGING
    // -------------------------------------------------------
    @SubscribeEvent
    public static void onAnvilUpdate(AnvilUpdateEvent event) {
        ItemStack left = event.getLeft();
        ItemStack right = event.getRight();

        // Left slot must be dormant radar
        if (!left.is(ModItems.DORMANT_RADAR.get())) return;

        boolean isFire = right.is(ModItems.FIRE_RADAR.get());
        boolean isIce = right.is(ModItems.ICE_RADAR.get());
        boolean isLightning = right.is(ModItems.LIGHTNING_RADAR.get());

        // Right slot must be one of the radars
        if (!isFire && !isIce && !isLightning) return;

        // Check not already merged
        if (isFire && ItemDormantRadar.hasMergedFire(left)) return;
        if (isIce && ItemDormantRadar.hasMergedIce(left)) return;
        if (isLightning && ItemDormantRadar.hasMergedLightning(left)) return;

        // Build output - copy dormant radar with new merge flag set
        ItemStack output = left.copy();

        if (isFire) ItemDormantRadar.setMergedFire(output);
        if (isIce) ItemDormantRadar.setMergedIce(output);
        if (isLightning) ItemDormantRadar.setMergedLightning(output);

        // Set anvil result
        event.setOutput(output);
        event.setCost(5);
    }

    // -------------------------------------------------------
    // DRAGON KILL PURIFICATION
    // -------------------------------------------------------
    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        // Must be killed by a player
        if (!(event.getSource().getEntity() instanceof Player player)) return;

        LivingEntity killed = event.getEntity();
        String entityType = killed.getType().getDescriptionId();

        // Check dragon type
        boolean isFire = entityType.contains("fire_dragon");
        boolean isIce = entityType.contains("ice_dragon");
        boolean isLightning = entityType.contains("lightning_dragon");

        if (!isFire && !isIce && !isLightning) return;

        // Get AgeTicks from entity NBT and calculate stage
        CompoundTag nbt = new CompoundTag();
        killed.saveWithoutId(nbt);

        if (!nbt.contains("AgeTicks")) return;
        int ageTicks = nbt.getInt("AgeTicks");
        int stage = calculateStage(ageTicks);

        // Must be stage 4 or 5
        if (stage < 4) return;

        // Check dormant radar is in offhand
        ItemStack offhand = player.getOffhandItem();
        if (!offhand.is(ModItems.DORMANT_RADAR.get())) return;

        // Must have all 3 radars merged first
        if (!ItemDormantRadar.isAllMerged(offhand)) {
            player.displayClientMessage(
                Component.literal("§6[Dormant Radar] §cMerge all three radars first!"),
                true
            );
            return;
        }

        // Check if this dragon type already killed
        if (isFire && ItemDormantRadar.hasKilledFire(offhand)) return;
        if (isIce && ItemDormantRadar.hasKilledIce(offhand)) return;
        if (isLightning && ItemDormantRadar.hasKilledLightning(offhand)) return;

        // Mark the kill and notify
        String dragonName = "";
        if (isFire) { ItemDormantRadar.setKilledFire(offhand); dragonName = "Fire"; }
        if (isIce) { ItemDormantRadar.setKilledIce(offhand); dragonName = "Ice"; }
        if (isLightning) { ItemDormantRadar.setKilledLightning(offhand); dragonName = "Lightning"; }

        player.displayClientMessage(
            Component.literal("§6[Dormant Radar] §a" + dragonName + " Dragon §7soul absorbed!"),
            true
        );

        // Check if fully complete - transform into Master Radar
        if (ItemDormantRadar.isComplete(offhand)) {
            ItemStack masterRadar = new ItemStack(ModItems.DRAGON_RADAR.get());
            player.getInventory().offhand.set(0, masterRadar);

            player.displayClientMessage(
                Component.literal("§6[Dormant Radar] §eAll souls consumed! §6Master Radar §eunlocked!"),
                false
            );
        }
    }

    // -------------------------------------------------------
    // STAGE CALCULATION (matches ServerDiskDragonScanner)
    // -------------------------------------------------------
    private static int calculateStage(int ageTicks) {
        int days = ageTicks / 24000;
        int stage = (days / 25) + 1;
        return Math.max(1, Math.min(5, stage));
    }
}