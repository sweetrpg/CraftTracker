## ADDED Requirements

### Requirement: Queued products can store an explicit recipe selection

Craft Tracker SHALL allow each queued product to store an optional selected recipe identifier
that overrides automatic least-expensive recipe selection during queue computation.

#### Scenario: Product has no explicit selection

- **WHEN** a queued product does not have a selected recipe
- **THEN** Craft Tracker continues to choose the recipe using the existing automatic least-expensive behavior

#### Scenario: Product has an explicit selection

- **WHEN** a queued product has a selected recipe identifier and that recipe is still valid for the product
- **THEN** queue computation uses the selected recipe instead of the automatic least-expensive recipe
- **AND** intermediates, raw materials, and fuel are computed from the selected recipe

#### Scenario: Selected recipe is no longer valid

- **WHEN** a queued product's stored selected recipe no longer exists or no longer produces that product
- **THEN** Craft Tracker falls back to automatic selection
- **AND** the UI indicates that the previous selection is unavailable or clears it during recomputation

### Requirement: Players can choose among available recipes for a queued product

Craft Tracker SHALL expose the available recipes for a queued product and let the player choose
which recipe drives computation.

#### Scenario: Opening recipe choices

- **WHEN** the player opens recipe selection for a queued product with multiple recipes
- **THEN** the UI lists the available recipes with enough ingredient/output information to distinguish them
- **AND** the current automatic or explicit selection is visible

#### Scenario: Selecting a recipe

- **WHEN** the player selects a recipe for a queued product
- **THEN** Craft Tracker stores that recipe as the product's explicit selection
- **AND** it recomputes intermediates, raw materials, shopping-list quantities, and fuel

### Requirement: Recipe selection persists backward-compatibly

Recipe selection SHALL persist with queued products without breaking queues saved before this
field existed.

#### Scenario: Loading an old queue entry

- **WHEN** Craft Tracker loads a queued product saved without a selected recipe field
- **THEN** the product loads successfully
- **AND** computation uses automatic recipe selection

#### Scenario: Reloading a selected recipe

- **WHEN** Craft Tracker saves and reloads a queued product with a selected recipe
- **THEN** the same recipe selection is restored if the recipe remains valid
