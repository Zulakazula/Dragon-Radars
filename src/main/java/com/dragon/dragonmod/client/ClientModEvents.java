package com.dragon.dragonmod.client;

import com.dragon.dragonmod.DragonRadarMod;
import com.dragon.dragonmod.ModItems;
import com.dragon.dragonmod.client.gui.master.MasterRadarAngle;
import com.dragon.dragonmod.client.gui.master.MasterRadarHud;
import com.dragon.dragonmod.client.gui.fire.FireRadarAngle;
import com.dragon.dragonmod.client.gui.fire.FireRadarHud;
import com.dragon.dragonmod.client.gui.ice.IceRadarAngle;
import com.dragon.dragonmod.client.gui.ice.IceRadarHud;
import com.dragon.dragonmod.client.gui.lightning.LightningRadarAngle;
import com.dragon.dragonmod.client.gui.lightning.LightningRadarHud;
import com.dragon.dragonmod.items.ItemDormantRadar;

import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = DragonRadarMod.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientModEvents {

    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            // --- EXISTING RADARS ---

            // Master Radar
            MasterRadarAngle masterAngle = new MasterRadarAngle();
            ItemProperties.register(ModItems.DRAGON_RADAR.get(),
                    new ResourceLocation(DragonRadarMod.MODID, "angle"),
                    (stack, level, entity, seed) -> masterAngle.getAngle(stack, level, entity, seed));

            // Fire Radar
            FireRadarAngle fireAngle = new FireRadarAngle();
            ItemProperties.register(ModItems.FIRE_RADAR.get(),
                    new ResourceLocation(DragonRadarMod.MODID, "angle"),
                    (stack, level, entity, seed) -> fireAngle.getAngle(stack, level, entity, seed));

            // Ice Radar
            IceRadarAngle iceAngle = new IceRadarAngle();
            ItemProperties.register(ModItems.ICE_RADAR.get(),
                    new ResourceLocation(DragonRadarMod.MODID, "angle"),
                    (stack, level, entity, seed) -> iceAngle.getAngle(stack, level, entity, seed));

            // Lightning Radar
            LightningRadarAngle lightningAngle = new LightningRadarAngle();
            ItemProperties.register(ModItems.LIGHTNING_RADAR.get(),
                    new ResourceLocation(DragonRadarMod.MODID, "angle"),
                    (stack, level, entity, seed) -> lightningAngle.getAngle(stack, level, entity, seed));

            // --- DORMANT RADAR LAYER PREDICATES ---

            // Fire: 0 = not merged, 1 = merged (active), 2 = purified
            ItemProperties.register(ModItems.DORMANT_RADAR.get(),
                    new ResourceLocation(DragonRadarMod.MODID, "fire_state"),
                    (stack, level, entity, seed) -> {
                        if (ItemDormantRadar.isAllMerged(stack) && ItemDormantRadar.hasKilledFire(stack)) return 2.0f;
                        if (ItemDormantRadar.hasMergedFire(stack)) return 1.0f;
                        return 0.0f;
                    });

            // Ice: 0 = not merged, 1 = merged (active), 2 = purified
            ItemProperties.register(ModItems.DORMANT_RADAR.get(),
                    new ResourceLocation(DragonRadarMod.MODID, "ice_state"),
                    (stack, level, entity, seed) -> {
                        if (ItemDormantRadar.isAllMerged(stack) && ItemDormantRadar.hasKilledIce(stack)) return 2.0f;
                        if (ItemDormantRadar.hasMergedIce(stack)) return 1.0f;
                        return 0.0f;
                    });

            // Lightning: 0 = not merged, 1 = merged (active), 2 = purified
            ItemProperties.register(ModItems.DORMANT_RADAR.get(),
                    new ResourceLocation(DragonRadarMod.MODID, "lightning_state"),
                    (stack, level, entity, seed) -> {
                        if (ItemDormantRadar.isAllMerged(stack) && ItemDormantRadar.hasKilledLightning(stack)) return 2.0f;
                        if (ItemDormantRadar.hasMergedLightning(stack)) return 1.0f;
                        return 0.0f;
                    });
        });

        // Register HUDs to Forge Event Bus
        MinecraftForge.EVENT_BUS.register(new MasterRadarHud());
        MinecraftForge.EVENT_BUS.register(new FireRadarHud());
        MinecraftForge.EVENT_BUS.register(new IceRadarHud());
        MinecraftForge.EVENT_BUS.register(new LightningRadarHud());
    }
}