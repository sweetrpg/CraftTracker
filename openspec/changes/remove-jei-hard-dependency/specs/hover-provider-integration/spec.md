## ADDED Requirements

### Requirement: Pluggable item-under-mouse providers

The mod SHALL resolve "the item under the mouse" through a provider abstraction rather than a
direct JEI call. A native provider backed by the vanilla container slot SHALL always be
available. Optional providers backed by other mods (e.g. JEI, and in future REI) SHALL be
available only when the corresponding mod is loaded.

#### Scenario: Native provider always present

- **WHEN** the add-to-queue action runs and no optional item-list mod is loaded
- **THEN** the native container-slot provider resolves the hovered item

#### Scenario: Optional provider used when its mod is present

- **WHEN** JEI is loaded and the player hovers an item in JEI's ingredient list overlay
- **THEN** the JEI provider resolves that item, including items the player does not currently possess

#### Scenario: Provider selection order

- **WHEN** more than one provider could resolve an item under the mouse
- **THEN** the mod queries providers in a defined order and uses the first that returns an item

### Requirement: JEI API code is isolated from no-JEI runtimes

All code that references JEI API types SHALL be confined to classes that are loaded only when
JEI is present, so that running without JEI does not cause class-loading failures.

#### Scenario: Running without JEI does not load JEI classes

- **WHEN** the game starts with Craft Tracker installed but JEI absent
- **THEN** no JEI API class is loaded and the mod initializes without `NoClassDefFoundError`

#### Scenario: Guarded access to the JEI provider

- **WHEN** the mod decides whether to register the JEI hover provider
- **THEN** it checks that JEI is loaded before referencing any JEI-backed class
