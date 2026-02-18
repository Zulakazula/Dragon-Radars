package com.dragon.dragonmod.client.gui.fire;

import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class FireRadarAngle {
    private final FireRadarAngleState state;

    public FireRadarAngle() {
        this.state = new FireRadarAngleState();
    }

    public float getAngle(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity, int seed) {
        return state.get(stack, level, entity, seed);
    }
}