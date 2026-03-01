package com.dragon.dragonmod.client;

import com.dragon.dragonmod.client.gui.dormant.DormantRadarScreen;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClientHelper {
    
    public static void openDormantRadarScreen() {
        Minecraft.getInstance().setScreen(new DormantRadarScreen());
    }
}