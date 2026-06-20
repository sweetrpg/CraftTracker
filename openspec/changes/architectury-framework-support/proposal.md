## Why

Tracks GitHub issue **#46** ("Other mod frameworks — Architectury?"). Craft Tracker is currently
**Forge-only**, maintained as parallel per-Minecraft-version branches. A multi-loader framework
such as Architectury (or splitting common logic from loader-specific code) could let the mod target
Forge, Fabric, NeoForge, and Quilt from a shared codebase, reducing the per-branch porting burden.

> Note: this is a coarse, strategic proposal (the issue body is a single word). It frames the
> investigation rather than committing to an implementation; the design phase should be a spike.

## What Changes

- Evaluate and (if chosen) adopt a multi-loader strategy that separates loader-agnostic logic
  (managers, storage, model, computation) from loader-specific code (registration, events,
  networking, key bindings, rendering hooks).
- Define how the existing Forge entry points (`@Mod`, `SimpleChannel`, `DistExecutor`,
  `KeyMapping`, datagen) map onto the abstraction.
- Build on `remove-jei-hard-dependency`: item-viewer integration becomes one of several
  loader/mod-optional concerns behind interfaces.

## Capabilities

### New Capabilities

- `multi-loader-abstraction`: The separation of loader-agnostic core logic from loader-specific
  adapters, and the strategy (e.g. Architectury) used to build for multiple mod loaders from one
  source.

### Modified Capabilities

<!-- None recorded as formal specs yet — this is structural. -->

## Impact

- **Project structure:** potentially a large reorganization (common vs. forge/fabric/neoforge
  source sets), build changes in `build.gradle`/`gradle.properties`, and rethinking the
  branch-per-version model.
- **Code:** networking (`SimpleChannel` → loader-neutral), registries, events, client/server split,
  datagen — all touched.
- **Risk:** high effort; should start as a time-boxed spike with a go/no-go decision in design,
  not a committed migration. Coordinate with the cross-version branches (#45).
