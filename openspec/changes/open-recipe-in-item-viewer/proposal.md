## Why

Tracks GitHub issue **#13**. When a screen (e.g. a crafting table) is open, players want to hover
a product or intermediate in Craft Tracker's lists and click to **open that item's recipe in the
item viewer** (JEI). This shortcuts the "what do I make this from?" loop.

## What Changes

- Add a hover + click interaction on entries in the queue/intermediates lists that opens the
  recipe for that item in the installed item viewer.
- Route the "show recipes for item X" action through the **optional integration layer** (the same
  provider/`Addon` abstraction introduced by `remove-jei-hard-dependency`), so this feature is
  available when JEI is present and simply inert when it is not.
- Leave room for a future REI provider to satisfy the same action.

## Capabilities

### New Capabilities

- `recipe-lookup-action`: A UI action on queue/intermediate entries that requests "show recipes
  for this item" from an optional item-viewer integration, gated on that viewer being installed.

### Modified Capabilities

<!-- Builds on hover-provider-integration from the remove-jei-hard-dependency change; no existing
     formal spec to modify yet. -->

## Impact

- **Depends on:** `remove-jei-hard-dependency` (provider/`Addon` integration structure). This
  feature is JEI-API-touching and must live in the quarantined JEI integration classes.
- **Code:** integration layer (`integration/jei/`) to invoke JEI's recipe-lookup
  (`IRecipesGui`/runtime show-recipes API); click handling in `client/overlay/CraftQueueOverlay`
  and/or the queue screen.
- **Behavior when no viewer present:** action is hidden/disabled, no error.
