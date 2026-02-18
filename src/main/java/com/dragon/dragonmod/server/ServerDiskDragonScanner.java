package com.dragon.dragonmod.server;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.storage.RegionFile;
import net.minecraft.world.level.storage.LevelResource;

import java.io.DataInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ServerDiskDragonScanner {

    public static class DragonData {
        public int stage;
        public String type;
        public boolean isMale; // ADDED: Gender field
        public double x, y, z;

        public DragonData(int stage, String type, boolean isMale, double x, double y, double z) {
            this.stage = stage;
            this.type = type;
            this.isMale = isMale;
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }

    public static List<DragonData> scanDragons(ServerLevel level, double centerX, double centerZ, double radius) {
        List<DragonData> foundDragons = new ArrayList<>();

        // ---------------------------------------------------------
        // PHASE 1: Scan Loaded Entities (RAM)
        // ---------------------------------------------------------
        for (Entity entity : level.getAllEntities()) {
            String entityId = EntityType.getKey(entity.getType()).toString();

            if (isValidDragon(entityId)) {
                double dist = Math.sqrt(entity.distanceToSqr(centerX, entity.getY(), centerZ));
                if (dist <= radius) {
                    CompoundTag nbt = new CompoundTag();
                    entity.saveWithoutId(nbt);
                    
                    // Filters: Alive, Wild, Not Tamed
                    if (nbt.getFloat("Health") > 0 && !nbt.getBoolean("Tamed") && !nbt.hasUUID("Owner")) {
                        int ageTicks = nbt.getInt("AgeTicks");
                        boolean gender = nbt.getBoolean("Gender"); // Extracting Gender

                        foundDragons.add(new DragonData(
                            calculateStage(ageTicks), 
                            identifyDragonType(entityId), 
                            gender,
                            entity.getX(), entity.getY(), entity.getZ()
                        ));
                    }
                }
            }
        }

        // ---------------------------------------------------------
        // PHASE 2: Scan Unloaded Chunks (DISK)
        // ---------------------------------------------------------
        File worldDir = level.getServer().getWorldPath(LevelResource.ROOT).toFile();
        File dimensionDir = level.dimension() == ServerLevel.OVERWORLD ? worldDir : 
            new File(worldDir, "dimensions/" + level.dimension().location().getNamespace() + "/" + level.dimension().location().getPath());
        
        File entitiesFolder = new File(dimensionDir, "entities");
        if (!entitiesFolder.exists()) return foundDragons;

        int minRegX = (int) Math.floor((centerX - radius) / 512.0);
        int maxRegX = (int) Math.floor((centerX + radius) / 512.0);
        int minRegZ = (int) Math.floor((centerZ - radius) / 512.0);
        int maxRegZ = (int) Math.floor((centerZ + radius) / 512.0);

        for (int rX = minRegX; rX <= maxRegX; rX++) {
            for (int rZ = minRegZ; rZ <= maxRegZ; rZ++) {
                File regionFile = new File(entitiesFolder, "r." + rX + "." + rZ + ".mca");
                if (!regionFile.exists()) continue;

                try (RegionFile rf = new RegionFile(regionFile.toPath(), entitiesFolder.toPath(), true)) {
                    for (int cx = 0; cx < 32; cx++) {
                        for (int cz = 0; cz < 32; cz++) {
                            int globalChunkX = (rX << 5) + cx;
                            int globalChunkZ = (rZ << 5) + cz;

                            if (level.hasChunk(globalChunkX, globalChunkZ)) continue;

                            ChunkPos pos = new ChunkPos(globalChunkX, globalChunkZ);
                            if (!rf.hasChunk(pos)) continue;

                            try (DataInputStream dis = rf.getChunkDataInputStream(pos)) {
                                if (dis == null) continue;
                                CompoundTag chunkNbt = NbtIo.read(dis);
                                if (chunkNbt == null) continue;
                                
                                ListTag entities = chunkNbt.getList("Entities", Tag.TAG_COMPOUND);
                                for (int i = 0; i < entities.size(); i++) {
                                    CompoundTag entityNbt = entities.getCompound(i);
                                    String id = entityNbt.getString("id");

                                    if (isValidDragon(id)) {
                                        ListTag posList = entityNbt.getList("Pos", Tag.TAG_DOUBLE);
                                        double ex = posList.getDouble(0);
                                        double ez = posList.getDouble(2);

                                        if (Math.sqrt(Math.pow(ex - centerX, 2) + Math.pow(ez - centerZ, 2)) <= radius) {
                                            if (entityNbt.getFloat("Health") > 0 && !entityNbt.getBoolean("Tamed") && !entityNbt.hasUUID("Owner")) {
                                                
                                                int ageTicks = entityNbt.getInt("AgeTicks");
                                                boolean gender = entityNbt.getBoolean("Gender"); // Extracting Gender from Disk

                                                foundDragons.add(new DragonData(
                                                    calculateStage(ageTicks), 
                                                    identifyDragonType(id), 
                                                    gender,
                                                    ex, posList.getDouble(1), ez
                                                ));
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception ignored) {}
            }
        }
        return foundDragons;
    }

    private static boolean isValidDragon(String id) {
        if (id == null) return false;
        String lower = id.toLowerCase();
        return lower.contains("iceandfire") && 
               (lower.contains("fire_dragon") || lower.contains("ice_dragon") || lower.contains("lightning_dragon")) &&
               !lower.contains("skull") && !lower.contains("part") && !lower.contains("egg");
    }

    private static int calculateStage(int ageTicks) {
        int days = ageTicks / 24000;
        int stage = (days / 25) + 1;
        return Math.max(1, Math.min(5, stage));
    }

    private static String identifyDragonType(String id) {
        String lower = id.toLowerCase();
        if (lower.contains("lightning_dragon")) return "Lightning Dragon";
        if (lower.contains("ice_dragon")) return "Ice Dragon";
        if (lower.contains("fire_dragon")) return "Fire Dragon";
        return "Unknown Dragon";
    }
}