package com.dragon.dragonmod.client;

import net.minecraft.world.entity.Entity;
import net.minecraft.core.BlockPos;

public class DragonInfo {
    public final String type;      // Changed from 'name' to 'type' to match Radar logic
    public final double distance;
    public final int stage;
    public final boolean isMale;   // The missing field fix
    public final BlockPos pos;
    public final double x, y, z;   // Added for precise teleporting/networking
    public final Entity entity;    // Keep reference for local use if needed

    /**
     * Standard constructor for local entity scanning
     */
    public DragonInfo(Entity entity, double distance, int stage, boolean isMale) {
        this.entity = entity;
        this.type = entity.getDisplayName().getString();
        this.distance = distance;
        this.stage = stage;
        this.isMale = isMale;
        this.pos = entity.blockPosition();
        this.x = entity.getX();
        this.y = entity.getY();
        this.z = entity.getZ();
    }

    /**
     * Overloaded constructor for networking (where the Entity object might not exist on the client)
     */
    public DragonInfo(int stage, String type, boolean isMale, double x, double y, double z) {
        this.entity = null;
        this.type = type;
        this.stage = stage;
        this.isMale = isMale;
        this.x = x;
        this.y = y;
        this.z = z;
        this.pos = new BlockPos((int)x, (int)y, (int)z);
        this.distance = 0; // Calculated by the GUI based on player position
    }
}