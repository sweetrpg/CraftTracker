## ADDED Requirements

### Requirement: Players can write a shopping-list snapshot to an item

Craft Tracker SHALL provide an in-world item that can store a snapshot of the player's current
shopping list in item NBT.

#### Scenario: Writing the current shopping list

- **WHEN** the player uses the write-shopping-list action while they have a non-empty shopping list and an eligible list item
- **THEN** Craft Tracker serializes the current shopping-list contents into that item's NBT
- **AND** the stored data is independent of later changes to the player's live shopping list

#### Scenario: Writing an empty shopping list

- **WHEN** the player attempts to write a shopping-list item while their shopping list is empty
- **THEN** Craft Tracker does not create a misleading populated snapshot
- **AND** the player receives no corrupted or partially written item data

### Requirement: Shopping-list items are shareable between players

The shopping-list item SHALL behave like a normal item so it can be dropped, traded, stored, and
read by another player.

#### Scenario: Another player receives the item

- **WHEN** another player obtains a shopping-list item containing snapshot NBT
- **THEN** they can view the stored shopping-list contents
- **AND** the contents match the snapshot written by the original player

#### Scenario: Item moves through inventories

- **WHEN** the shopping-list item is moved through inventories, containers, drops, or trades
- **THEN** its shopping-list NBT remains attached to the item stack

### Requirement: Players can view and optionally import a shared list

Craft Tracker SHALL provide a way to view a shopping-list item and, if implemented, import its
snapshot into the receiving player's shopping list intentionally.

#### Scenario: Viewing a shared list

- **WHEN** the player opens or uses a shopping-list item with valid snapshot data
- **THEN** Craft Tracker displays the stored item names, quantities, and relevant breakdown information
- **AND** viewing does not mutate the player's live shopping list

#### Scenario: Importing a shared list

- **WHEN** the player chooses to import the snapshot
- **THEN** Craft Tracker applies the stored entries to the player's shopping list according to the documented merge or replace behavior
- **AND** the import is an explicit action, not a side effect of viewing

#### Scenario: Invalid snapshot data

- **WHEN** a shopping-list item contains missing, malformed, or incompatible snapshot NBT
- **THEN** Craft Tracker handles the item without crashing
- **AND** the invalid data is not imported into the live shopping list
