package org.jumpmind.pos.core.screen;

import java.util.ArrayList;
import java.util.List;

import org.jumpmind.pos.core.model.Total;
import org.jumpmind.pos.core.model.Total.TotalType;
import org.jumpmind.pos.core.screenpart.BaconStripPart;
import org.jumpmind.pos.core.screenpart.ScanPart;
import org.jumpmind.pos.core.screenpart.StatusStripPart;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class SaleScreen extends Screen {
    private static final long serialVersionUID = 1L;
    public static final String ITEM_TOTAL_NAME = "itemTotal";
    
    private BaconStripPart baconStrip;
    private ScanPart scan;
    private StatusStripPart statusStrip;
    
    private List<SellItem> items = new ArrayList<>();
    private List<Integer> selectedItemIndexes = new ArrayList<>();
    private List<MenuItem> localMenuItems = new ArrayList<>();
    private List<MenuItem> transactionMenuItems = new ArrayList<>();
    
    private String balanceDue;
    private String discountTotal;
    private String grandTotal;
    private String subTotal;
    private String taxTotal;
    private String itemActionName = "Item";
    private Transaction transaction = new Transaction();
    private String customerName;
    private String noCustomerText;
    private List<Total> totals = new ArrayList<>();
    private MenuItem loyaltyButton;
    private MenuItem promoButton;
    private List<MenuItem> multiSelectedMenuItems;

    public SaleScreen() {
        this.setScreenType(ScreenType.Sale);
    }

    public List<SellItem> getItems() {
        return items;
    }

    public void setItems(List<SellItem> items) {
        this.items = items;
    }
    
    public String getBalanceDue() {
        return balanceDue;
    }

    public void setBalanceDue(String balanceDue) {
        this.balanceDue = balanceDue;
    }

    public String getDiscountTotal() {
        return discountTotal;
    }

    public void setDiscountTotal(String discountTotal) {
        this.discountTotal = discountTotal;
    }

    public String getGrandTotal() {
        return grandTotal;
    }

    public void setGrandTotal(String grandTotal) {
        this.grandTotal = grandTotal;
    }

    public String getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(String subTotal) {
        this.subTotal = subTotal;
    }

    public String getTaxTotal() {
        return taxTotal;
    }

    public void setTaxTotal(String taxTotal) {
        this.taxTotal = taxTotal;
    }

    
    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }
    
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
    
    public String getCustomerName() {
        return customerName;
    }

    public String getItemActionName() {
        return itemActionName;
    }

    public void setItemActionName(String itemActionName) {
        this.itemActionName = itemActionName;
    }    

	public List<Total> getTotals() {
		return totals;
	}

	public void setTotals(List<Total> totals) {
		this.totals = totals;
	}
	
	public void addTotal(String name, String amount) {
		this.totals.add(new Total(name, amount ));
	}

    public void addTotal(Total total) {
        this.totals.add(total);
    }
    
	public void setItemTotal(String total) {
	    Total itemTotal = this.getItemTotal();
	    if (itemTotal == null) {
	        this.totals.add(new Total(ITEM_TOTAL_NAME, total, TotalType.Quantity));
	    } else {
	        itemTotal.setAmount(total);
	    }
	}

	@JsonIgnore
    public Total getItemTotal() {
        return this.totals.stream().filter(
            t -> t.getType() == TotalType.Quantity && ITEM_TOTAL_NAME.equalsIgnoreCase(t.getName())).findFirst().orElse(null);
    }
    
	public String getNoCustomerText() {
		return noCustomerText;
	}

	public void setNoCustomerText(String noCustomerText) {
		this.noCustomerText = noCustomerText;
	}

	public MenuItem getLoyaltyButton() {
		return loyaltyButton;
	}

	public void setLoyaltyButton(MenuItem loyaltyButton) {
		this.loyaltyButton = loyaltyButton;
	}

    public MenuItem getPromoButton() {
        return promoButton;
    }

    public void setPromoButton(MenuItem promoButton) {
        this.promoButton = promoButton;
    }

    public List<MenuItem> getMultiSelectedMenuItems() {
        return multiSelectedMenuItems;
    }

    public void setMultiSelectedMenuItems(List<MenuItem> multiSelectedMenuItems) {
        this.multiSelectedMenuItems = multiSelectedMenuItems;
    }
    
    public MenuItem getLocalMenuItemByAction(String action) {
        return this. localMenuItems.stream().filter( mi -> action.equalsIgnoreCase(mi.getAction())).findFirst().orElse(null);
    }
    
    public MenuItem getLocalMenuItemByTitle(String title) {
        return this.localMenuItems.stream().filter( mi -> title.equalsIgnoreCase(mi.getTitle())).findFirst().orElse(null);
    }

    public void addLocalMenuItem(MenuItem menuItem) {
        this.localMenuItems.add(menuItem);
    }
    
    public void setLocalMenuItems(List<MenuItem> localMenuItems) {
        this.localMenuItems = localMenuItems;
    }
    
    public List<MenuItem> getLocalMenuItems() {
        return localMenuItems;
    }
    
    public void addTransactionMenuItem(MenuItem menuItem) {
        this.transactionMenuItems.add(menuItem);
    }
    
    public void setTransactionMenuItems(List<MenuItem> transactionMenuItems) {
        this.transactionMenuItems = transactionMenuItems;
    }
    
    public List<MenuItem> getTransactionMenuItems() {
        return transactionMenuItems;
    }

    public ScanPart getScan() {
        return scan;
    }

    public void setScan(ScanPart scan) {
        this.scan = scan;
    }

    public BaconStripPart getBaconStrip() {
        return baconStrip;
    }

    public void setBaconStrip(BaconStripPart baconStrip) {
        this.baconStrip = baconStrip;
    }

    public List<Integer> getSelectedItemIndexes() {
        return selectedItemIndexes;
    }

    public void setSelectedItemIndexes(List<Integer> selectedItemIndexes) {
        this.selectedItemIndexes = selectedItemIndexes;
    }

	public StatusStripPart getStatusStrip() {
		return statusStrip;
	}

	public void setStatusStrip(StatusStripPart statusStrip) {
		this.statusStrip = statusStrip;
	}
    
}
