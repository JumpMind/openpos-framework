package org.jumpmind.pos.core.ui.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jumpmind.pos.core.model.DisplayProperty;
import org.jumpmind.pos.core.ui.ActionItem;
import org.jumpmind.pos.core.ui.AssignKeyBindings;
import org.jumpmind.pos.core.ui.UIMessage;
import org.jumpmind.pos.core.ui.data.BuddyStore;
import org.jumpmind.pos.core.ui.data.Promotion;

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
    private String price;
    private DisplayProperty itemValueDisplay;
    private List<String> imageUrls;
    private String alternateImageUrl;
    private List<DisplayProperty> itemProperties;
    private String itemPromotionsTitle;
    private String itemNoPromotionsTitle;
    private List<Promotion> promotions;
    private String promotionStackingDisclaimer;
    
    private List<ProductOptionComponent> productOptionsComponents;
    private String itemOptionInstructions;
    
    private String inventoryMessage;
    private boolean buddyStoreOffline;
    private String buddyStoreTitle;
    private String buddyStoreIcon;
    private String buddyStoreOfflineTitle;
    private List<BuddyStore> buddyStores;
    private List<ActionItem> actions;
    private List<String> detailSections;


    public void addItemProperty(DisplayProperty property) {
        if (itemProperties == null) {
            itemProperties = new ArrayList<>();
        }
        itemProperties.add(property);
    }
    
    public void addProductOptionComponent(String componentType, String componentName){
        if(productOptionsComponents == null){
            productOptionsComponents = new ArrayList<>();
        }
        productOptionsComponents.add(ProductOptionComponent.builder().name(componentName).type(componentType).build());
    }

    @Override
    public String getScreenType() {
        return UIMessageType.ITEM_DETAIL;
    }
}
