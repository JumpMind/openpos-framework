package org.jumpmind.pos.core.ui.data;


import static org.apache.commons.lang3.StringUtils.isBlank;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    /**
     * Put properties in this map if they are optional. When not set, they don't
     * show up in the json which means less overhead.
     */
    private Map<String, Object> optionalProperties = new HashMap<String, Object>();

    @Override
    public String getSubtitle() {
        if(isBlank(subtitle)) {
            subtitle = "Item: %s%s %s@%s";
            String altItemId = isBlank(this.getAltItemId()) ? "" : "/" + this.getAltItemId();
            if (this.salesAssociate != null && this.salesAssociate != "") {
                subtitle = subtitle + " - Sales Associate: %s";
                subtitle = String.format(subtitle, this.getPosItemId(), altItemId, this.getQuantity(), this.getSellingPrice(), this.getSalesAssociate());
            } else {
                subtitle = String.format(subtitle, this.getPosItemId(), altItemId, this.getQuantity(), this.getSellingPrice());
            }
        }
        return subtitle;
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
    public void setIsGiftReceipt( boolean giftReceipt ) {
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
    
    /**
    **  Determine if the given item has a quantity value that indicates
    **  it is being returned.
    **
    **  @return   If the quantity is negative, the item is being returned.
    **            Otherwise, if we have a quantity of zero or more or if
    **            the quantity is unknown, return false.
    */
    public boolean hasReturnQuantity()  {
    	boolean hasReturnQuantity = false;
    	
    	if (!StringUtils.isEmpty(quantity))  {
    		try  {
    			int num = Integer.parseInt(quantity);
    			hasReturnQuantity = (num < 0);
    		}  catch (Throwable err)  {
    			//  Invalid number format. Can't tell.
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
}
