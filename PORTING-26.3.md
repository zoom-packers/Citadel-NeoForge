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

- **Render passes (zp5.3, 2026-10-22).** 26.3 refuses a texture upload while a render pass is open ("Close the
  existing render pass before performing additional commands"), and `RenderType#prepare` loads a render type's
  textures the first time they are used. Two changes:
  - **`WorldRenderMacros.BufferSource#endBatch`.** It now prepares every render type before it opens its pass.
    Before, the first follower, lectern book page or pathfinding debug draw with a new texture crashed the client.
    With the Patreon follower that meant: a listed player in third person; any client drawing a listed player nearby;
    and, with an Iris shader pack, every listed player, because Iris draws the player in its shadow pass. The list in
    `patreon.txt` includes `Dev`, the NeoForge dev client's default name, so every dev client hit it.
  - **The follower draws through the frame's `SubmitNodeCollector`.** `CitadelPatreonRenderer` has a new
    overridable `submit(PoseStack, SubmitNodeCollector, ...)`, which `ClientProxy#playerRender` calls with the
    event's collector. The default keeps the old path (`render` into a `WorldRenderMacros` batch, then `endBatch`), so
    third-party renderers still work. `SpaceStationPatreonRenderer` overrides it with `submitCustomGeometry`, so the
    station draws in order with the player, and in a shader pack's shadow pass with the right matrices.

## Verification

- `./gradlew build` passes.
- Render passes (zp5.3): a dev client with Rats, Create, Iris 1.11.7 and Sodium 0.9.3 (the pack's versions), as `Dev`
  in third person. ZP5's `mods/zp5-workforce` `runClientSmoke -PsmokeUser=Dev -PsmokeThirdPerson -PwithIris
  -PcitadelJar=...`.
  - With zp5.1 it crashes the moment the follower first draws, without shaders.
  - With zp5.3 it reaches the world and draws the follower, both without shaders and under Complementary Unbound
    r5.9.3.
  - The default `submit` path (third-party renderers) and the lectern and debug batches were not run.
- Rats 26.3 builds against it, and a dedicated server with both boots. Every Rats entity, which uses Citadel's
  animation and model classes, ticks without errors.
- The client was not run before zp5.3.
