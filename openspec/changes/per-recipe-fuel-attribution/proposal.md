## Why

Tracks GitHub issue **#26**. Aggregate fuel display already exists — `CraftQueueOverlay` renders a
`// SECTION: fuel` block from `CraftingQueueManager.getFuel()`, and `handlePopulateShoppingList`
already adds fuel to the shopping list. So the *basic* ask (show fuel at all) is implemented.

This change captures the remaining gap: fuel is currently shown only as a flat, queue-wide total.
Players can't see **which queued product / recipe** requires fuel, how much, or distinguish smelting
fuel needs per item — so the number is hard to act on or verify.

## What Changes

- Attribute fuel needs to the specific product/recipe that requires them, rather than only a
  single aggregate total.
- Surface per-recipe (or per-product) fuel in the queue UI and shopping list breakdown.
- Keep the existing aggregate total as a summary.

## Capabilities

### New Capabilities

- `fuel-attribution`: How fuel requirements are associated with the products/recipes that need
  them and surfaced in the queue and shopping list, beyond a single aggregate figure.

### Modified Capabilities

<!-- None recorded as formal specs yet; existing aggregate behavior stays. -->

## Impact

- **Code:** `common/manager/CraftingQueueManager` (`computeRecipe` already classifies fuel — extend
  to record the owning product/recipe), `client/overlay/CraftQueueOverlay` fuel section,
  shopping-list population in `client/event`.
- **Pre-implementation step:** confirm the exact current rendering vs. the issue's intent; if the
  aggregate already satisfies the reporter, this narrows to a small UI grouping change or closes.
- **No storage format change expected.**
