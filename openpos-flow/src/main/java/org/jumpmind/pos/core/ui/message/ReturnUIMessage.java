package org.jumpmind.pos.core.ui.message;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.jumpmind.pos.core.model.DisplayProperty;
import org.jumpmind.pos.core.model.Total;
import org.jumpmind.pos.core.ui.ActionItem;
import org.jumpmind.pos.core.ui.ActionItemGroup;
import org.jumpmind.pos.core.ui.data.SellItem;
import org.jumpmind.pos.core.ui.data.TransactionReceipt;
import org.jumpmind.pos.core.ui.AssignKeyBindings;
import org.jumpmind.pos.core.ui.UIMessage;

import java.util.ArrayList;
import java.util.List;

@AssignKeyBindings
public class ReturnUIMessage extends UIMessage {
    private static final long serialVersionUID = 1L;
    public static final String ITEM_TOTAL_NAME = "itemTotal";

    private List<SellItem> items = new ArrayList<>();
    private List<SellItem> selectedItems = new ArrayList<>();
    private int[] selectedItemIndexes = new int[0];
    private List<TransactionReceipt> receipts = new ArrayList<>();
    private ActionItem removeReceiptAction;
    private String transactionMenuPrompt;
    private ActionItemGroup transactionMenu = new ActionItemGroup();
    private List<ActionItem> multiSelectedMenuItems = new ArrayList<ActionItem>();
    private String itemActionName = "Item";
    private List<Total> totals = new ArrayList<>();
    private DisplayProperty grandTotal;
    private String customerName;
    private String noCustomerText;
    private boolean locationEnabled;
    private String locationOverridePrompt;
    private ActionItem checkoutButton;
    private String itemCount;

    public ReturnUIMessage() {
        this.setScreenType(UIMessageType.RETURN);
    }

    public List<SellItem> getItems() {
        return items;
    }

    public void setItems(List<SellItem> items) {
        this.items = items;
    }

    public List<SellItem> getSelectedItems() {
        return selectedItems;
    }

    public void setSelectedItems(List<SellItem> selectedItems) {
        this.selectedItems = selectedItems;
    }

    public int[] getSelectedItemIndexes() {
        return selectedItemIndexes;
    }

    public void setSelectedItemIndexes(int[] selectedItemIndexes) {
        this.selectedItemIndexes = selectedItemIndexes;
    }

    public List<TransactionReceipt> getReceipts() {
        return receipts;
    }

    public void setReceipts(List<TransactionReceipt> receipts) {
        this.receipts = receipts;
    }

    public ActionItem getRemoveReceiptAction() {
        return removeReceiptAction;
    }

    public void setRemoveReceiptAction(ActionItem removeReceiptAction) {
        this.removeReceiptAction = removeReceiptAction;
    }

    public void addReceipt(TransactionReceipt receipt) {
        this.receipts.add(receipt);
    }

    public String getTransactionMenuPrompt() {
        return transactionMenuPrompt;
    }

    public void setTransactionMenuPrompt(String transactionMenuPrompt) {
        this.transactionMenuPrompt = transactionMenuPrompt;
    }

    public ActionItemGroup getTransactionMenu() {
        return transactionMenu;
    }

    public void setTransactionMenu(ActionItemGroup transactionMenu) {
        this.transactionMenu = transactionMenu;
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
        this.totals.add(new Total(name, amount));
    }

    public void addTotal(Total total) {
        this.totals.add(total);
    }

    public DisplayProperty getGrandTotal() {
        return grandTotal;
    }

    public void setGrandTotal(DisplayProperty grandTotal) {
        this.grandTotal = grandTotal;
    }

    public void setItemTotal(String total) {
        Total itemTotal = this.getItemTotal();
        if (itemTotal == null) {
            this.totals.add(new Total(ITEM_TOTAL_NAME, total, Total.TotalType.Quantity));
        } else {
            itemTotal.setAmount(total);
        }
    }

    @JsonIgnore
    public Total getItemTotal() {
        return this.totals.stream().filter(t -> t.getType() == Total.TotalType.Quantity && ITEM_TOTAL_NAME.equalsIgnoreCase(t.getName()))
                .findFirst().orElse(null);
    }

    public List<ActionItem> getMultiSelectedMenuItems() {
        return multiSelectedMenuItems;
    }

    public void setMultiSelectedMenuItems(List<ActionItem> multiSelectedMenuItems) {
        this.multiSelectedMenuItems = multiSelectedMenuItems;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getNoCustomerText() {
        return noCustomerText;
    }

    public void setNoCustomerText(String noCustomerText) {
        this.noCustomerText = noCustomerText;
    }

    public boolean isLocationEnabled() {
        return locationEnabled;
    }

    public void setLocationEnabled(boolean locationEnabled) {
        this.locationEnabled = locationEnabled;
    }

    public String getLocationOverridePrompt() {
        return locationOverridePrompt;
    }

    public void setLocationOverridePrompt(String locationOverridePrompt) {
        this.locationOverridePrompt = locationOverridePrompt;
    }

    public ActionItem getCheckoutButton() {
        return checkoutButton;
    }

    public void setCheckoutButton(ActionItem checkoutButton) {
        this.checkoutButton = checkoutButton;
    }

    public String getItemCount() {
        return itemCount;
    }

    public void setItemCount(String itemCount) {
        this.itemCount = itemCount;
    }
}
