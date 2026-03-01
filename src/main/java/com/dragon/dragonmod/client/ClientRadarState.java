package com.dragon.dragonmod.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClientRadarState {
    public static String currentlyTrackedMaster = null;
    public static String currentlyTrackedFire = null;
    public static String currentlyTrackedIce = null;
    public static String currentlyTrackedLightning = null;
    public static String currentlyTrackedDormant = null;
}