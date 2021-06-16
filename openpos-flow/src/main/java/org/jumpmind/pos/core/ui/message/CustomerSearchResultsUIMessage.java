package org.jumpmind.pos.core.ui.message;

import lombok.Data;

@Data
public class CustomerSearchResultsUIMessage<T extends SelectableItem> extends SelectionListUIMessage<T>{
    private String memberIcon;
    private String nonMemberIcon;

    public CustomerSearchResultsUIMessage(String uiMessageType) {
        super(uiMessageType);
    }
}
