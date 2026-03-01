package com.dragon.dragonmod.client.gui.master;

import com.dragon.dragonmod.client.ClientRadarState;
import com.dragon.dragonmod.client.DragonInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class MasterRadarHud {

    private static final int COLOR_LIGHTNING = 0xFF9933FF; 
    private static final int COLOR_ICE = 0xFF0023FF;       
    private static final int COLOR_FIRE = 0xFFFF0000;

    @SubscribeEvent
    public void onRender(RenderGuiEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        
        if (player == null || mc.options.hideGui) return;
        
        GuiGraphics gui = event.getGuiGraphics();
        String targetID = ClientRadarState.currentlyTrackedMaster;
        
        if (targetID == null) return;
        
        DragonInfo target = null;
        for (DragonInfo d : MasterRadarSettings.INSTANCE.globalResults) {
            String dragonID = d.type + "_S" + d.stage + "_X" + (int)d.x + "_Z" + (int)d.z;
            if (targetID.equals(dragonID)) {
                target = d;
                break;
            }
        }
        
        if (target != null) {
            double dx = target.x - player.getX();
            double dy = target.y - player.getY();
            double dz = target.z - player.getZ();
            int dist = (int) Math.sqrt(dx * dx + dy * dy + dz * dz);
            
            // Determine outline color based on dragon type
            int outlineColor;
            if (target.type.contains("Lightning")) {
                outlineColor = COLOR_LIGHTNING;
            } else if (target.type.contains("Ice")) {
                outlineColor = COLOR_ICE;
            } else {
                outlineColor = COLOR_FIRE;
            }
            
            int x = 10;
            int y = 10;
            
            gui.fill(x - 2, y - 2, x + 160, y + 42, 0x99000000);
            gui.renderOutline(x - 2, y - 2, 162, 44, outlineColor);  // Colored!
            
            String nameLine = target.type + " (Stage " + target.stage + ")";
            String cordLine = String.format("X: %d Y: %d Z: %d", (int)target.x, (int)target.y, (int)target.z);
            String distLine = dist + " blocks away";
            
            gui.drawString(mc.font, nameLine, x + 4, y + 4, 0xFFFFFF);
            gui.drawString(mc.font, cordLine, x + 4, y + 16, 0xAAAAAA);
            gui.drawString(mc.font, distLine, x + 4, y + 28, 0xFFFF55);
        }
    }
}