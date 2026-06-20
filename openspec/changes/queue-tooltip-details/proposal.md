## Why

Tracks GitHub issue **#81**. Players want richer information surfaced as tooltips/labels in the
queue UI, specifically:

- **Recipe count** — how many distinct recipes exist for a product.
- **Cost range** — the cheapest-to-most-expensive cost span across those recipes.

This makes the auto-selected "least expensive" choice transparent and gives players the context to
decide whether to override it (see related #25 / `select-crafting-method`).

## What Changes

- Compute and display the number of recipes available for a queued product.
- Compute and display a cost range (min–max) across a product's recipes, reusing the existing
  cost calculators.
- Render these as tooltip content (and/or inline labels) where queued products are shown.

## Capabilities

### New Capabilities

- `queue-item-tooltips`: The informational tooltip/label content shown for queued products,
  including recipe count and cost range, and where it is surfaced in the UI.

### Modified Capabilities

<!-- None recorded as formal specs yet. -->

## Impact

- **Code:** `common/util/calc/` (`RecipeCostCalculator` etc.) to expose min/max cost and recipe
  count; `client/overlay/CraftQueueOverlay` and/or `client/screen/QueueManagementScreen` for
  rendering; translation keys in `common/Constants` + `CTLangProvider`.
- **Performance:** cost-range computation should reuse already-computed recipe data rather than
  recomputing per frame.
- **Relationship:** complements `select-crafting-method` (#25) — the range/count is the context a
  player uses when choosing a method.
