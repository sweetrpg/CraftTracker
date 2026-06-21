## 1. Hover provider abstraction

- [x] 1.1 Add a `HoverItemProvider` interface under `integration/` returning the item under the mouse for a given
  screen + mouse position (priority + `getName()`)
- [x] 1.2 Implement `VanillaSlotHoverProvider` using `AbstractContainerScreen.getSlotUnderMouse()` → `slot.getItem()`;
  register it unconditionally
- [x] 1.3 Implement `JeiHoverProvider` wrapping `getIngredientListOverlay().getIngredientUnderMouse()`; this is the only
  client-path class that imports JEI API types
- [x] 1.4 Add a registry/lookup that returns registered providers in priority order, with the JEI provider ahead of the
  native provider

## 2. JEI classloading isolation & registration

- [x] 2.1 Hand the `IJeiRuntime` from `CTPlugin.onRuntimeAvailable` to `JeiHoverProvider` instead of exposing
  `CTPlugin.jeiRuntime` to `ClientEventHandler`
- [x] 2.2 Register `JeiHoverProvider` only behind `ModList.get().isLoaded("jei")`, wired through the existing `Addon`/
  `AddonManager` integration layer
- [x] 2.3 Confirm no class on a no-JEI code path statically references `CTPlugin` or any JEI API type

## 3. Repoint add-to-queue

- [x] 3.1 Rewrite `ClientEventHandler#handleAddToQueue` to query the provider lookup, take the first resolved item, and
  call `CraftingQueueManager.INSTANCE.addProduct(player, id, 1)` + send the `QUEUE_ITEM` advancement
- [x] 3.2 Remove the JEI import and `CTPlugin.jeiRuntime` reference from `ClientEventHandler`
- [x] 3.3 Confirm `onKeyInput` still gates the action to container screens (extend the allowed screen list if needed)

## 4. JEI-independent data lifecycle

- [x] 4.1 Add a client world-join / login listener that loads `CraftingQueueManager` and `ShoppingListManager` for the
  client player, registered in the existing client event wiring
- [x] 4.2 Remove the `load(player)` calls from `CTPlugin.onRuntimeAvailable`
- [x] 4.3 Ensure loading is idempotent and fires once per world entry

## 5. Native item picker in Queue Management screen

- [x] 5.1 Add a search box + scrollable item list to `QueueManagementScreen` backed by `ForgeRegistries.ITEMS`, filtered
  by the search term
- [x] 5.2 On selection, call `addProduct(player, itemId, 1)` and refresh the screen/queue
- [x] 5.3 Add any new translation keys to `common/Constants` and `CTLangProvider`, then regenerate with `./gradlew data`

## 6. Dependency metadata

- [x] 6.1 Set `mandatory=false` for the `jei` dependency block in `META-INF/mods.toml`
- [x] 6.2 Change CurseForge `requiredDependency 'jei'` and Modrinth `required.project "jei"` to their optional
  equivalents in `build.gradle` (keep `runtimeOnly` JEI for dev)

## 7. Verification

- [x] 7.1 `./gradlew build` and `./gradlew test` pass
- [ ] 7.2 Launch `./gradlew client` with JEI present: verify hover-add from container slots, hover-add from the JEI panel, the picker, and that queue/list load on join
- [ ] 7.3 Launch the client with JEI removed from `runtimeOnly`: verify the mod loads (no `NoClassDefFoundError`), hover-add from slots works, the picker works, and queue/list load on join
