package org.jumpmind.pos.core.ui.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jumpmind.pos.core.model.DisplayProperty;
import org.jumpmind.pos.core.ui.AssignKeyBindings;
import org.jumpmind.pos.core.ui.UIMessage;

import java.util.ArrayList;
import java.util.List;

@AssignKeyBindings
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemDetailUIMessage extends UIMessage {

    private String itemName;
    private String summary;
    private List<String> imageUrls;
    private List<DisplayProperty> itemProperties;

    public void addItemProperty(DisplayProperty property){
        if(itemProperties == null){
            itemProperties = new ArrayList<>();
        }
        itemProperties.add(property);
    }

    @Override
    public String getScreenType(){ return UIMessageType.ITEM_DETAIL; }
}
