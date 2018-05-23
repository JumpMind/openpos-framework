package org.jumpmind.pos.core.flow;

import org.jumpmind.pos.core.screen.Screen;

public interface IScreenInterceptor {

    Screen intercept(Screen screen);
    
}
