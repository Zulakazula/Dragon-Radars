package com.dragon.dragonmod;

import com.dragon.dragonmod.items.ItemDormantRadar; // Fixed class name
import com.dragon.dragonmod.items.ItemFireRadar;
import com.dragon.dragonmod.items.ItemIceRadar;
import com.dragon.dragonmod.items.ItemLightningRadar;
import com.dragon.dragonmod.items.ItemMasterRadar;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, DragonRadarMod.MODID);

    // Dormant Radar
    public static final RegistryObject<Item> DORMANT_RADAR =
            ITEMS.register("dormant_radar",
                    () -> new ItemDormantRadar(new Item.Properties().stacksTo(1)));

    // Master Radar (Universal)
    public static final RegistryObject<Item> DRAGON_RADAR =
            ITEMS.register("master_radar",
                    () -> new ItemMasterRadar(new Item.Properties()));

    // Fire Radar
    public static final RegistryObject<Item> FIRE_RADAR =
            ITEMS.register("fire_radar",
                    () -> new ItemFireRadar(new Item.Properties()));

    // Ice Radar
    public static final RegistryObject<Item> ICE_RADAR =
            ITEMS.register("ice_radar",
                    () -> new ItemIceRadar(new Item.Properties()));

    // Lightning Radar
    public static final RegistryObject<Item> LIGHTNING_RADAR =
            ITEMS.register("lightning_radar",
                    () -> new ItemLightningRadar(new Item.Properties()));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}