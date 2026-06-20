## Why

JEI is declared a **mandatory** dependency (`mods.toml`, CurseForge/Modrinth metadata), yet
the mod's actual coupling to JEI is tiny: a single feature method reads "the item under the
mouse" from JEI's ingredient-list overlay, and the JEI plugin's `onRuntimeAvailable` is the
*only* place the crafting queue and shopping list are loaded from disk. This forces every user
to install JEI even though the queue, shopping list, overlays, computation, and key bindings are
all JEI-independent. Dropping the hard dependency makes Craft Tracker usable standalone while
keeping the full feature set when JEI (or another item-list mod) is present.

## What Changes

- **BREAKING (dependency):** JEI changes from a mandatory to an optional dependency
  (`mods.toml` `mandatory=false`; CurseForge/Modrinth `requiredDependency`/`required.project` →
  optional). The mod loads and runs with JEI absent.
- Add-to-queue from hovering now reads the hovered item from the **vanilla container slot under
  the mouse** (`AbstractContainerScreen.getSlotUnderMouse()`), so it works in any container view
  (inventory, crafting table, chests, creative tabs) with no dependency.
- JEI becomes an **optional hover provider** behind an interface, accessed through the existing
  `integration`/`Addon` layer, with room for additional providers (e.g. REI). When present, JEI
  still supplies full-registry hover; when absent, native slot hover is used.
- The crafting queue and shopping list are **loaded on a JEI-independent client lifecycle event**
  (e.g. client login / world join) instead of inside `CTPlugin.onRuntimeAvailable`, so saved data
  loads whether or not JEI is installed.
- The Queue Management screen gains a **native item lookup** (search + browse the item registry,
  click to add directly), backfilling the "queue an item I don't currently own" workflow that JEI
  hover previously provided.
- All JEI-API-referencing code is **quarantined** so JEI classes are never classloaded when JEI is
  absent (avoids `NoClassDefFoundError`).

## Capabilities

### New Capabilities

- `queue-item-input`: How items are added to the crafting queue from the UI — adding the hovered
  item from any vanilla container view, and adding an item directly via search/browse in the Queue
  Management screen.
- `hover-provider-integration`: A pluggable abstraction for "the item under the mouse" with a
  native vanilla provider always available and optional mod-backed providers (JEI, future REI)
  selected at runtime based on which mods are loaded.
- `queue-data-lifecycle`: When the crafting queue and shopping list are loaded and saved, decoupled
  from any optional integration so persisted data is available regardless of JEI.

### Modified Capabilities

<!-- None — openspec/specs/ is empty; all behavior here is newly captured. -->

## Impact

- **Dependency declarations:** `src/main/resources/META-INF/mods.toml`, `build.gradle`
  (CurseForge `requiredDependency 'jei'` → optional; Modrinth `required.project "jei"` → optional;
  `compileOnly ...:api` already correct, `runtimeOnly` kept for dev).
- **Code:**
  - `client/event/ClientEventHandler#handleAddToQueue` — replace JEI overlay read with a hover
    provider lookup.
  - `integration/jei/CTPlugin#onRuntimeAvailable` — remove manager loading; keep only optional
    runtime wiring. Quarantine JEI-API references.
  - New hover-provider interface + native and JEI implementations under `integration/`.
  - New lifecycle listener (client login/world join) in `CraftTracker`/client event handling to
    load managers.
  - `client/screen/QueueManagementScreen` — add item search/browse UI calling
    `CraftingQueueManager.addProduct(player, itemId, qty)` (already accepts a plain `ResourceLocation`).
- **Cross-version:** This pattern (optional dep + native hover + picker) will need porting to the
  `1.16`/`1.19`/`1.20`/`1.21` branches; vanilla slot APIs and event names differ per version.
- **No data format change:** queue/shopping-list NBT storage is unchanged.
