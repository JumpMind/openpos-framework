package org.jumpmind.pos.core.screen;

import java.io.Serializable;

/**
 * Indicates that the icon is hosted locally on the client and should be resolved
 * in the context of the client UI.
 */
public class LocalIcon implements IIcon, Serializable {
    private static final long serialVersionUID = 1L;
    
    private String name;
    
    public LocalIcon(String name) {
        this.name = String.format("openpos_%s", name);
    }
    
    @Override
    public String getName() {
        return name;
    }

}
