package com.dragon.dragonmod.client.gui.lightning;

import com.dragon.dragonmod.client.DragonInfo;
import com.dragon.dragonmod.client.DragonScanner;
import com.dragon.dragonmod.client.GlobalRadarState;
import com.dragon.dragonmod.client.gui.fire.FireRadarSettings;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class LightningRadarScreen extends Screen {

    private static final int COLOR_LIGHTNING = 0xFF9933FF; 
    private static final int COLOR_ICE = 0xFF0023FF;       
    private static final int COLOR_FIRE = 0xFFFF0000;      

    private final String[] stageNames = {"Stage 1", "Stage 2", "Stage 3", "Stage 4", "Stage 5"};
    private final String[] genderNames = {"Male", "Female"};

    private boolean showStageBox = false;
    private boolean showGenderBox = false;
    
    private String selectedDragonEntry = null; 
    private double scrollAmount = 0;
    private boolean isDraggingScrollBar = false;
    private final int scrollBarWidth = 6;
    
    public static String currentlyTrackedLightning = null; 

    private boolean isWaitingForResults = false;
    private int serverWaitTimer = 0;
    private int resetFeedbackTicks = 0;
    private int refreshFeedbackTicks = 0;
    private TransparentSlider radiusSlider;

    public static class TrackedDragon {
    public String name;
    public int stage;
    public boolean isMale;
    public int distance;
    public String id;
    public double x, y, z;

    public TrackedDragon(DragonInfo info, int dist, int index) {
        this.name = info.type; 
        this.stage = info.stage;
        this.isMale = info.isMale;
        this.distance = dist;
        this.x = info.x;
        this.y = info.y;
        this.z = info.z;
        this.id = this.name + "_S" + this.stage + "_X" + (int)info.x + "_Z" + (int)info.z;
    }
}

    public LightningRadarScreen() {
        super(Component.literal("Lightning Dragon Radar"));
        if (LightningRadarSettings.INSTANCE.searchRadius <= 0 || LightningRadarSettings.INSTANCE.searchRadius > 15000) 
            LightningRadarSettings.INSTANCE.searchRadius = 7500;
        if (LightningRadarSettings.INSTANCE.selectedStages.isEmpty()) 
            LightningRadarSettings.INSTANCE.selectedStages.addAll(Arrays.asList(stageNames));
        if (LightningRadarSettings.INSTANCE.selectedGenders.isEmpty()) 
            LightningRadarSettings.INSTANCE.selectedGenders.addAll(Arrays.asList(genderNames));
    }

    private void performActualSearch() {
        if (this.minecraft.player == null) return;
        LightningRadarSettings.INSTANCE.globalResults.clear();
        LightningRadarSettings.INSTANCE.hasPerformedSearch = false; 
        DragonScanner.isSearchComplete = false;
        DragonScanner.requestServerSearch(LightningRadarSettings.INSTANCE.searchRadius, "lightning");
        this.isWaitingForResults = true;
        this.serverWaitTimer = 340; 
        this.scrollAmount = 0;
    }

    private List<TrackedDragon> getFilteredList() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return new ArrayList<>();
        
        List<TrackedDragon> result = new ArrayList<>();
        for (int i = 0; i < LightningRadarSettings.INSTANCE.globalResults.size(); i++) {
            DragonInfo info = LightningRadarSettings.INSTANCE.globalResults.get(i);
            double dx = mc.player.getX() - info.x;
            double dy = mc.player.getY() - info.y;
            double dz = mc.player.getZ() - info.z;
            int dist = (int) Math.sqrt(dx * dx + dy * dy + dz * dz);
            result.add(new TrackedDragon(info, dist, i));
        }
        
        return result.stream()
            .filter(d -> d.name.contains("Lightning"))
            .filter(d -> FireRadarSettings.INSTANCE.selectedStages.contains("Stage " + d.stage))
            .filter(d -> FireRadarSettings.INSTANCE.selectedGenders.contains(d.isMale ? "Male" : "Female"))
            .sorted((d1, d2) -> {
                boolean d1Tracked = d1.id.equals(currentlyTrackedLightning);
                boolean d2Tracked = d2.id.equals(currentlyTrackedLightning);
                if (d1Tracked && !d2Tracked) return -1;
                if (!d1Tracked && d2Tracked) return 1;
        
        // Then sort by distance
                return LightningRadarSettings.INSTANCE.sortClosest ? 
                    Integer.compare(d1.distance, d2.distance) : 
                    Integer.compare(d2.distance, d1.distance);
            })
        .collect(Collectors.toList());
        }

    private int getMaxScroll() {
        int listStartY = 50; 
        int totalRows = (int) Math.ceil(getFilteredList().size() / 2.0);
        int availableHeight = this.height - listStartY - 10;
        return Math.max(0, (totalRows * 85) - availableHeight);
    }

    @Override
    protected void init() {
        this.addRenderableWidget(new TransparentButton(10, 40, 110, 20, Component.literal(""), b -> {
            if (!LightningRadarSettings.INSTANCE.hasPerformedSearch && !isWaitingForResults) performActualSearch();
            else if (isWaitingForResults) { this.isWaitingForResults = false; LightningRadarSettings.INSTANCE.hasPerformedSearch = true; }
            else if (selectedDragonEntry != null) {
                if (selectedDragonEntry.equals(currentlyTrackedLightning)) {
                    currentlyTrackedLightning = null;
                    GlobalRadarState.stopTracking();
                } else {
                    com.dragon.dragonmod.client.gui.master.MasterRadarScreen.currentlyTrackedMaster = null;
                    com.dragon.dragonmod.client.gui.fire.FireRadarScreen.currentlyTrackedFire = null;
                    com.dragon.dragonmod.client.gui.ice.IceRadarScreen.currentlyTrackedIce = null;
                    com.dragon.dragonmod.client.gui.dormant.DormantRadarScreen.currentlyTrackedDormant = null;
                    currentlyTrackedLightning = selectedDragonEntry;
                    GlobalRadarState.startTracking("lightning", selectedDragonEntry);
                    this.onClose();
                }
            } else if (currentlyTrackedLightning != null) {
                currentlyTrackedLightning = null;
                GlobalRadarState.stopTracking();
            } else { 
                LightningRadarSettings.INSTANCE.globalResults.clear(); 
                LightningRadarSettings.INSTANCE.hasPerformedSearch = false; 
            }
        }) {
            @Override public void renderWidget(GuiGraphics gui, int mx, int my, float pt) {
                String label;
                if (isWaitingForResults) {
                    label = "Stop Searching";
                } else if (!LightningRadarSettings.INSTANCE.hasPerformedSearch) {
                    label = "Start Search";
                } else if (currentlyTrackedLightning != null && (selectedDragonEntry == null || selectedDragonEntry.equals(currentlyTrackedLightning))) {
                    label = "Stop Tracking";
                } else if (selectedDragonEntry != null) {
                    if (GlobalRadarState.isTracking() && !GlobalRadarState.isTrackingWith("lightning")) {
                        label = "Switch Target";
                    } else {
                        label = "Start Tracking";
                    }
                } else {
                    label = "Clear List";
                }
                this.setMessage(Component.literal(label));
                super.renderWidget(gui, mx, my, pt);
            }
        });

        this.addRenderableWidget(new TransparentButton(10, 65, 110, 20, Component.literal("Refresh List"), b -> {
            performActualSearch(); this.refreshFeedbackTicks = 40; currentlyTrackedLightning = null; this.selectedDragonEntry = null;
        }) {
            @Override public void renderWidget(GuiGraphics gui, int mx, int my, float pt) {
                this.setMessage(Component.literal(refreshFeedbackTicks > 0 ? "List Refreshed!" : "Refresh List"));
                super.renderWidget(gui, mx, my, pt);
            }
        });

        this.addRenderableWidget(new TransparentButton(10, 105, 110, 20, Component.literal("Filter: Stages"), b -> { showStageBox = !showStageBox; showGenderBox = false; }));
        this.addRenderableWidget(new TransparentButton(10, 130, 110, 20, Component.literal("Filter: Genders"), b -> { showGenderBox = !showGenderBox; showStageBox = false; }));

        this.addRenderableWidget(new TransparentButton(10, 155, 110, 20, Component.literal("Reset Filters"), b -> { 
            LightningRadarSettings.INSTANCE.selectedStages.clear(); LightningRadarSettings.INSTANCE.selectedStages.addAll(Arrays.asList(stageNames));
            LightningRadarSettings.INSTANCE.selectedGenders.clear(); LightningRadarSettings.INSTANCE.selectedGenders.addAll(Arrays.asList(genderNames));
            LightningRadarSettings.INSTANCE.searchRadius = 7500; LightningRadarSettings.INSTANCE.sortClosest = true;
            if (this.radiusSlider != null) this.radiusSlider.setValue(7500.0 / 15000.0);
            this.resetFeedbackTicks = 40; 
        }) {
            @Override public void renderWidget(GuiGraphics gui, int mx, int my, float pt) {
                this.setMessage(Component.literal(resetFeedbackTicks > 0 ? "Filters Reset!" : "Reset Filters"));
                super.renderWidget(gui, mx, my, pt);
            }
        });

        this.addRenderableWidget(new TransparentButton(10, 200, 110, 20, Component.literal(""), b -> LightningRadarSettings.INSTANCE.sortClosest = !LightningRadarSettings.INSTANCE.sortClosest) {
            @Override public void renderWidget(GuiGraphics gui, int mx, int my, float pt) {
                this.setMessage(Component.literal(LightningRadarSettings.INSTANCE.sortClosest ? "Dist: Closest -> Far" : "Dist: Far -> Closest"));
                super.renderWidget(gui, mx, my, pt);
            }
        });

        this.radiusSlider = new TransparentSlider(10, 242, 110, 20, Component.literal("Radius: " + LightningRadarSettings.INSTANCE.searchRadius), (double) LightningRadarSettings.INSTANCE.searchRadius / 15000.0D) { 
            @Override protected void updateMessage() { LightningRadarSettings.INSTANCE.searchRadius = Math.max(100, Math.round(((int)(this.value * 15000.0D)) / 100.0f) * 100); this.setMessage(Component.literal("Radius: " + LightningRadarSettings.INSTANCE.searchRadius)); } 
            @Override protected void applyValue() { updateMessage(); } 
        };
        this.addRenderableWidget(this.radiusSlider);

        this.addRenderableWidget(new TransparentButton(this.width - 120, 10, 110, 20, Component.literal("Teleport"), b -> {
            if (this.minecraft.player != null && currentlyTrackedLightning != null) {
                getFilteredList().stream().filter(d -> d.id.equals(currentlyTrackedLightning)).findFirst().ifPresent(td -> {
                    this.minecraft.player.connection.sendUnsignedCommand(String.format("tp @s %.1f %.1f %.1f", td.x, td.y, td.z));
                    this.onClose();
                });
            }
        }) {
            @Override public void renderWidget(GuiGraphics gui, int mx, int my, float pt) {
                this.active = minecraft.player != null && minecraft.player.isCreative() && currentlyTrackedLightning != null;
                super.renderWidget(gui, mx, my, pt);
            }
        });

        this.addRenderableWidget(new TransparentButton(10, this.height - 30, 110, 20, Component.literal("Cancel"), b -> this.onClose()));
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(guiGraphics); 
        guiGraphics.drawString(this.font, "Filters", 10, 93, 0xAAAAAA);
        guiGraphics.drawString(this.font, "Sort", 10, 188, 0xAAAAAA);
        guiGraphics.drawString(this.font, "Search Settings", 10, 230, 0xAAAAAA);

        int startX = 145 + 16; 
        int startY = 50; 
        int listHeight = this.height - startY - 10;
        int scrollBarX = startX + 80 + (180 * 2) - 5; 

        if (isWaitingForResults) {
            String dots = ".".repeat((int)((System.currentTimeMillis() / 300) % 4));
            guiGraphics.drawCenteredString(this.font, "Scanning Sky" + dots, startX + 80 + 180 - 10, this.height / 2, 0xFFAA00);
        } else {
            int statsX = this.width - 120;
            if (LightningRadarSettings.INSTANCE.hasPerformedSearch) renderStatsBox(guiGraphics, statsX, 40);

            List<TrackedDragon> filtered = getFilteredList();
            if (filtered.isEmpty() && LightningRadarSettings.INSTANCE.hasPerformedSearch) {
                guiGraphics.drawCenteredString(this.font, "No Dragons Found", startX + 80 + 180 - 10, this.height / 2, 0xFF5555);
            } else if (!filtered.isEmpty()) {
                if (this.isDraggingScrollBar) this.scrollAmount = Mth.clamp((float)(mouseY - startY) / (float)listHeight, 0, 1) * getMaxScroll();
                
                PoseStack ps = guiGraphics.pose();
                ps.pushPose();
                guiGraphics.enableScissor(startX + 70, startY - 6, scrollBarX + 10, startY + listHeight);
                ps.translate(0, -scrollAmount, 0);

                for (int i = 0; i < filtered.size(); i++) {
                    TrackedDragon d = filtered.get(i);
                    int rX = startX + 80 + ((i % 2) * 180);
                    int rY = startY + ((i / 2) * 85); 
                    boolean hover = mouseX >= rX && mouseX <= rX + 170 && (mouseY + scrollAmount) >= rY && (mouseY + scrollAmount) <= rY + 70;
                    if (d.id.equals(selectedDragonEntry)) guiGraphics.fill(rX - 2, rY - 5, rX + 170, rY + 65, 0x44FFFFFF);
                    renderDragonProfile(guiGraphics, d, rX, rY, d.id.equals(selectedDragonEntry), d.id.equals(currentlyTrackedLightning), hover);
                }
                guiGraphics.disableScissor();
                ps.popPose();

                int barPos = (int)((getMaxScroll() <= 0) ? 0 : (scrollAmount / getMaxScroll()) * (listHeight - 40));
                guiGraphics.fill(scrollBarX, startY, scrollBarX + scrollBarWidth, startY + listHeight, 0x44000000);
                guiGraphics.fill(scrollBarX, startY + barPos, scrollBarX + scrollBarWidth, startY + barPos + 40, 0xFFFFFFFF);
            }
        }

        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        if (showStageBox) renderPopupBox(guiGraphics, stageNames, LightningRadarSettings.INSTANCE.selectedStages, 125, 105);
        if (showGenderBox) renderPopupBox(guiGraphics, genderNames, LightningRadarSettings.INSTANCE.selectedGenders, 125, 130);
    }

    private void renderDragonProfile(GuiGraphics gui, TrackedDragon d, int x, int y, boolean sel, boolean track, boolean hovered) {
        int color = d.name.contains("Lightning") ? COLOR_LIGHTNING : (d.name.contains("Ice") ? COLOR_ICE : COLOR_FIRE);
        PoseStack ps = gui.pose(); ps.pushPose(); ps.scale(1.5f, 1.5f, 1.0f);
        int finalColor = track ? 0xFF55FF55 : (sel ? 0xFFFFFF55 : color);
        gui.drawString(this.font, (track ? ">> " : "") + d.name, (int)(x / 1.5f), (int)(y / 1.5f), finalColor);
        ps.pushPose(); ps.scale(0.66f, 0.66f, 1.0f);
        int tx = (int)((x/1.5f)/0.66f), ty = (int)((y/1.5f+12)/0.66f);
        gui.drawString(this.font, "Distance: " + d.distance + "m", tx, ty, 0xAAAAAA);
        gui.drawString(this.font, "Stage: " + d.stage, tx, ty + 12, 0xAAAAAA);
        gui.drawString(this.font, "Gender: " + (d.isMale ? "§bMale" : "§dFemale"), tx, ty + 24, 0xAAAAAA);
        ps.popPose(); ps.popPose();
        if (!hovered) gui.renderOutline(x - 2, y - 5, 172, 70, 0xFFFFFFFF);
    }

    private void renderStatsBox(GuiGraphics gui, int x, int y) {
        long light = LightningRadarSettings.INSTANCE.globalResults.stream().filter(d -> d.type.contains("Lightning")).count();
        PoseStack ps = gui.pose(); ps.pushPose(); ps.scale(0.85f, 0.85f, 1.0f);
        int sx = (int)(x / 0.85f), sy = (int)(y / 0.85f);
        gui.drawString(this.font, "§nDRAGONS FOUND:", sx, sy, 0xFFFFFF);
        gui.drawString(this.font, "Lightning Dragons = " + light, sx, sy + 15, COLOR_LIGHTNING);
        gui.drawString(this.font, "Total = " + light, sx, sy + 27, 0xFFFFFF);
        ps.popPose();
    }

    private boolean handlePopupClick(double mx, double my, String[] items, List<String> sel, int x, int y) { 
        int lh = items.length * 15; 
        if (mx >= x + 5 && mx <= x + 105 && my >= y + lh + 10 && my <= y + lh + 22) { sel.clear(); sel.addAll(Arrays.asList(items)); return true; } 
        if (mx >= x + 5 && mx <= x + 105 && my >= y + lh + 25 && my <= y + lh + 37) { sel.clear(); return true; } 
        for (int i = 0; i < items.length; i++) { 
            if (mx >= x && mx <= x + 110 && my >= y + 5 + (i * 15) && my <= y + 5 + (i * 15) + 12) { 
                if (sel.contains(items[i])) sel.remove(items[i]); else sel.add(items[i]); return true; 
            } 
        } return false; 
    }

    private void renderPopupBox(GuiGraphics gui, String[] items, List<String> sel, int x, int y) { 
        int lh = items.length * 15; gui.fill(x, y, x + 110, y + lh + 45, 0xEE000000); gui.renderOutline(x, y, 110, lh + 45, 0xFFFFFFFF); 
        for (int i = 0; i < items.length; i++) { boolean s = sel.contains(items[i]); gui.drawString(this.font, (s ? "[X] " : "[ ] ") + items[i], x + 5, y + 5 + (i * 15), s ? 0xFF00FF00 : 0xFFFFFFFF); } 
        gui.fill(x + 5, y + lh + 10, x + 105, y + lh + 22, 0x44FFFFFF); gui.drawString(this.font, "Select All", x + 10, y + lh + 12, 0xFFFFFF); 
        gui.fill(x + 5, y + lh + 25, x + 105, y + lh + 37, 0x44FFFFFF); gui.drawString(this.font, "Deselect All", x + 10, y + lh + 27, 0xFFFFFF); 
    }

    private static class TransparentButton extends Button { 
        public TransparentButton(int x, int y, int w, int h, Component lbl, OnPress prs) { super(x, y, w, h, lbl, prs, DEFAULT_NARRATION); } 
        @Override public void renderWidget(GuiGraphics gui, int mx, int my, float pt) { if (!isHovered && active) gui.renderOutline(getX(), getY(), width, height, 0xFFFFFFFF); gui.fill(getX(), getY(), getX() + width, getY() + height, (isHovered && active ? 180 : 200) / 2 << 24); gui.drawCenteredString(Minecraft.getInstance().font, getMessage(), getX() + width / 2, getY() + (height - 8) / 2, active ? 0xFFFFFFFF : 0xFF555555); } 
    }

    private abstract static class TransparentSlider extends AbstractSliderButton { 
        public TransparentSlider(int x, int y, int w, int h, Component t, double v) { super(x, y, w, h, t, v); } 
        public void setValue(double nv) { this.value = Mth.clamp(nv, 0, 1); updateMessage(); }
        @Override public void renderWidget(GuiGraphics gui, int mx, int my, float pt) { gui.renderOutline(getX(), getY(), width, height, 0xAAFFFFFF); gui.fill(getX(), getY(), getX() + width, getY() + height, 0x44FFFFFF); int hW = 8; int hX = getX() + (int) (this.value * (double) (this.width - hW)); gui.fill(hX, getY(), hX + hW, getY() + height, 0xCCFFFFFF); gui.drawCenteredString(Minecraft.getInstance().font, getMessage(), getX() + width / 2, getY() + (height - 8) / 2, 0xFFFFFF); } 
    }

    @Override public boolean mouseClicked(double mx, double my, int b) { 
        int startX = 145 + 16, lSY = 50, sBX = startX + 80 + (180 * 2) - 5, lH = this.height - lSY - 10;
        if (!LightningRadarSettings.INSTANCE.globalResults.isEmpty() && mx >= sBX - 5 && mx <= sBX + 15 && my >= lSY && my <= lSY + lH) { this.isDraggingScrollBar = true; return true; } 
        if (!isWaitingForResults && mx > startX + 80) { List<TrackedDragon> f = getFilteredList(); for (int i = 0; i < f.size(); i++) { int rX = startX + 80 + ((i % 2) * 180), rY = lSY + ((i / 2) * 85); if (mx >= rX && mx <= rX + 170 && (my + scrollAmount) >= rY && (my + scrollAmount) <= rY + 70) { this.selectedDragonEntry = f.get(i).id.equals(selectedDragonEntry) ? null : f.get(i).id; return true; } } } 
        if (showStageBox && handlePopupClick(mx, my, stageNames, LightningRadarSettings.INSTANCE.selectedStages, 125, 105)) return true; 
        if (showGenderBox && handlePopupClick(mx, my, genderNames, LightningRadarSettings.INSTANCE.selectedGenders, 125, 130)) return true;
        return super.mouseClicked(mx, my, b); 
    }

    @Override public void tick() { 
        super.tick(); 
        if (isWaitingForResults) {
            if (!LightningRadarSettings.INSTANCE.globalResults.isEmpty() || DragonScanner.isSearchComplete) { isWaitingForResults = false; LightningRadarSettings.INSTANCE.hasPerformedSearch = true; } 
            else { serverWaitTimer--; if (serverWaitTimer <= 0) { isWaitingForResults = false; LightningRadarSettings.INSTANCE.hasPerformedSearch = true; } }
        }
        if (resetFeedbackTicks > 0) resetFeedbackTicks--; if (refreshFeedbackTicks > 0) refreshFeedbackTicks--; 
    }
    
    @Override public boolean mouseReleased(double mx, double my, int b) { this.isDraggingScrollBar = false; return super.mouseReleased(mx, my, b); }
    @Override public boolean mouseScrolled(double mx, double my, double d) { this.scrollAmount = Mth.clamp(this.scrollAmount - d * 25, 0, getMaxScroll()); return true; }
}