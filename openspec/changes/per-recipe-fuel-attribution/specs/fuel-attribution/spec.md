## ADDED Requirements

### Requirement: Fuel requirements retain their owning queued product

Craft Tracker SHALL attribute fuel requirements to the queued product or recipe that caused
them, while preserving the existing queue-wide aggregate fuel total.

#### Scenario: Smelting product requires fuel

- **WHEN** a queued product or one of its selected sub-recipes requires furnace fuel
- **THEN** the computed fuel entry records the owning queued product and recipe context
- **AND** the same fuel quantity contributes to the aggregate fuel total

#### Scenario: Multiple queued products require the same fuel

- **WHEN** two queued products both require the same fuel item
- **THEN** Craft Tracker can show each product's fuel requirement separately
- **AND** the aggregate total combines both requirements for shopping-list summary purposes

### Requirement: Fuel attribution survives queue recomputation

Fuel attribution SHALL be recalculated whenever the queue is recomputed so that product-specific
fuel details stay consistent with recipe selection, inventory changes, and queue mutations.

#### Scenario: Product removed from queue

- **WHEN** a queued product with attributed fuel is removed
- **THEN** its attributed fuel entries are removed from the per-product breakdown
- **AND** the aggregate fuel total is recomputed without that product's fuel

#### Scenario: Recipe selection changes fuel need

- **WHEN** a product's chosen recipe changes from a fuel-using recipe to a non-fuel recipe, or vice versa
- **THEN** Craft Tracker updates both the product-specific fuel breakdown and the aggregate fuel total

### Requirement: UI surfaces per-product fuel details

Craft Tracker SHALL surface attributed fuel details in the queue UI or shopping-list breakdown
so players can see why fuel is needed.

#### Scenario: Viewing fuel details

- **WHEN** the queue contains fuel requirements
- **THEN** the UI shows the aggregate fuel summary
- **AND** it provides a per-product or per-recipe breakdown that identifies the item responsible for each fuel requirement
