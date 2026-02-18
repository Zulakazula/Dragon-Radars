package com.dragon.dragonmod.client.gui.master;

import com.dragon.dragonmod.client.DragonInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class MasterRadarHud {

    @SubscribeEvent
    public void onRender(RenderGuiEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        
        if (player == null || mc.options.hideGui) return;
        
        GuiGraphics gui = event.getGuiGraphics();
        String targetID = MasterRadarScreen.currentlyTrackedMaster;
        
        if (targetID == null) return;
        
        // Find the tracked dragon by ID
        DragonInfo target = null;
        for (DragonInfo d : MasterRadarSettings.INSTANCE.globalResults) {
            String dragonID = d.type + "_S" + d.stage;
            if (targetID.startsWith(dragonID)) {
                target = d;
                break;
            }
        }
        
        if (target != null) {
            double dx = target.x - player.getX();
            double dy = target.y - player.getY();
            double dz = target.z - player.getZ();
            int dist = (int) Math.sqrt(dx * dx + dy * dy + dz * dz);
            
            int x = 10;
            int y = 10;
            
            // Background box (black)
            gui.fill(x - 2, y - 2, x + 160, y + 42, 0x99000000);
            // Green outline
            gui.renderOutline(x - 2, y - 2, 162, 44, 0xFF00FF00);
            
            String nameLine = target.type + " (Stage " + target.stage + ")";
            String cordLine = String.format("X: %d Y: %d Z: %d", (int)target.x, (int)target.y, (int)target.z);
            String distLine = dist + " blocks away";
            
            gui.drawString(mc.font, nameLine, x + 4, y + 4, 0xFFFFFF);
            gui.drawString(mc.font, cordLine, x + 4, y + 16, 0xAAAAAA);
            gui.drawString(mc.font, distLine, x + 4, y + 28, 0xFFFF55);
        }
    }
}