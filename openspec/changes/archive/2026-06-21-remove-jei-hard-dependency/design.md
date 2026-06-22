## Context

Craft Tracker declares JEI as a **mandatory** dependency, but its real coupling is three small
touchpoints:

1. `META-INF/mods.toml` (`modId="jei"`, `mandatory=true`) and the CurseForge/Modrinth publishing
   metadata in `build.gradle` (`requiredDependency 'jei'`, `required.project "jei"`). The Java
   classpath dependency is already correct: `compileOnly ...:jei:api` + `runtimeOnly` for dev.
2. `integration/jei/CTPlugin` — a `@JeiPlugin` whose 11 registration methods are empty stubs.
   Its only real work is in `onRuntimeAvailable`: it caches `IJeiRuntime` and **loads the
   managers** (`CraftingQueueManager.INSTANCE.load(player)` / `ShoppingListManager...`).
3. `client/event/ClientEventHandler#handleAddToQueue` — the sole feature use of JEI:
   `CTPlugin.jeiRuntime.getIngredientListOverlay().getIngredientUnderMouse()`, from which it
   pulls an `ItemStack`'s `getRegistryName()` and calls
   `CraftingQueueManager.INSTANCE.addProduct(player, res, 1)`.

`addProduct` already takes a plain `ResourceLocation`, so JEI was never a data provider — only a
source of "which item is under the cursor." The add-to-queue key already only fires while a
`CraftingScreen` or `InventoryScreen` is open (`onKeyInput`), with JEI's overlay drawn on top.

An `integration/Addon` interface and `AddonManager` already exist (lifecycle `init`/`exec`,
`getMods()`/`shouldLoad()`), giving a natural home for optional, mod-gated wiring.

## Goals / Non-Goals

**Goals:**
- The mod loads and is fully usable with JEI absent (JEI optional, not mandatory).
- Add-to-queue from hover works in any vanilla container view via native slot lookup.
- JEI (and, by structure, a future REI) remains an optional hover provider when installed.
- Queue/shopping-list persistence loads on a JEI-independent lifecycle event.
- A native item picker in the Queue Management screen backfills full-registry "add directly."

**Non-Goals:**
- Building our own always-on full-registry overlay (JEI/REI fill that when present; the picker
  covers the standalone case).
- Changing the NBT storage format or the queue computation logic.
- Porting to the `1.16`/`1.19`/`1.20`/`1.21` branches in this change (tracked separately).
- Adding REI support now — only leaving the structure open for it.

## Decisions

### D1. Provider abstraction for "item under the mouse"

Introduce an interface (working name `HoverItemProvider`) returning
`Optional<ResourceLocation>` (or `Optional<ItemStack>`) for the current screen + mouse position.

- **Native impl** (`VanillaSlotHoverProvider`): for `AbstractContainerScreen`, read
  `getSlotUnderMouse()` → `slot.getItem()`. Always registered.
- **JEI impl** (`JeiHoverProvider`): wraps the existing
  `getIngredientListOverlay().getIngredientUnderMouse()` logic. Registered only when JEI is loaded.

`handleAddToQueue` iterates registered providers in priority order and uses the first non-empty
result. Provider registration lives in the `integration` layer; the JEI provider is gated by
`ModList.get().isLoaded("jei")`.

- **Why over a direct `if (jeiLoaded) … else …` in `handleAddToQueue`:** keeps the JEI API import
  out of the client event handler (classloading isolation), and makes REI a drop-in third provider.
- **Alternative considered:** reuse the `Addon` interface directly for hover. Rejected — `Addon`
  models init/exec lifecycle, not per-frame mouse queries; a focused interface is clearer. The
  `Addon`/`AddonManager` layer is still used to *register* the JEI provider behind its mod gate.

### D2. Classloading isolation of JEI API

`CTPlugin` and `JeiHoverProvider` are the only classes allowed to import JEI API types. Nothing
on a no-JEI code path may statically reference them. `CTPlugin.jeiRuntime` stops being read from
`ClientEventHandler`; the runtime is instead handed to `JeiHoverProvider` when JEI initializes.

- **Why:** referencing a class that implements/uses a missing API triggers `NoClassDefFoundError`
  at classload, even inside a guarded branch. The `ModList.isLoaded` check must guard the *only*
  reference site, and that site must be a separate class.

### D3. Move manager loading to a JEI-independent lifecycle event

Remove the `load(player)` calls from `CTPlugin.onRuntimeAvailable`. Load the managers from a
client-side world-join / login event (e.g. `ClientPlayerNetworkEvent.LoggedInEvent` or equivalent
for 1.18.2), registered in the existing client event wiring.

- **Why:** today this is the silent breakage point — without JEI, managers never load and saved
  data appears empty. Loading is a client-lifecycle concern, not a JEI concern.
- **Alternative considered:** lazy-load on first overlay render / first mutation. Rejected as more
  surface area and less predictable than an explicit join event.

### D4. Native item picker in the Queue Management screen

`QueueManagementScreen` (a plain `Screen`, already opened via its own keybind) gains a search box
and a scrollable list over `ForgeRegistries.ITEMS`; selecting an item calls
`addProduct(player, id, 1)`.

- **Why:** this is the standalone replacement for JEI's full-registry browse. `addProduct` already
  accepts a `ResourceLocation`, so no manager changes are needed.
- **Scope note:** keep the picker minimal (text filter + clickable list). Rich faceting is out of
  scope.

### D5. Dependency metadata flip

`mods.toml`: `mandatory=false` for the `jei` dependency block. `build.gradle`: move JEI from
`requiredDependency`/`required.project` to the optional equivalents for CurseForge and Modrinth.
Keep `runtimeOnly` JEI so the dev client still launches with JEI for testing both paths.

## Risks / Trade-offs

- **Gesture change for "queue what I don't own"** → Previously hover JEI's search panel; now use
  the picker. Mitigation: the picker is discoverable from the Queue Management keybind; document it.
- **Classloading leak slips through** (a stray JEI import on a common path) → `NoClassDefFoundError`
  only at runtime without JEI. Mitigation: verify by launching the dev client with JEI temporarily
  removed from `runtimeOnly`; keep JEI imports confined to `CTPlugin` + `JeiHoverProvider`.
- **Double-load or no-load of managers** after moving the lifecycle hook → Mitigation: ensure the
  join event fires once per world entry and is idempotent.
- **`getSlotUnderMouse()` returns the JEI overlay region, not a vanilla slot** when JEI is present
  → the JEI provider should take priority over the native provider so hovering the JEI panel still
  resolves via JEI. Provider order (D1) handles this.
- **Per-version API drift** (slot APIs, event names, registry access) → out of scope here but noted
  for the port; this change targets 1.18.2 only.

## Migration Plan

1. Add provider interface + native and JEI implementations; register native always, JEI behind
   `ModList.isLoaded("jei")`.
2. Repoint `handleAddToQueue` at the provider lookup; remove its JEI import.
3. Add the world-join lifecycle listener that loads both managers; remove load calls from
   `CTPlugin.onRuntimeAvailable`.
4. Add the picker UI to `QueueManagementScreen`.
5. Flip dependency metadata to optional.
6. Verify both runtimes in the dev client: with JEI present (hover panel + slots + picker) and
   with JEI absent (slots + picker, managers load on join).

Rollback: revert the metadata flip to restore `mandatory=true`; the provider/lifecycle changes are
backward-compatible with JEI present and can remain.

## Open Questions

- Exact 1.18.2 event for client world-join used elsewhere in the codebase for the load hook
  (confirm during implementation).
- Whether the picker should also offer a quantity input or always add 1 (proposal assumes 1; revisit
  if desired).
