package com.dragon.dragonmod.client.gui.lightning;

import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class LightningRadarAngle {
    private final LightningRadarAngleState state;

    public LightningRadarAngle() {
        this.state = new LightningRadarAngleState();
    }

    public float getAngle(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity, int seed) {
        return state.get(stack, level, entity, seed);
    }
}