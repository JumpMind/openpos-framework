package org.jumpmind.pos.core.ui;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class ActionItemGroup implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String title;
    private String keybind;
    private String selectedAction;
    @Builder.Default
    private List<ActionItem> actionItems= new ArrayList<>();
    
}
