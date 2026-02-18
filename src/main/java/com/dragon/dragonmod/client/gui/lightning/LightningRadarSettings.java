package com.dragon.dragonmod.client.gui.lightning;

import com.dragon.dragonmod.client.DragonInfo;
import java.util.ArrayList;
import java.util.List;

public class LightningRadarSettings {
    public static final LightningRadarSettings INSTANCE = new LightningRadarSettings();
    
    public int searchRadius = 7500;
    public boolean hasPerformedSearch = false;
    public boolean sortByDistance = false;
    public boolean sortClosest = true; 
    public boolean sortHighToLow = true;
    public List<String> selectedDragons = new ArrayList<>();
    public List<String> selectedStages = new ArrayList<>();
    public List<String> selectedGenders = new ArrayList<>();
    public List<DragonInfo> globalResults = new ArrayList<>();

    private LightningRadarSettings() {}

    public void resetToDefaults() {
        selectedDragons.clear();
        selectedStages.clear();
        selectedGenders.clear();
        globalResults.clear();
        searchRadius = 7500;
        sortHighToLow = true;
        sortByDistance = false;
        sortClosest = true;
        hasPerformedSearch = false;
    }
}