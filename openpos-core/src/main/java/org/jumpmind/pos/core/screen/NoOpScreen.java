package org.jumpmind.pos.core.screen;

public class NoOpScreen extends Screen {

    private static final long serialVersionUID = 1L;

    public NoOpScreen() {
        this.setType(ScreenType.NoOp);
    }

}
