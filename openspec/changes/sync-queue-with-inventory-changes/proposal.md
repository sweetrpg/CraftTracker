## Why

Tracks GitHub issues **#76** (acquiring items via other crafting/storage doesn't update the
queue) and **#49** (removing items from inventory doesn't update the queue/shopping list).

The crafting queue and shopping list are computed as a one-shot snapshot — `computeAll()` runs
only when a product is added or removed. `InventoryUtil` subtracts what the player currently
holds, but nothing recomputes when the player's inventory *changes* afterward (picking items up,
pulling from a chest, crafting, or dropping/using items). The result is a stale "needed" count
that no longer reflects what the player actually has.

## What Changes

- Recompute the affected quantities (and refresh the shopping list's "needed" amounts) in
  response to **client-side inventory changes**, so the queue/shopping list stay accurate as the
  player gains or loses items.
- Debounce/coalesce recomputation so rapid inventory churn (e.g. bulk transfers) doesn't trigger
  a recompute per slot tick.
- Ensure both directions are covered: acquiring items (#76) *reduces* needed amounts; removing
  items (#49) *increases* them.

## Capabilities

### New Capabilities

- `inventory-driven-recompute`: When and how the queue/shopping-list quantities are refreshed in
  response to changes in the player's inventory, including coalescing of rapid changes.

### Modified Capabilities

<!-- None recorded as formal specs yet. -->

## Impact

- **Code:** `common/manager/CraftingQueueManager` (recompute entry point), `common/util/InventoryUtil`,
  a client-side inventory-change listener (likely in `client/event`), and `ShoppingListManager`
  refresh. Computation is already client-side, so the listener belongs on the client.
- **Performance:** must avoid recompute storms — coalesce within a tick or on a short timer.
- **No storage format change.**
- **Open question:** should recompute be fully automatic, or gated behind a config toggle for
  players who prefer a static snapshot? (decide in design)
