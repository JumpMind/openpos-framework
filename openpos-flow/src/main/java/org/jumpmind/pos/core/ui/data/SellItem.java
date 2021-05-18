package org.jumpmind.pos.core.ui.data;

import static org.apache.commons.lang3.StringUtils.isBlank;

import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;

import org.jumpmind.pos.core.ui.ActionItem;

public class SellItem extends DefaultItem {
    private static final long serialVersionUID = 1L;

    private String posItemId;
    private String altItemId;
    private String originalAmount;
    private String sellingPrice;
    private String quantity;
    private String discountAmount;
    private String salesAssociate;
    private List<ActionItem> menuItems = new ArrayList<>();
    private boolean isGiftReceipt = false;
    private boolean isQuantityChangeable;
    private boolean isOrderItem = false;
    private boolean showSellingPrice = false;
    private List<AdditionalLabel> additionalLabels = new ArrayList<>();
    private List<AdditionalLabel> returnItemLabels;
    private List<AdditionalLabel> orderItemLabels;
    private List<AdditionalLabel> collapsedAdditionalLabels = new ArrayList<>();
    private List<AdditionalLabel> promoLabels = new ArrayList<>();
    private String imageUrl;
    private String optionsLabel;
    private boolean isTender;
    private boolean svgImage;
    private String iconName;

    /**
     * Put properties in this map if they are optional. When not set, they don't
     * show up in the json which means less overhead.
     */
    private Map<String, Object> optionalProperties = new HashMap<String, Object>();

    @Override
    public String getSubtitle() {
        if (isBlank(subtitle)) {
            subtitle = "Item: %s%s %s@%s";
            String altItemId = isBlank(this.getAltItemId()) ? "" : "/" + this.getAltItemId();
            if (this.salesAssociate != null && this.salesAssociate != "") {
                subtitle = subtitle + " - Sales Associate: %s";
                subtitle = String.format(subtitle, this.getPosItemId(), altItemId, this.getQuantity(), this.getSellingPrice(),
                        this.getSalesAssociate());
            } else {
                subtitle = String.format(subtitle, this.getPosItemId(), altItemId, this.getQuantity(), this.getSellingPrice());
            }
        }
        return subtitle;
    }

    public String getIconName() {
        return iconName;
    }

    public void setIconName(String iconName) {
        this.iconName = iconName;
    }

    @Override
    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public void setPosItemId(String posItemId) {
        this.posItemId = posItemId;
    }

    public String getPosItemId() {
        return posItemId;
    }

    public void setAltItemId(String altItemId) {
        this.altItemId = altItemId;
    }

    public String getAltItemId() {
        return altItemId;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public void setSellingPrice(String sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    public String getSellingPrice() {
        return sellingPrice;
    }

    public void setIsGiftReceipt(boolean giftReceipt) {
        this.isGiftReceipt = giftReceipt;
    }

    public boolean getIsGiftReceipt() {
        return this.isGiftReceipt;
    }

    public void addMenuItem(ActionItem menuItem) {
        this.menuItems.add(menuItem);
    }

    public void setMenuItems(List<ActionItem> transactionMenuItems) {
        this.menuItems = transactionMenuItems;
    }

    public void addMenuItems(ActionItem... transactionMenuItems) {
        if (this.menuItems == null) {
            this.setMenuItems(Arrays.asList(transactionMenuItems));
        } else {
            this.menuItems.addAll(Arrays.asList(transactionMenuItems));
        }
    }

    public List<ActionItem> getMenuItems() {
        return menuItems;
    }

    public String getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(String discountAmount) {
        this.discountAmount = discountAmount;
    }

    public String getSalesAssociate() {
        return salesAssociate;
    }

    public void setSalesAssociate(String salesAssociate) {
        this.salesAssociate = salesAssociate;
    }

    public String getOriginalAmount() {
        return this.originalAmount;
    }

    public void setOriginalAmount(String originalAmount) {
        this.originalAmount = originalAmount;
    }

    @JsonAnyGetter
    public Map<String, Object> any() {
        return this.optionalProperties;
    }

    @JsonAnySetter
    public void put(String name, Object value) {
        this.optionalProperties.put(name, value);
    }

    public boolean contains(String name) {
        return this.optionalProperties.containsKey(name);
    }

    public Object get(String name) {
        return optionalProperties.get(name);
    }

    public boolean isQuantityChangeable() {
        return isQuantityChangeable;
    }

    public void setQuantityChangeable(boolean isQuantityChangeable) {
        this.isQuantityChangeable = isQuantityChangeable;
    }

    public boolean isShowSellingPrice() {
        return showSellingPrice;
    }

    public void setShowSellingPrice(boolean showSellingPrice) {
        this.showSellingPrice = showSellingPrice;
    }

    /**
     ** Determine if the given item has a quantity value that indicates it is
     * being returned.
     **
     ** @return If the quantity is negative, the item is being returned.
     **         Otherwise, if we have a quantity of zero or more or if the
     *         quantity is unknown, return false.
     */
    public boolean hasReturnQuantity() {
        boolean hasReturnQuantity = false;

        if (!StringUtils.isEmpty(quantity)) {
            try {
                int num = Integer.parseInt(quantity);
                hasReturnQuantity = (num < 0);
            } catch (Throwable err) {
                // Invalid number format. Can't tell.
            }
        }

        return hasReturnQuantity;
    }

    public boolean getIsOrderItem() {
        return isOrderItem;
    }

    public void setIsOrderItem(boolean isOrderItem) {
        this.isOrderItem = isOrderItem;
    }

    public List<AdditionalLabel> getAdditionalLabels() {
        return additionalLabels;
    }

    public void setAdditionalLabels(List<AdditionalLabel> additionalLabels) {
        this.additionalLabels = additionalLabels;
    }

    public void addAdditionalLabel(String label, String value) {
        this.additionalLabels.add(new AdditionalLabel(label, value));
    }

    public List<AdditionalLabel> getReturnItemLabels() {
        return returnItemLabels;
    }

    public void setReturnItemLabels(List<AdditionalLabel> returnItemLabels) {
        this.returnItemLabels = returnItemLabels;
    }

    public void addReturnItemLabel(String label, String value) {
        if(this.returnItemLabels == null) {
            this.returnItemLabels = new ArrayList<>();
        }
        this.returnItemLabels.add(new AdditionalLabel(label, value));
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getOptionsLabel() {
        return optionsLabel;
    }

    public void setOptionsLabel(String optionsLabel) {
        this.optionsLabel = optionsLabel;
    }

    public List<AdditionalLabel> getOrderItemLabels() {
        return orderItemLabels;
    }

    public void setOrderItemLabels(List<AdditionalLabel> orderItemLabels) {
        this.orderItemLabels = orderItemLabels;
    }

    public void addOrderItemLabel(String label, String value) {
        if(this.orderItemLabels == null) {
            this.orderItemLabels = new ArrayList<>();
        }
        this.orderItemLabels.add(new AdditionalLabel(label, value));
    }

    public boolean getIsTender() {
        return isTender;
    }

    public void setIsTender(boolean isTender) {
        this.isTender = isTender;
    }

    public boolean isSvgImage() {
        return svgImage;
    }

    public void setSvgImage(boolean svgImage) {
        this.svgImage = svgImage;
    }

    public boolean matches(SellItem otherItem) {
        if((this.posItemId != null && (otherItem.getPosItemId() == null || !this.posItemId.equals(otherItem.getPosItemId())))
                || (this.posItemId == null && otherItem.getPosItemId() != null)) {
            return false;
        }
        if((altItemId != null && (otherItem.getAltItemId() == null || !this.altItemId.equals(otherItem.getAltItemId())))
                || (this.altItemId == null && otherItem.getAltItemId() != null)) {
            return false;
        }
        if((originalAmount != null && (otherItem.getOriginalAmount() == null || !this.originalAmount.equals(otherItem.getOriginalAmount())))
                || (this.originalAmount == null && otherItem.getOriginalAmount() != null)) {
            return false;
        }
        if((sellingPrice != null && (otherItem.getSellingPrice() == null || !this.sellingPrice.equals(otherItem.getSellingPrice())))
                || (this.sellingPrice == null && otherItem.getSellingPrice() != null)) {
            return false;
        }
        if((quantity != null && (otherItem.getQuantity() == null || !this.quantity.equals(otherItem.getQuantity())))
                || (this.quantity == null && otherItem.getQuantity() != null)) {
            return false;
        }
        if((discountAmount != null && (otherItem.getDiscountAmount() == null || !this.discountAmount.equals(otherItem.getDiscountAmount())))
                || (this.discountAmount == null && otherItem.getDiscountAmount() != null)) {
            return false;
        }
        if((salesAssociate != null && (otherItem.getSalesAssociate() == null || !this.salesAssociate.equals(otherItem.getSalesAssociate())))
                || (this.salesAssociate == null && otherItem.getSalesAssociate() != null)) {
            return false;
        }
        if((menuItems != null && (otherItem.getMenuItems() == null || menuItems.size() != otherItem.getMenuItems().size()))
                || (menuItems == null && otherItem.getMenuItems() != null)) {
            return false;
        }
        if((isGiftReceipt && !otherItem.isGiftReceipt)||(!isGiftReceipt && !otherItem.isGiftReceipt)) {
            return false;
        }
        if((isQuantityChangeable && !otherItem.isQuantityChangeable)||(!isQuantityChangeable && !otherItem.isQuantityChangeable)) {
            return false;
        }
        if((isOrderItem && !otherItem.isOrderItem)||(!isOrderItem && !otherItem.isOrderItem)) {
            return false;
        }
        if((showSellingPrice && !otherItem.showSellingPrice)||(!showSellingPrice && !otherItem.showSellingPrice)) {
            return false;
        }
        if((additionalLabels != null && (otherItem.getAdditionalLabels() == null || additionalLabels.size() != otherItem.getAdditionalLabels().size()))
                || (additionalLabels == null && otherItem.getAdditionalLabels() != null)) {
            return false;
        }
        if((returnItemLabels != null && (otherItem.getReturnItemLabels() == null || returnItemLabels.size() != otherItem.getReturnItemLabels().size()))
                || (returnItemLabels == null && otherItem.getReturnItemLabels() != null)) {
            return false;
        }
        if((orderItemLabels != null && (otherItem.getOrderItemLabels() == null || orderItemLabels.size() != otherItem.getOrderItemLabels().size()))
                || (orderItemLabels == null && otherItem.getOrderItemLabels() != null)) {
            return false;
        }
        if((imageUrl != null && (otherItem.getImageUrl() == null || !this.imageUrl.equals(otherItem.getImageUrl())))
                || (this.imageUrl == null && otherItem.getImageUrl() != null)) {
            return false;
        }
        if((optionsLabel != null && (otherItem.getOptionsLabel() == null || !this.optionsLabel.equals(otherItem.getOptionsLabel())))
                || (this.optionsLabel == null && otherItem.getOptionsLabel() != null)) {
            return false;
        }
        return true;
    }

    public List<AdditionalLabel> getCollapsedAdditionalLabels() {
        return collapsedAdditionalLabels;
    }

    public void setCollapsedAdditionalLabels(List<AdditionalLabel> collapsedAdditionalLabels) {
        this.collapsedAdditionalLabels = collapsedAdditionalLabels;
    }

    public List<AdditionalLabel> getPromoLabels() {
        return promoLabels;
    }

    public void setPromoLabels(List<AdditionalLabel> promoLabels) {
        this.promoLabels = promoLabels;
    }

    public void addPromoLabels(AdditionalLabel... labels) {
        addPromoLabels(Arrays.asList(labels));
    }

    public void addPromoLabels(Collection<AdditionalLabel> labels) {
        if (getPromoLabels() == null) {
            setPromoLabels(new ArrayList<>(labels));
        } else {
            getPromoLabels().addAll(labels);
        }
    }
}
