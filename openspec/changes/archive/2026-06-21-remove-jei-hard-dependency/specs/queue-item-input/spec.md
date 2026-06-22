## ADDED Requirements

### Requirement: Add hovered item to queue from any container view

The mod SHALL add the item currently under the mouse to the crafting queue when the
add-to-queue key is pressed while a vanilla container screen is open, without requiring JEI.
The hovered item SHALL be resolved from the container slot under the mouse so the action works
in the inventory, crafting table, and other container views.

#### Scenario: Hovering an occupied slot in the inventory

- **WHEN** the player opens the inventory, hovers a slot containing an item, and presses the add-to-queue key
- **THEN** that item is added to the crafting queue with quantity 1
- **AND** the queue-item advancement is granted

#### Scenario: Hovering an empty slot

- **WHEN** the player presses the add-to-queue key while the slot under the mouse is empty
- **THEN** no item is added to the queue and no error is shown

#### Scenario: JEI is not installed

- **WHEN** JEI is absent and the player hovers an item in a container view and presses the add-to-queue key
- **THEN** the item is added to the queue using the native container slot, exactly as it would with JEI installed

### Requirement: Add an item to the queue directly from the Queue Management screen

The Queue Management screen SHALL let the player find any registered item by searching and/or
browsing and add it to the crafting queue directly, without needing to hover it in a container
or in JEI. This provides the full-registry add capability when the player does not currently
possess the item.

#### Scenario: Searching for and adding an item

- **WHEN** the player opens the Queue Management screen, enters a search term, and selects a matching item
- **THEN** that item is added to the crafting queue
- **AND** the queue recomputes its intermediates, raw materials, and fuel

#### Scenario: Adding an item the player does not own

- **WHEN** the player selects an item that is not present in any of their containers
- **THEN** the item is still added to the queue, since the picker browses the item registry rather than the player's inventory
