# queue-data-lifecycle Specification

## Purpose
TBD - created by archiving change remove-jei-hard-dependency. Update Purpose after archive.
## Requirements
### Requirement: Queue and shopping list load independent of JEI

The crafting queue and shopping list SHALL be loaded for the client player on a JEI-independent
client lifecycle event (e.g. client login / world join), not from JEI's runtime-available
callback. Persisted data SHALL load whether or not JEI is installed.

#### Scenario: Joining a world without JEI

- **WHEN** the player joins a world with JEI absent
- **THEN** the crafting queue and shopping list are loaded from disk and reflect their previously saved state

#### Scenario: Joining a world with JEI present

- **WHEN** the player joins a world with JEI installed
- **THEN** the crafting queue and shopping list are loaded exactly once, with no dependence on JEI's runtime callback

### Requirement: JEI dependency is optional

The mod's dependency metadata SHALL declare JEI as optional rather than mandatory, and the mod
SHALL load successfully when JEI is not installed.

#### Scenario: Loading with JEI absent

- **WHEN** the game loads Craft Tracker without JEI installed
- **THEN** the mod loads without a missing-dependency error and all non-JEI features are available

