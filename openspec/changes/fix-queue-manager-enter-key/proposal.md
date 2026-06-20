## Why

Tracks GitHub issue **#75**. In the Queue Management screen, after clicking a button, pressing
Enter re-activates the last-clicked button. A focused widget retains focus and the Enter key
re-fires its action, causing accidental repeat operations (e.g. re-removing or re-adding an item).

## What Changes

- Prevent the Enter key from re-triggering the previously clicked button in
  `QueueManagementScreen`.
- Define intended Enter behavior: either it does nothing unless a text field is focused, or it
  confirms the focused control deliberately — not "replay the last click".
- Audit related focus handling (`setSendRepeatsToGui(true)` is set in `init()`) for the same
  class of bug across the screen's widgets.

## Capabilities

### New Capabilities

- `queue-manager-key-handling`: How keyboard input (notably Enter) is handled in the Queue
  Management screen so that key presses do not unintentionally replay button actions.

### Modified Capabilities

<!-- None recorded as formal specs yet. -->

## Impact

- **Code:** `client/screen/QueueManagementScreen` — `keyPressed`/widget focus handling; review
  button construction and whether buttons retain focus after a click.
- **UX:** removes a frustrating accidental-action bug; should not change intended click behavior.
- **No storage or networking change.**
