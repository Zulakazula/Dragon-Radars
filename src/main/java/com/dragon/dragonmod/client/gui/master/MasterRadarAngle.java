package com.dragon.dragonmod.client.gui.master;

import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class MasterRadarAngle {

    private final MasterRadarAngleState state;

    public MasterRadarAngle() {
        this.state = new MasterRadarAngleState();
    }

    public float getAngle(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity, int seed) {
        return state.get(stack, level, entity, seed);
    }
}