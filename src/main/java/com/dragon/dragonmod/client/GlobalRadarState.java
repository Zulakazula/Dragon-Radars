package com.dragon.dragonmod.client;

public class GlobalRadarState {
    private static String currentRadarType = null;
    private static String currentTargetID = null;

    public static void startTracking(String radarType, String targetID) {
        currentRadarType = radarType;
        currentTargetID = targetID;
    }

    public static void stopTracking() {
        currentRadarType = null;
        currentTargetID = null;
    }

    public static boolean isTracking() {
        return currentRadarType != null;
    }

    public static boolean isTrackingWith(String radarType) {
        return radarType.equals(currentRadarType);
    }

    public static String getCurrentRadarType() {
        return currentRadarType;
    }

    public static String getCurrentTargetID() {
        return currentTargetID;
    }
}