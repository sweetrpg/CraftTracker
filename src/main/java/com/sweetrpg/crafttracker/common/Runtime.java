package com.sweetrpg.crafttracker.common;

/**
 * Container class for runtime information.
 */
public class Runtime {

    public static Runtime INSTANCE = new Runtime();

    public enum OverlayState {
        SHOW,
        HIDE,
        SUPPRESS,
        DYNAMIC,
    }

    public OverlayState queueOverlayRequestedState;
    public OverlayState shoppingOverlayRequestedState;

    public Runtime() {
        init();
    }

    private void init() {
        this.queueOverlayRequestedState = OverlayState.DYNAMIC;
        this.shoppingOverlayRequestedState = OverlayState.DYNAMIC;
    }
}
