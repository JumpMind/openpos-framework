package org.jumpmind.pos.core.ui.message;

import lombok.Data;

@Data
public class SelectableItem {
    protected boolean selected = false;
    protected boolean enabled = true;
}