## Why

Tracks GitHub issue **#28**. Players want a physical, shareable representation of the shopping
list — "a shopping list book/paper, like Create's materials list, that can be shared with another
player." Today the shopping list lives only in each player's client-side overlay/storage and can't
be handed to someone else.

## What Changes

- Add an in-world **item** (book or paper) that captures a snapshot of the player's current
  shopping list.
- Writing the item serializes the shopping-list contents into the item's NBT; reading/using it
  displays those contents.
- The item can be traded/dropped/placed like any item, so another player can receive it and view
  (and optionally import) the list.
- Optionally allow importing the item's contents into the receiver's own shopping list.

## Capabilities

### New Capabilities

- `shopping-list-item`: A craftable/obtainable item that stores a shopping-list snapshot in NBT,
  can be shared between players, and can be viewed and optionally imported.

### Modified Capabilities

<!-- None recorded as formal specs yet. -->

## Impact

- **Code:** new item registration in `common/registry`, item NBT (de)serialization reusing
  `ShoppingListStorage` logic, a view screen in `client/screen`, recipe/datagen via
  `data/` providers, translation keys in `common/Constants` + `CTLangProvider`.
- **Server/client split:** the item and its NBT are server-authoritative; viewing is client-side.
  Importing into the local shopping list is a client action (possibly via a packet through
  `PacketHandler`).
- **Open questions (design):** is the item crafted, given, or written at a station? Snapshot vs.
  live-linked list? Cross-version item-model considerations.
