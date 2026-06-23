# Design: open-recipe-in-item-viewer

## Overview

When a player hovers an entry in the Craft Queue HUD overlay and presses a configurable key,
Craft Tracker calls the active item-viewer integration to show recipes for that item. The action
is routed through the `Addon` abstraction so JEI API code stays quarantined in `integration/jei/`
and the feature is silently inert when no supported viewer is installed.

## Components

### 1. `Addon` interface — new capability method

Add a default no-op method:

```java
default void showRecipesFor(ItemStack stack) {
}
```

Addons that support recipe display override this method. The no-op default ensures unknown or
non-viewer addons compile and run without change.

### 2. `JeiAddon` (new) — `integration/jei/JeiAddon.java`

Implements `Addon`. Responsible for the JEI-specific recipe-display call.

- `getName()` → `"jei"`
- `getMods()` → `List.of("jei")`
- `showRecipesFor(ItemStack stack)`:

```java
IJeiRuntime runtime = CTPlugin.jeiRuntime;
if(runtime ==null)return;
IFocus<ItemStack> focus = runtime.getJeiHelpers()
        .getFocusFactory()
        .createFocus(IRecipeCategory.RecipeIngredientRole.OUTPUT, VanillaTypes.ITEM_STACK, stack);
runtime.

getRecipesGui().

showTypes(List.of(focus));
```

`CTPlugin.jeiRuntime` is already a `public static` field set by `onRuntimeAvailable`, so
`JeiAddon` can read it without a back-reference to `CTPlugin`.

### 3. `AddonManager` — register `JeiAddon` + dispatch method

- Add `new JeiAddon()` to `ADDONS`.
- Add:

```java
public static void showRecipesFor(ItemStack stack) {
    doWork(RUN, Addon::shouldLoad,
            addon -> addon.showRecipesFor(stack),
            (addon, e) -> CraftTracker.LOGGER.warn("Failed to show recipes via {}", addon.getName()));
}
```

`doWork` already swallows `RuntimeException` per addon, satisfying the spec requirement that a
failure must not propagate as an unhandled exception.

### 4. `CraftQueueOverlay` — hover tracking

Add a package-visible static field:

```java
static ItemStack hoveredItem = null;
```

During the render lambda, after computing each row's `yPos`, compare the raw mouse cursor
position (via `Minecraft.getInstance().mouseHandler`) against the item icon bounding box
`(x + SECTION_X_OFFSET, yPos, 16, 16)` for every product and intermediate row. Set
`hoveredItem` to the matching `ItemStack`, or `null` if none match.

Mouse coordinates from `mouseHandler` are in physical pixels; divide by
`mc.getWindow().getGuiScale()` to get GUI coordinates before comparing.

### 5. `Constants` — new translation key

```java
public static final String TRANSLATION_KEY_BINDINGS_SHOW_RECIPE_TITLE = "key.crafttracker.showRecipe";
```

### 6. `ModKeyBindings` — new key binding

```java
public static final KeyMapping SHOW_RECIPE_MAPPING = new KeyMapping(
        Constants.TRANSLATION_KEY_BINDINGS_SHOW_RECIPE_TITLE,
        KeyConflictContext.GUI,
        InputConstants.Type.KEYSYM,
        InputConstants.KEY_R,
        Constants.KEY_BINDINGS_CATEGORY_TITLE
);
```

Register it in `init()` with `ClientRegistry.registerKeyBinding(SHOW_RECIPE_MAPPING)`.

### 7. `ClientEventHandler.onKeyInput` — dispatch

In the `screen != null` branch, add a check after the existing `ADD_TO_QUEUE_MAPPING` block:

```java
if(ModKeyBindings.SHOW_RECIPE_MAPPING.matches(event.getKey(),event.

getScanCode())){
ItemStack hovered = CraftQueueOverlay.hoveredItem;
    if(hovered !=null){
        AddonManager.

showRecipesFor(hovered);
    }
            }
```

No screen-type guard needed — `hoveredItem` is only non-null when the mouse is actually over
a CT overlay entry, which implicitly gates the action.

### 8. `CTLangProvider` — English, en-GB, German strings

Add to all three locale methods:

```java
add(Constants.TRANSLATION_KEY_BINDINGS_SHOW_RECIPE_TITLE, "Show Recipe");
```

Run `./gradlew data` after to regenerate the committed JSON lang files.

## What is NOT in scope

- Shopping list overlay hover (raw materials, fuel) — items there have no crafting recipe to
  show in an interesting way; can be added in a follow-up.
- REI integration — deferred to a future addon implementation.
- Click (mouse button) as the trigger — keyboard-only for now, consistent with existing bindings.
