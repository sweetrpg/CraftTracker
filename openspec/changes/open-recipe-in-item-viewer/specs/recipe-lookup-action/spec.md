## ADDED Requirements

### Requirement: Queue entries can request recipe lookup in an installed item viewer

Craft Tracker SHALL let players request recipe lookup for a product, intermediate, raw material,
or fuel entry shown by Craft Tracker when an item-viewer integration that supports recipe lookup
is installed.

#### Scenario: Opening a recipe for a queued product

- **WHEN** the player hovers or selects a queued product entry and performs the recipe-lookup action
- **THEN** Craft Tracker asks the active item-viewer integration to show recipes for that product
- **AND** the current Minecraft screen remains open unless the integration itself changes focus

#### Scenario: Opening a recipe for an intermediate item

- **WHEN** the player performs the recipe-lookup action on an intermediate item entry
- **THEN** the item viewer opens recipes for that intermediate item rather than for the final queued product

### Requirement: Recipe lookup is routed through optional integrations

Craft Tracker SHALL route recipe lookup through an optional integration abstraction so JEI,
future item viewers, and no-viewer runtimes can be handled without direct common-code coupling.

#### Scenario: JEI is installed

- **WHEN** JEI is loaded and its runtime is available
- **THEN** the JEI integration handles recipe lookup using JEI's recipe-display API
- **AND** JEI API classes remain confined to JEI-only integration code

#### Scenario: No supported item viewer is installed

- **WHEN** the player attempts recipe lookup and no supported item viewer is available
- **THEN** Craft Tracker does not crash
- **AND** the action is hidden, disabled, or produces no queue mutation

#### Scenario: Integration cannot show the requested item

- **WHEN** the active item-viewer integration declines or fails to show recipes for the requested item
- **THEN** Craft Tracker leaves queue and shopping-list state unchanged
- **AND** the failure does not propagate as an unhandled exception
