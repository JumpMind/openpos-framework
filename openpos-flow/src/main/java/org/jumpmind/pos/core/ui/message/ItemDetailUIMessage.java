package org.jumpmind.pos.core.ui.message;

import org.jumpmind.pos.core.model.DisplayProperty;
import org.jumpmind.pos.core.ui.AssignKeyBindings;
import org.jumpmind.pos.core.ui.UIMessage;

import java.util.ArrayList;
import java.util.List;

@AssignKeyBindings
public class ItemDetailUIMessage extends UIMessage {

    private String itemName;
    private List<String> imageUrls;
    private List<DisplayProperty> itemProperties;

    public ItemDetailUIMessage() {
        setScreenType(UIMessageType.ITEM_DETAIL);
    }


    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public void addImageUrl(String imageUrl) {
        if(imageUrls == null) {
            this.imageUrls = new ArrayList<>();
        }
        this.imageUrls.add(imageUrl);
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
