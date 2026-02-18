package com.dragon.dragonmod.client.gui.master;

import com.dragon.dragonmod.client.DragonInfo;
import java.util.ArrayList;
import java.util.List;

public class MasterRadarSettings {
    public static final MasterRadarSettings INSTANCE = new MasterRadarSettings();
    
    // --- SEARCH & FILTER STATE ---
    public int searchRadius = 7500;
    public boolean hasPerformedSearch = false;
    
    // --- SORTING LOGIC ---
    public boolean sortByDistance = false;
    public boolean sortClosest = true; 
    public boolean sortHighToLow = true;

    // --- FILTER LISTS ---
    public List<String> selectedDragons = new ArrayList<>();
    public List<String> selectedStages = new ArrayList<>();
    public List<String> selectedGenders = new ArrayList<>();

    // --- PERSISTENT DATA ---
    public List<DragonInfo> globalResults = new ArrayList<>();

    private MasterRadarSettings() {
        // Private constructor for singleton pattern
    }

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