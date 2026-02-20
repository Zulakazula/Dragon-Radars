package com.dragon.dragonmod;

import com.dragon.dragonmod.items.ItemDormantRadar;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = DragonRadarMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class AnvilRecipeHandler {

    @SubscribeEvent
    public static void onAnvilUpdate(AnvilUpdateEvent event) {
        ItemStack left = event.getLeft();
        ItemStack right = event.getRight();
        
        // Merge Fire Radar into Dormant
        if (left.is(ModItems.DORMANT_RADAR.get()) && right.is(ModItems.FIRE_RADAR.get())) {
            ItemStack result = left.copy();
            ItemDormantRadar.setMergedFire(result);
            event.setOutput(result);
            event.setCost(1);
            event.setMaterialCost(1);
        }
        
        // Merge Ice Radar into Dormant
        if (left.is(ModItems.DORMANT_RADAR.get()) && right.is(ModItems.ICE_RADAR.get())) {
            ItemStack result = left.copy();
            ItemDormantRadar.setMergedIce(result);
            event.setOutput(result);
            event.setCost(1);
            event.setMaterialCost(1);
        }
        
        // Merge Lightning Radar into Dormant
        if (left.is(ModItems.DORMANT_RADAR.get()) && right.is(ModItems.LIGHTNING_RADAR.get())) {
            ItemStack result = left.copy();
            ItemDormantRadar.setMergedLightning(result);
            event.setOutput(result);
            event.setCost(1);
            event.setMaterialCost(1);
        }
        
        // Transform fully purified Dormant Radar + Emerald = Master Radar
        if (left.is(ModItems.DORMANT_RADAR.get()) && right.is(Items.EMERALD)) {
            if (ItemDormantRadar.isComplete(left)) {
                ItemStack result = new ItemStack(ModItems.DRAGON_RADAR.get());
                event.setOutput(result);
                event.setCost(5);
                event.setMaterialCost(1);
            }
        }
        
        // Also check reverse order
        if (left.is(Items.EMERALD) && right.is(ModItems.DORMANT_RADAR.get())) {
            if (ItemDormantRadar.isComplete(right)) {
                ItemStack result = new ItemStack(ModItems.DRAGON_RADAR.get());
                event.setOutput(result);
                event.setCost(5);
                event.setMaterialCost(1);
            }
        }
    }
}   