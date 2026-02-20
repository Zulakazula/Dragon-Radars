package com.dragon.dragonmod.client.gui.dormant;

import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class DormantRadarAngle {
    private final DormantRadarAngleState state;

    public DormantRadarAngle() {
        this.state = new DormantRadarAngleState();
    }

    public float getAngle(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity, int seed) {
        return state.get(stack, level, entity, seed);
    }
}