## ADDED Requirements

### Requirement: Minecraft 1.21 port preserves Craft Tracker feature parity

The Minecraft 1.21 branch SHALL preserve Craft Tracker's existing player-facing behavior from
the maintained version branches unless a platform API change forces a documented divergence.

#### Scenario: Queue workflow parity

- **WHEN** Craft Tracker runs on Minecraft 1.21
- **THEN** players can add products to the crafting queue, compute intermediates, raw materials, and fuel, remove products, and persist the queue across world joins
- **AND** behavior matches the source branch except for documented 1.21 platform differences

#### Scenario: Shopping-list workflow parity

- **WHEN** Craft Tracker runs on Minecraft 1.21
- **THEN** players can populate, view, mutate, save, and reload the shopping list with the same semantics as the source branch

#### Scenario: Client overlay parity

- **WHEN** the queue or shopping-list overlay is visible on Minecraft 1.21
- **THEN** it renders equivalent content using the 1.21 client rendering APIs
- **AND** rendering migrations such as `GuiGraphics` do not change the displayed values

### Requirement: Platform and dependency metadata target Minecraft 1.21

The port SHALL update platform, mapping, loader, dependency, release, and CI metadata so the
1.21 branch builds and publishes against the intended 1.21 loader ecosystem.

#### Scenario: Building the 1.21 branch

- **WHEN** the build runs on the 1.21 branch
- **THEN** Gradle resolves Minecraft 1.21-compatible loader, mapping, JEI, and related dependency versions
- **AND** the produced mod jar declares compatibility with the intended 1.21 platform

#### Scenario: CI recognizes the 1.21 branch

- **WHEN** CI runs for the 1.21 branch
- **THEN** version metadata, changelog paths, and release automation use the 1.21 release-info and changelog directories

### Requirement: Ported integrations obey 1.21 client/server boundaries

The port SHALL adapt changed 1.21 APIs while preserving the existing common/client/server split.

#### Scenario: Server loads without client-only classes

- **WHEN** the dedicated server starts with the 1.21 build installed
- **THEN** Craft Tracker does not classload client-only screens, overlays, key mappings, or item-viewer UI integrations

#### Scenario: Optional item-viewer integration on 1.21

- **WHEN** the configured item viewer is absent on Minecraft 1.21
- **THEN** Craft Tracker still loads and non-viewer queue workflows remain available
