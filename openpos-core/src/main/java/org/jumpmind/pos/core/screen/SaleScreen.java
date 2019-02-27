package org.jumpmind.pos.core.screen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jumpmind.pos.core.screenpart.BaconStripPart;
import org.jumpmind.pos.core.screenpart.ScanPart;
import org.jumpmind.pos.core.screenpart.StatusStripPart;

public class SaleScreen extends Screen {
    private static final long serialVersionUID = 1L;
    public static final String ITEM_TOTAL_NAME = "itemTotal";

    private BaconStripPart baconStrip = new BaconStripPart();
    private ScanPart scan = new ScanPart();
    private StatusStripPart statusStrip = new StatusStripPart();

    private List<SellItem> items = new ArrayList<>();
    private int[] selectedItemIndexes = new int[0];
    private List<ActionItem> sausageLinks = new ArrayList<>();

    private String transactionMenuPrompt;
    private List<ActionItem> transactionMenuItems = new ArrayList<>();

    private String itemCount;
    private String grandTotal;
    private boolean transactionActive = false;
    private String customerName;
    private String noCustomerText;
    private Map<String, String> totals;
    private ActionItem loyaltyButton;
    private ActionItem promoButton;
    private ActionItem checkoutButton;
    private List<ActionItem> multiSelectedMenuItems;

    public SaleScreen() {
        this.setScreenType(ScreenType.Sale);
        this.setId("sale");
        this.setTemplate(null);
    }

    public List<SellItem> getItems() {
        return items;
    }

    public void setItems(List<SellItem> items) {
        this.items = items;
    }

    public String getGrandTotal() {
        return grandTotal;
    }

    public void setGrandTotal(String grandTotal) {
        this.grandTotal = grandTotal;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerName() {
        return customerName;
    }

    public Map<String, String> getTotals() {
        return totals;
    }

    public void setTotals(Map<String, String> totals) {
        this.totals = totals;
    }

    public void setTotal(String name, String amount) {
    	if( totals == null ) {
    		totals = new HashMap<String, String>();
    	}
        totals.put(name, amount);
    }

    public String getNoCustomerText() {
        return noCustomerText;
    }

    public void setNoCustomerText(String noCustomerText) {
        this.noCustomerText = noCustomerText;
    }

    public ActionItem getLoyaltyButton() {
        return loyaltyButton;
    }

    public void setLoyaltyButton(ActionItem loyaltyButton) {
        this.loyaltyButton = loyaltyButton;
    }

    public ActionItem getPromoButton() {
        return promoButton;
    }

    public void setPromoButton(ActionItem promoButton) {
        this.promoButton = promoButton;
    }

    public List<ActionItem> getMultiSelectedMenuItems() {
        return multiSelectedMenuItems;
    }

    public void setMultiSelectedMenuItems(List<ActionItem> multiSelectedMenuItems) {
        this.multiSelectedMenuItems = multiSelectedMenuItems;
    }

    public ActionItem getLocalSausageLinkByAction(String action) {
        return this.sausageLinks.stream().filter(mi -> action.equalsIgnoreCase(mi.getAction())).findFirst().orElse(null);
    }

    public ActionItem getSausageLinkByTitle(String title) {
        return this.sausageLinks.stream().filter(mi -> title.equalsIgnoreCase(mi.getTitle())).findFirst().orElse(null);
    }

    public void addSausageLink(ActionItem menuItem) {
        this.sausageLinks.add(menuItem);
    }

    public void setSausageLinks(List<ActionItem> localMenuItems) {
        this.sausageLinks = localMenuItems;
    }

    public List<ActionItem> getSausageLinks() {
        return sausageLinks;
    }

    public String getTransactionMenuPrompt() {
        return transactionMenuPrompt;
    }

    public void setTransactionMenuPrompt(String transactionMenuPrompt) {
        this.transactionMenuPrompt = transactionMenuPrompt;
    }

    public void addTransactionMenuItem(ActionItem menuItem) {
        this.transactionMenuItems.add(menuItem);
    }

    public void setTransactionMenuItems(List<ActionItem> transactionMenuItems) {
        this.transactionMenuItems = transactionMenuItems;
    }

    public List<ActionItem> getTransactionMenuItems() {
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

    public int[] getSelectedItemIndexes() {
        return selectedItemIndexes;
    }

    public void setSelectedItemIndexes(int[] selectedItemIndexes) {
        this.selectedItemIndexes = selectedItemIndexes;
    }

    public StatusStripPart getStatusStrip() {
        return statusStrip;
    }

    public void setStatusStrip(StatusStripPart statusStrip) {
        this.statusStrip = statusStrip;
    }
        
    public void setTransactionActive(boolean isTransactionActive) {
        this.transactionActive = isTransactionActive;
    }
    
    public boolean isTransactionActive() {
        return transactionActive;
    }

	public String getItemCount() {
		return itemCount;
	}

	public void setItemCount(String itemCount) {
		this.itemCount = itemCount;
	}

	public ActionItem getCheckoutButton() {
		return checkoutButton;
	}

	public void setCheckoutButton(ActionItem checkoutButton) {
		this.checkoutButton = checkoutButton;
	}

}
