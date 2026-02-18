---
description: Repository Information Overview
alwaysApply: true
---

# Dragon Radar Mod Information

## Summary
The **Dragon Radar Mod** is a Minecraft Forge modification for version **1.20.1**. It adds a functional "Dragon Radar" item to the game, primarily designed as a skeleton or template for radar-like functionality. The mod depends on the **Citadel** library and includes optional integration with the **Ice and Fire** mod.

## Structure
- [./src/main/java](./src/main/java): Contains the Java source code for the mod, including the main entry point and item definitions.
- [./src/main/resources](./src/main/resources): Holds mod metadata (`mods.toml`), assets (textures, models), and data (tags, recipes).
- [./src/generated](./src/generated): Stores resources generated via the Gradle `runData` task.
- [./gradle](./gradle): Contains the Gradle Wrapper files for consistent builds.
- [./run-data](./run-data): Working directory for data generation tasks.

## Language & Runtime
- **Language**: Java
- **Version**: 17
- **Build System**: Gradle (version 8.x compatible)
- **Package Manager**: Gradle (using ForgeGradle 6.0.47)

## Dependencies
**Main Dependencies**:
- **Minecraft**: 1.20.1
- **Minecraft Forge**: 47.4.4
- **Citadel**: 2.5.4 (Required, handled via CurseMaven)
- **Ice and Fire**: 2.1.13-1.20.1-beta-5 (Optional/Referenced in run configurations)

## Build & Installation
```bash
# Build the mod JAR
./gradlew build

# Run the Minecraft Client with the mod
./gradlew runClient

# Run the Minecraft Server with the mod
./gradlew runServer

# Generate data (recipes, tags, etc.)
./gradlew runData

# Setup IDE project files
./gradlew genIntellijRuns   # For IntelliJ IDEA
./gradlew genEclipseRuns    # For Eclipse
```

## Main Files & Resources
- [./src/main/java/com/dragon/dragonmod/DragonRadarMod.java](./src/main/java/com/dragon/dragonmod/DragonRadarMod.java): The main mod entry point where event listeners and mod components are registered.
- [./src/main/resources/META-INF/mods.toml](./src/main/resources/META-INF/mods.toml): Mod metadata file containing ID, version, and dependency information.
- [./src/main/java/com/dragon/dragonmod/ModItems.java](./src/main/java/com/dragon/dragonmod/ModItems.java): Registration of mod items, including the Dragon Radar.
- [./build.gradle](./build.gradle): Core build configuration for Forge and dependencies.

## Testing
- **Framework**: No specific testing framework (like JUnit) is currently configured with active tests.
- **Validation**: Relies on the `runClient` and `runServer` tasks for manual verification of mod functionality within the Minecraft environment.
