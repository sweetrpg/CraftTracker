package com.sweetrpg.crafttracker.common.lib;

import com.sweetrpg.crafttracker.common.config.ConfigHandler;

public class CTRuntime {

    public static CTRuntime INSTANCE = new CTRuntime();

    public enum OverlayState {
        SHOW,
        HIDE,
        SUPPRESS,
        DO_NOT_CARE,
    }

    public OverlayState queueOverlayRequestedState;
    public OverlayState shoppingOverlayRequestedState;

    public CTRuntime() {
        init();
    }

    private void init() {
        this.queueOverlayRequestedState = OverlayState.DO_NOT_CARE;
        this.shoppingOverlayRequestedState = OverlayState.DO_NOT_CARE;
    }
}
