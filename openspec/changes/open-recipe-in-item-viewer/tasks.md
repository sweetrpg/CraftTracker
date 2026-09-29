# Tasks: open-recipe-in-item-viewer

- [x] Add `showRecipesFor(ItemStack)` default no-op to `Addon` interface
- [x] Create `JeiAddon` in `integration/jei/` with JEI recipe-display implementation
- [x] Register `JeiAddon` in `AddonManager.ADDONS` and add `showRecipesFor` dispatch method
- [x] Add hover tracking (`hoveredItem` field + per-row hit-test) to `CraftQueueOverlay`
- [x] Add `TRANSLATION_KEY_BINDINGS_SHOW_RECIPE_TITLE` to `Constants` and `SHOW_RECIPE_MAPPING` to `ModKeyBindings`
- [x] Handle `SHOW_RECIPE_MAPPING` in `ClientEventHandler.onKeyInput`
- [x] Add "Show Recipe" translation string to all three locales in `CTLangProvider`
- [x] Run `./gradlew data` to regenerate committed lang JSON files
