# Citadel for Minecraft 26.3 / NeoForge 26.3.0.89 (zp5/26.3)

This branch builds on astryxion's `NeoForge-26.3` port of Citadel (`c9dea0e`, "ported to neoforge 26.3"). That port
was written for an early 26.3 NeoForge and lived inside the Alex's Mobs build. Citadel is © Alexthe666 and
contributors, licensed under the GNU LGPL v3 (`LICENSE`), and the changes here are LGPL v3 too. It was made for the
Zoompack 5 modpack, to run Rats; there are no modpack-specific changes.

Checked on 2026-10-21 against MC 26.3, NeoForge 26.3.0.89, ModDevGradle 2.0.147 and JDK 25.

## Changes

- **A standalone build.** `settings.gradle`, `gradle.properties` and the Gradle wrapper (9.2.1) were added. The video
  libraries (jcodec, jaad) are bundled into the jar, as in the official Citadel build, and a `publishing` block
  publishes `com.github.alexthe666:citadelneoforge:26.3-1.0.0-zp5.1` (`./gradlew build publishToMavenLocal`).
- **API changes since the 26.3 beta it was written against:**
  - `BiomeModifier#modify` takes a `RegistryAccess` (`SpawnProbabilityModifier`).
  - `ModConfig.Type.COMMON` is now `LOCAL`.
  - `AbstractWidget#getFGColor` no longer exists, so `LinkButton` keeps it as its own method.

## Verification

- `./gradlew build` passes.
- Rats 26.3 builds against it, and a dedicated server with both boots. Every Rats entity, which uses Citadel's
  animation and model classes, ticks without errors.
- The client was not run.
