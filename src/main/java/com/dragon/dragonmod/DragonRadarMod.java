package com.dragon.dragonmod;

import com.dragon.dragonmod.client.DragonScanner;
import com.dragon.dragonmod.items.ItemDormantRadar;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(DragonRadarMod.MODID)
public class DragonRadarMod {

    public static final String MODID = "dragonradarmod";

    public DragonRadarMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register the Items
        ModItems.register(modEventBus);

        // Register your Custom Creative Tab 
        ModCreativeTabs.register(modEventBus);

        // Add items to vanilla creative tabs
        modEventBus.addListener(this::addCreativeItems);
        
        // Setup network packets
        modEventBus.addListener(this::commonSetup);
        
        // Register anvil recipe handler
        MinecraftForge.EVENT_BUS.register(ItemDormantRadar.class);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            DragonScanner.registerPackets();
            System.out.println("Dragon Radar: Network packets registered!");
        });
    }

    private void addCreativeItems(BuildCreativeModeTabContentsEvent event) {
        // Adding the items to the Tools & Utilities tab
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            
            event.accept(ModItems.DORMANT_RADAR.get()); 
            event.accept(ModItems.FIRE_RADAR.get());
            event.accept(ModItems.ICE_RADAR.get());
            event.accept(ModItems.LIGHTNING_RADAR.get());
            event.accept(ModItems.DRAGON_RADAR.get());
        }
    }
}