# AGEMTS.md

This file provides guidance to the robots when working with code in this repository.

## What this is

Craft Tracker is a **Minecraft Forge** mod (Java 17, Minecraft 1.18.2, Forge 40.2.17) that
lets players queue items they want to craft and build a shopping list of materials to gather.
JEI is a required runtime dependency; the queue is populated by hovering an item in JEI and
pressing the add-to-queue key.

This repo is the **`1.18` branch of a multi-version mod**. Parallel branches exist for `1.16`,
`1.19`, `1.20`, and `1.21` (see the CI trigger in `.github/workflows/ci-build.yml`). Changes
often need to be ported across branches; keep code version-specific only where the Minecraft/Forge
API forces it.

## Build & run

```bash
./gradlew build            # compile + package jar into build/libs/
./gradlew test             # run JUnit 5 (Jupiter) tests
./gradlew test --tests '<FQCN>'   # run a single test class
./gradlew client           # launch the Minecraft client (working dir: run/)
./gradlew server           # launch a dedicated server (working dir: run-server/)
./gradlew data             # run data generators, output to src/generated/resources/
```

- Java 17 is required (`.java-version` pins the JDK; `org.gradle.jvmargs=-Xmx4G` is needed for
  Minecraft decompilation). The Apple Silicon support is applied via a remote `applesilicon.gradle`.
- Mappings are **Parchment** over official MCP (`parchment_version` in `gradle.properties`).
- After editing key bindings, GUIs, or other client code, verify with `./gradlew client`. Tests
  cover only pure logic — most behavior is validated in-game.

### Versioning & release

- Build version resolves from the `MOD_VERSION` env var, else `.release-info/<base_mc_version>/VERSION`
  (e.g. `.release-info/1.18/VERSION`). Do not hand-edit these unless cutting a release.
- CI (`ci-build.yml`) auto-bumps the version using `.release-info/<branch>/BUMP_LEVEL` (default
  `patch`), commits the new VERSION, and publishes a prerelease GitHub release on every push to a
  version branch. CurseForge/Modrinth publishing is configured in `build.gradle` and keyed off
  `CHANGELOG/<base_mc_version>/current.md`.
- All inter-mod dependency versions/project IDs live in `gradle.properties`, not `build.gradle`.

## Architecture

All code is under `src/main/java/com/sweetrpg/crafttracker/`, organized by responsibility:

- **`CraftTracker.java`** — `@Mod` entry point. Wires up event-bus listeners and constructs the
  `SimpleChannel` (`HANDLER`) for networking. Client-only listeners are registered inside a
  `DistExecutor.runWhenOn(Dist.CLIENT, ...)` block — respect this client/server split; never call
  client-only classes (anything touching `Minecraft.getInstance()`) from common code paths that run
  on the server.
- **`common/`** — logic shared between client and server: `manager/`, `storage/`, `model/`,
  `network/`, `registry/`, `config/`, `advancement/`, `event/`, and `util/`.
- **`client/`** — client-only: `screen/` (GUIs), `overlay/` (HUD overlays), `event/` (key/screen
  input handling).
- **`data/`** — datagen providers (`CTLangProvider`, `CTAdvancementProvider`) run by the `data`
  Gradle task. Generated output lands in `src/generated/resources/` and is committed.
- **`integration/`** — third-party mod hooks. `AddonManager` runs a list of `Addon`s defensively
  (an addon that throws is dropped). `integration/jei/CTPlugin` is the JEI plugin.

### Two core features, same shape

Both the **crafting queue** and the **shopping list** follow the same pattern:

1. A singleton **Manager** (`CraftingQueueManager.INSTANCE`, `ShoppingListManager.INSTANCE`) holds
   state and is the entry point for all mutations.
2. A **Storage** class (`CraftingQueueStorage`, `ShoppingListStorage`) serializes that state to/from
   NBT. Managers persist to disk (`<storage path>/queue.nbt`) on every mutation via `save(player)`.
3. A **client overlay** (`CraftQueueOverlay`, `ShoppingListOverlay`) renders the HUD, with show/hide/
   dynamic state tracked in `common/Runtime.INSTANCE`.

### Crafting queue computation

`CraftingQueueManager` is the heart of the mod. When a product is added/removed it calls
`computeAll()`, which recursively walks each product's recipe (`computeRecipe`) to classify every
ingredient as an **intermediate product**, **raw material**, or **fuel**. Key behaviors to preserve:

- Recursion depth is bounded by `ConfigHandler.CLIENT.calculationDepth`; beyond it, ingredients are
  treated as raw materials.
- Ingredients are also treated as raw if they have no recipe or appear in `ConfigHandler.COMMON.rawMaterials`.
- A sub-recipe whose ingredients cross Minecraft namespaces is rejected (returns `null`) to avoid
  nonsensical cross-mod crafting chains.
- "Least expensive" recipe/ingredient selection is delegated to `common/util/calc/` (`RecipeCostCalculator`,
  `ItemCostCalculator`, `IngredientCostCalculator`) and `data/Costs.java` / `Multipliers.java`.
- Inventory checks (`InventoryUtil`) subtract what the player already holds — note this reads the
  **client** player, so computation is client-side.

### Networking

`common/network/PacketHandler.init()` registers packets on the `SimpleChannel`. Each packet
implements `IPacket<D>` (encode/decode/handle) with a separate data class under `network/packet/data/`.
Use `PacketHandler.sendToServer(...)` / `sendToPlayer(...)`. Advancements are granted server-side by
sending an `AdvancementData` packet from the client.

### Key bindings

`common/registry/ModKeyBindings` defines all `KeyMapping`s (all use `KeyConflictContext.GUI`).
`client/event/ClientEventHandler.onKeyInput` dispatches them. Default keys live in `ModKeyBindings`;
translation keys are centralized in `common/Constants`.

### Constants & translation keys

`common/Constants.java` is the single home for the mod ID, channel name, colors, and **every**
translation key (GUI strings, key bindings, config, advancements). Add new translation keys here
rather than inlining string literals, then add the actual text via `CTLangProvider` + regenerate
with `./gradlew data`.

## Code Exploration Policy

Always use jCodemunch-MCP tools for code navigation. Never fall back to Read, Grep, Glob, or Bash for code exploration.
**Exception:** Use `Read` when you need to edit a file — the agent harness requires a `Read` before `Edit`/`Write` will succeed. Use jCodemunch tools to *find and understand* code, then `Read` only the specific file you're about to modify.

**Start any session:**
1. `resolve_repo { "path": "." }` — confirm the project is indexed. If not: `index_folder { "path": "." }`
2. `suggest_queries` — when the repo is unfamiliar

**Finding code:**
- symbol by name → `search_symbols` (add `kind=`, `language=`, `file_pattern=`, `decorator=` to narrow)
- decorator-aware queries → `search_symbols(decorator="X")` to find symbols with a specific decorator (e.g. `@property`, `@route`); combine with set-difference to find symbols *lacking* a decorator (e.g. "which endpoints lack CSRF protection?")
- string, comment, config value → `search_text` (supports regex, `context_lines`)
- database columns (dbt/SQLMesh) → `search_columns`

**Reading code:**
- before opening any file → `get_file_outline` first
- one or more symbols → `get_symbol_source` (single ID → flat object; array → batch)
- symbol + its imports → `get_context_bundle`
- specific line range only → `get_file_content` (last resort)

**Repo structure:**
- `get_repo_outline` → dirs, languages, symbol counts
- `get_file_tree` → file layout, filter with `path_prefix`

**Relationships & impact:**
- what imports this file → `find_importers`
- where is this name used → `find_references`
- is this identifier used anywhere → `check_references`
- file dependency graph → `get_dependency_graph`
- what breaks if I change X → `get_blast_radius`
- what symbols actually changed since last commit → `get_changed_symbols`
- find unreachable/dead code → `find_dead_code`
- class hierarchy → `get_class_hierarchy`

## Session-Aware Routing

**Opening move for any task:**
1. `plan_turn { "repo": "...", "query": "your task description", "model": "<your-model-id>" }` — get confidence + recommended files; the `model` parameter narrows the exposed tool list to match your capabilities at zero extra requests.
2. Obey the confidence level:
   - `high` → go directly to recommended symbols, max 2 supplementary reads
   - `medium` → explore recommended files, max 5 supplementary reads
   - `low` → the feature likely doesn't exist. Report the gap to the user. Do NOT search further hoping to find it.

**Interpreting search results:**
- If `search_symbols` returns `negative_evidence` with `verdict: "no_implementation_found"`:
  - Do NOT re-search with different terms hoping to find it
  - Do NOT assume a related file (e.g. auth middleware) implements the missing feature (e.g. CSRF)
  - DO report: "No existing implementation found for X. This would need to be created."
  - DO check `related_existing` files — they show what's nearby, not what exists
- If `verdict: "low_confidence_matches"`: examine the matches critically before assuming they implement the feature

**After editing files:**
- If PostToolUse hooks are installed (Claude Code only), edited files are auto-reindexed
- Otherwise, call `register_edit` with edited file paths to invalidate caches and keep the index fresh
- For bulk edits (5+ files), always use `register_edit` with all paths to batch-invalidate

**Token efficiency:**
- If `_meta` contains `budget_warning`: stop exploring and work with what you have
- If `auto_compacted: true` appears: results were automatically compressed due to turn budget
- Use `get_session_context` to check what you've already read — avoid re-reading the same files

## Model-Driven Tool Tiering

Your jcodemunch-mcp server narrows the exposed tool list based on the model you are running as. To avoid wasting requests on primitives when a composite would do, always include `model="<your-model-id>"` in your opening `plan_turn` call.

Replace `<your-model-id>` with your active model:
- Claude Opus variants → `claude-opus-4-7` (or any `claude-opus-*`)
- Claude Sonnet variants → `claude-sonnet-4-6`
- Claude Haiku variants → `claude-haiku-4-5`
- GPT-4o / GPT-5 / o1 / Llama → use the model id as printed by your runner

The `model=` parameter rides on the existing `plan_turn` call — it does **not** add a separate tool invocation. If `plan_turn` is not appropriate for a non-code task, call `announce_model(model="...")` once instead.
