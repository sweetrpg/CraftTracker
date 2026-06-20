## ADDED Requirements

### Requirement: Queue item details include recipe count

Craft Tracker SHALL expose the number of distinct craftable recipes available for a queued
product in the queue UI.

#### Scenario: Product has multiple recipes

- **WHEN** a queued product has more than one craftable recipe
- **THEN** its tooltip or detail label shows the recipe count
- **AND** the count reflects recipes considered by Craft Tracker's recipe-selection and cost logic

#### Scenario: Product has no recipe

- **WHEN** a queued product has no craftable recipe and is treated as raw material
- **THEN** its tooltip or detail label indicates that no recipe is available or omits the recipe-count value without showing a misleading count

### Requirement: Queue item details include recipe cost range

Craft Tracker SHALL expose the minimum and maximum recipe cost for a queued product when more
than one candidate recipe cost is available.

#### Scenario: Multiple recipes have different costs

- **WHEN** Craft Tracker computes recipe costs for a queued product and the cheapest and most expensive recipes differ
- **THEN** the product tooltip or detail label shows a min-to-max cost range
- **AND** the currently auto-selected or manually selected recipe remains identifiable

#### Scenario: All recipes have the same cost

- **WHEN** all candidate recipes for a product have the same computed cost
- **THEN** the UI may show a single cost value instead of a range
- **AND** it does not imply a nonexistent cheaper or more expensive alternative

### Requirement: Tooltip data is computed outside per-frame rendering

Recipe count and cost-range data SHALL be computed or cached during queue computation, not by
performing full recipe-cost scans every render frame.

#### Scenario: Rendering queue overlay repeatedly

- **WHEN** the queue overlay renders across multiple frames without queue, inventory, or recipe-selection changes
- **THEN** Craft Tracker reuses previously computed tooltip details
- **AND** it does not recompute every recipe's full cost range each frame
