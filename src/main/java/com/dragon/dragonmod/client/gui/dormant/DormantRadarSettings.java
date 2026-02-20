package com.dragon.dragonmod.client.gui.dormant;

import com.dragon.dragonmod.client.DragonInfo;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DormantRadarSettings {
    public static final DormantRadarSettings INSTANCE = new DormantRadarSettings();
    
    public List<DragonInfo> globalResults = new ArrayList<>();
    public boolean hasPerformedSearch = false;
    
    // Only Stage 4-5 available
    public List<String> selectedStages = new ArrayList<>(Arrays.asList("Stage 4", "Stage 5"));
    
    public int searchRadius = 7500;
    public boolean sortClosest = true;
}