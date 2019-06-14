package org.jumpmind.pos.core.screen;

import org.jumpmind.pos.core.model.DisplayProperty;
import org.jumpmind.pos.core.model.Total;
import org.jumpmind.pos.core.screenpart.BaconStripPart;
import org.jumpmind.pos.core.screenpart.ScanPart;
import org.jumpmind.pos.core.screenpart.StatusStripPart;
import org.jumpmind.pos.core.ui.UIMessage;

import java.util.ArrayList;
import java.util.List;

@Deprecated
public class SaleScreen extends Screen {
    private static final long serialVersionUID = 1L;

    private BaconStripPart baconStrip = new BaconStripPart();
    private ScanPart scan = new ScanPart();
    private StatusStripPart statusStrip = new StatusStripPart();

    private List<ActionItem> sausageLinks = new ArrayList<>();

    private String transactionMenuPrompt;
    private ActionItemGroup transactionMenu = new ActionItemGroup();
    private List<ActionItem> multiSelectedMenuItems;
    private List<SellItem> items = new ArrayList<>();
    private int[] selectedItemIndexes = new int[0];
    private List<Total> totals;

    private String itemCount;
    private DisplayProperty grandTotal;
    private boolean transactionActive = false;
    private String customerName;
    private String noCustomerText;
    private ActionItem loyaltyButton;
    private ActionItem promoButton;
    private ActionItem checkoutButton;

    public SaleScreen() {
        this.setScreenType(ScreenType.Sale);
        this.setId("sale");
    }

    public List<SellItem> getItems() {
        return items;
    }

    public void setItems(List<SellItem> items) {
        this.items = items;
    }

    public DisplayProperty getGrandTotal() {
        return grandTotal;
    }

    public void setGrandTotal(DisplayProperty grandTotal) {
        this.grandTotal = grandTotal;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerName() {
        return customerName;
    }

    public List<Total> getTotals() {
        return totals;
    }

    public void setTotals(List<Total> totals) {
        this.totals = totals;
    }

    public void addTotal(String name, String amount) {
    	if( totals == null ) {
    		totals = new ArrayList<>();
    	}
        totals.add(new Total(name, amount));
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
        this.transactionMenu.getActionItems().add(menuItem);
    }

    public void setTransactionMenu(ActionItemGroup transactionMenu) {
        this.transactionMenu = transactionMenu;
    }

    public ActionItemGroup getTransactionMenu() {
        return transactionMenu;
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
