package com.dragon.dragonmod.client.gui.master;

import com.dragon.dragonmod.client.ClientRadarState;
import com.dragon.dragonmod.client.DragonInfo;
import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.util.Mth;

public class MasterRadarAngleState {
    private final NeedleWobbler wobbler = new NeedleWobbler(0.15f);

    public float get(ItemStack stack, @Nullable ClientLevel level, @Nullable Entity entity, int seed) {
        if (level == null || entity == null) return 0f;

        String targetID = ClientRadarState.currentlyTrackedMaster;
        DragonInfo targetData = null;
        
        if (targetID != null) {
            for (DragonInfo d : MasterRadarSettings.INSTANCE.globalResults) {
                String dragonID = d.type + "_S" + d.stage + "_X" + (int)d.x + "_Z" + (int)d.z;
                if (targetID.equals(dragonID)) {
                    targetData = d;
                    break;
                }
            }
        }

        float targetRotation;

        if (targetData != null) {
            double dx = targetData.x - entity.getX();
            double dz = targetData.z - entity.getZ();
            double horizontalDistance = Math.sqrt(dx * dx + dz * dz);
            
            if (horizontalDistance < 5.0) {
                targetRotation = 1.0f - (float)(level.getGameTime() % 40L) / 40.0f;
            } else {
                double angleToTarget = Math.atan2(dz, dx);
                double playerYawRadians = Math.toRadians(entity.getYRot() + 90.0f);
                double relativeAngle = angleToTarget - playerYawRadians;
                targetRotation = (float)(relativeAngle / (2 * Math.PI) + 0.5);
                targetRotation = Mth.positiveModulo(targetRotation, 1.0f);
            }
        } else {
            targetRotation = (float)(level.getGameTime() % 40L) / 40.0f;
        }

        long time = level.getGameTime();
        if (wobbler.shouldUpdate(time)) {
            wobbler.update(time, targetRotation);
        }

        return Mth.positiveModulo(wobbler.getRotation(), 1.0f);
    }

    private static class NeedleWobbler {
        private final float smoothing;
        private float rotation = 0f;
        private long lastUpdate = -1L;

        public NeedleWobbler(float smoothing) { 
            this.smoothing = smoothing; 
        }
        
        public boolean shouldUpdate(long time) { 
            return time != this.lastUpdate; 
        }
        
        public void update(long time, float targetOffset) {
            this.lastUpdate = time;
            float delta = targetOffset - this.rotation;
            
            if (delta > 0.5f) delta -= 1.0f;
            if (delta < -0.5f) delta += 1.0f;
            
            this.rotation += delta * this.smoothing;
            this.rotation = Mth.positiveModulo(this.rotation, 1.0f);
        }
        
        public float getRotation() { 
            return this.rotation; 
        }
    }
}