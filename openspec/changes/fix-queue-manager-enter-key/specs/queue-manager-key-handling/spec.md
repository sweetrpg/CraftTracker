## ADDED Requirements

### Requirement: Enter does not replay the last clicked queue-management button

The Queue Management screen SHALL prevent the Enter key from unintentionally activating the
button that was most recently clicked. Keyboard activation SHALL only occur when the currently
focused widget is intentionally allowed to handle Enter.

#### Scenario: Clicking a button then pressing Enter

- **WHEN** the player clicks a button in the Queue Management screen and then presses Enter without deliberately focusing that button by keyboard navigation
- **THEN** the button action is not invoked a second time
- **AND** no queue item is accidentally added, removed, cleared, or modified

#### Scenario: Repeated Enter after a destructive click

- **WHEN** the player clicks a destructive or quantity-changing button and presses Enter repeatedly
- **THEN** the original clicked action is not replayed by retained button focus
- **AND** the queue state changes only from the original click

### Requirement: Text input keeps deliberate Enter behavior

The Queue Management screen SHALL preserve expected Enter behavior for text-focused controls,
such as search fields, while still suppressing accidental button replay.

#### Scenario: Enter while search field is focused

- **WHEN** the search field or another text-input control is focused and the player presses Enter
- **THEN** the screen handles Enter according to that control's documented behavior
- **AND** the previously clicked button is not activated

#### Scenario: Enter with no eligible focused control

- **WHEN** no text input or explicitly keyboard-activatable control is focused and the player presses Enter
- **THEN** the key press is consumed or ignored without mutating the queue

### Requirement: Focus is normalized after pointer activation

Pointer-clicking a Queue Management button SHALL leave screen focus in a state that cannot cause
later keyboard input to repeat the clicked action accidentally.

#### Scenario: Button focus after mouse click

- **WHEN** the player activates a Queue Management button with the mouse
- **THEN** the screen clears or redirects focus so that Enter does not target that button by default
