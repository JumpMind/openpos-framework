package org.jumpmind.pos.core.ui.message;

import org.jumpmind.pos.core.model.DisplayProperty;
import org.jumpmind.pos.core.ui.UIMessage;

import java.util.ArrayList;
import java.util.List;

public class ItemDetailUIMessage extends UIMessage {

    private String itemName;
    private String imageUrl;
    private List<DisplayProperty> itemProperties;

    public ItemDetailUIMessage() {
        setScreenType(UIMessageType.ITEM_DETAIL);
    }


    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public List<DisplayProperty> getItemProperties() {
        return itemProperties;
    }

    public void setItemProperties(List<DisplayProperty> itemProperties) {
        this.itemProperties = itemProperties;
    }

    public void addItemProperty(DisplayProperty property) {
        if( itemProperties == null ) {
            itemProperties = new ArrayList<>();
        }

        itemProperties.add(property);
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }


}
