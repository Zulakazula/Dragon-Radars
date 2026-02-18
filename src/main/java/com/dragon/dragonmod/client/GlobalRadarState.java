package com.dragon.dragonmod.client;

public class GlobalRadarState {
    public static String activeRadarType = null;
    public static String activeTrackingID = null;
    
    public static void startTracking(String radarType, String dragonID) {
        activeRadarType = radarType;
        activeTrackingID = dragonID;
    }
    
    public static void stopTracking() {
        activeRadarType = null;
        activeTrackingID = null;
    }
    
    public static boolean isTracking() {
        return activeRadarType != null && activeTrackingID != null;
    }
    
    public static boolean isTrackingWith(String radarType) {
        return radarType.equals(activeRadarType);
    }
}