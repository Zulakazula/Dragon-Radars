package com.dragon.dragonmod;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeTabs {

    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, DragonRadarMod.MODID);

    public static final RegistryObject<CreativeModeTab> DRAGON_TAB =
            TABS.register("dragon_tab",
                    () -> CreativeModeTab.builder()
                            .title(Component.literal("Dragon Radars"))
                            .icon(() -> ModItems.DRAGON_RADAR.get().getDefaultInstance())
                            .displayItems((params, output) -> {
                               
                                output.accept(ModItems.DORMANT_RADAR.get());
                                output.accept(ModItems.FIRE_RADAR.get());
                                output.accept(ModItems.ICE_RADAR.get());
                                output.accept(ModItems.LIGHTNING_RADAR.get());
                                output.accept(ModItems.DRAGON_RADAR.get());
                            })
                            .build()
            );

    public static void register(IEventBus bus) {
        TABS.register(bus);
    }
}