## Why

Tracks GitHub issue **#45** ("1.21"). Craft Tracker ships parallel per-version branches
(`1.16`, `1.18`, `1.19`, `1.20`, `1.21` per the CI trigger). This change captures bringing the
current feature set to **Minecraft 1.21** (Forge/NeoForge as appropriate for that version).

> Note: this is a port, not a new feature. The issue body is empty; scope = parity with the
> existing branches on the 1.21 platform. It is captured as a proposal because you asked, but the
> work is largely mechanical API migration rather than new requirements.

## What Changes

- Migrate the mod to the Minecraft 1.21 platform APIs while preserving the existing feature set
  (queue, shopping list, overlays, computation, key bindings, JEI integration, advancements).
- Update mappings, dependency versions, and loader entry points for 1.21.
- Adapt version-divergent APIs: rendering/`PoseStack` → `GuiGraphics`, registry access, event
  bus signatures, `KeyMapping`/input events, networking registration, datagen providers.

## Capabilities

### New Capabilities

<!-- None — this ports existing behavior; no new requirements. Spec deltas only if 1.21 forces a
     behavior change. -->

### Modified Capabilities

<!-- None recorded as formal specs yet. -->

## Impact

- **Platform:** new `1.21` branch; `gradle.properties` versions (Minecraft, Forge/NeoForge, JEI,
  Parchment), `.release-info/1.21/`, CI trigger entry.
- **Code:** broad but shallow — every client-facing API that changed between 1.18.2 and 1.21
  (notably the `GuiGraphics` rendering migration and registry/event changes).
- **Sequencing:** ideally lands after structural changes (`remove-jei-hard-dependency`, and a
  decision on `architectury-framework-support` #46) to avoid porting code that's about to be
  restructured.
- **Out of scope:** new gameplay features; this is parity-only.
