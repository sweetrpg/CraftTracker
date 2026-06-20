## ADDED Requirements

### Requirement: Queue requirements update after inventory changes

Craft Tracker SHALL refresh queue-derived needed quantities when the client player's inventory
changes after products have already been queued.

#### Scenario: Player acquires needed raw materials

- **WHEN** the player gains items that satisfy raw-material requirements for the current queue
- **THEN** Craft Tracker recomputes needed quantities after the inventory change is observed
- **AND** the queue and shopping list show fewer remaining needed items where applicable

#### Scenario: Player loses previously counted materials

- **WHEN** the player drops, consumes, moves away, or otherwise loses items that were reducing queue requirements
- **THEN** Craft Tracker recomputes needed quantities after the inventory change is observed
- **AND** the queue and shopping list show increased remaining needed items where applicable

### Requirement: Inventory-driven recompute is coalesced

Craft Tracker SHALL debounce or coalesce rapid inventory changes so bulk transfers and crafting
operations do not trigger redundant full recomputations for every slot mutation.

#### Scenario: Bulk transfer changes many slots

- **WHEN** a player shift-clicks or otherwise transfers many stacks in a short interval
- **THEN** Craft Tracker schedules a bounded recompute rather than recomputing once per slot change
- **AND** the final displayed quantities reflect the completed inventory state

#### Scenario: No effective inventory change

- **WHEN** an observed inventory event does not change the item identities or counts relevant to the queue
- **THEN** Craft Tracker avoids unnecessary recomputation where practical

### Requirement: Inventory refresh preserves player-controlled queue state

Inventory-driven recomputation SHALL update derived quantities without changing the player's
queued products or explicit selections.

#### Scenario: Recompute after inventory change

- **WHEN** Craft Tracker refreshes because inventory contents changed
- **THEN** the set of queued products remains unchanged
- **AND** selected recipes, quantities, and persisted queue data are not overwritten by the refresh

#### Scenario: Shopping list follows recomputed queue requirements

- **WHEN** the shopping list was populated from the crafting queue and the inventory-driven recompute changes needed quantities
- **THEN** the shopping-list view refreshes its remaining-needed amounts consistently with the recomputed queue
