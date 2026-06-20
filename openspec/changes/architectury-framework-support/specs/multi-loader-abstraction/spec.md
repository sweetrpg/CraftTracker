## ADDED Requirements

### Requirement: Evaluate a multi-loader architecture before migration

Craft Tracker SHALL evaluate a multi-loader architecture before committing to a full migration.
The evaluation SHALL identify which code can remain loader-agnostic and which code must stay
behind Forge, Fabric, NeoForge, Quilt, or Architectury-specific adapters.

#### Scenario: Classifying existing code by loader dependency

- **WHEN** the migration spike reviews managers, storage, models, calculation utilities, networking, registries, events, key bindings, rendering hooks, and datagen
- **THEN** it records each area as loader-agnostic, loader-adapted, or loader-specific
- **AND** it identifies the adapter API needed for each loader-adapted area

#### Scenario: Deciding whether to adopt Architectury

- **WHEN** the spike compares Architectury against a custom common-plus-loader split
- **THEN** it records a go/no-go recommendation with expected branch impact, build impact, and cross-loader maintenance cost
- **AND** implementation does not begin until that recommendation is documented

### Requirement: Separate common logic from loader integrations

If a multi-loader strategy is adopted, Craft Tracker SHALL keep core queue, shopping-list,
storage, model, and calculation behavior in common code while isolating loader APIs behind
adapter boundaries.

#### Scenario: Common code avoids loader-only classes

- **WHEN** common queue computation, storage, and model classes are compiled
- **THEN** they do not directly reference Forge, Fabric, NeoForge, Quilt, Minecraft client-only, or Architectury loader entry-point classes
- **AND** loader-specific behavior is reached through explicit adapter interfaces

#### Scenario: Loader-specific client wiring remains isolated

- **WHEN** key bindings, screen events, overlays, item-viewer hooks, or client lifecycle handlers are registered
- **THEN** the registration is performed in the relevant loader/client adapter
- **AND** server/common initialization cannot classload client-only adapter code

### Requirement: Preserve feature parity across supported loaders

The multi-loader build SHALL preserve Craft Tracker's existing player-facing behavior on every
supported loader unless a loader cannot expose the required API.

#### Scenario: Queue and shopping-list behavior on each loader

- **WHEN** the mod is built and run on a supported loader
- **THEN** players can add queue items, compute intermediate/raw/fuel requirements, view overlays, manage the shopping list, and persist data across world joins
- **AND** any loader-specific limitation is documented as an explicit compatibility gap

#### Scenario: Optional integrations remain optional

- **WHEN** an optional item-viewer integration such as JEI is absent on a supported loader
- **THEN** Craft Tracker still loads and the native queue workflows remain available
