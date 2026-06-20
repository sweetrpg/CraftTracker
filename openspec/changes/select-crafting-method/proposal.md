## Why

Tracks GitHub issue **#25**. When an item has more than one recipe, Craft Tracker currently
picks the "least expensive" method automatically (via `common/util/calc/`). Players want to choose
*which* recipe is used for a queued product, so the computed intermediates, raw materials, and fuel
reflect the chosen method rather than the auto-selected one.

## What Changes

- Allow a queued product to carry an **explicit recipe selection** that overrides automatic
  "least-expensive" selection during computation.
- Surface the available recipes for a product and let the player pick one (in the Queue Management
  screen and/or queue overlay interaction).
- Recompute intermediates/materials/fuel for the chosen recipe when the selection changes.
- Persist the selection with the queued product so it survives reload.

## Capabilities

### New Capabilities

- `recipe-selection`: How a player chooses and persists a specific recipe for a queued product,
  and how that choice flows into the queue computation instead of automatic cost-based selection.

### Modified Capabilities

<!-- None recorded as formal specs yet — the automatic-selection behavior in calc/ is currently
     implicit and will be made overridable. -->

## Impact

- **Code:** `common/manager/CraftingQueueManager` (`computeProduct`/`computeRecipe` honor a chosen
  recipe), `common/util/calc/` (selection becomes overridable rather than always cheapest),
  `common/model/CraftingQueueProduct` (store selected recipe id),
  `common/storage/CraftingQueueStorage` (persist it), and UI in `client/screen` / `client/overlay`.
- **Data:** adds a field to the persisted queue NBT — needs backward-compatible load (absent =
  auto-select).
- **Open question:** how to identify a recipe stably across reloads/mod updates (recipe
  ResourceLocation vs. index) — decide in design.
