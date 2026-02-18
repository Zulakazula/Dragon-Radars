package com.dragon.dragonmod.client.gui.ice;

import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class IceRadarAngle {
    private final IceRadarAngleState state;

    public IceRadarAngle() {
        this.state = new IceRadarAngleState();
    }

    public float getAngle(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity, int seed) {
        return state.get(stack, level, entity, seed);
    }
}