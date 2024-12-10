package com.sweetrpg.crafttracker.common.lib;

/**
 * Container class for runtime information.
 */
public class CTRuntime {

    public static CTRuntime INSTANCE = new CTRuntime();

    public enum OverlayState {
        SHOW,
        HIDE,
        SUPPRESS,
        DYNAMIC,
    }

    public OverlayState queueOverlayRequestedState;
    public OverlayState shoppingOverlayRequestedState;

    public CTRuntime() {
        init();
    }

    private void init() {
        this.queueOverlayRequestedState = OverlayState.DYNAMIC;
        this.shoppingOverlayRequestedState = OverlayState.DYNAMIC;
    }
}
