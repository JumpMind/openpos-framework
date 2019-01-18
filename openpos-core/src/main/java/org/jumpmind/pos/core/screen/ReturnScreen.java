package org.jumpmind.pos.core.screen;

import java.util.ArrayList;
import java.util.List;

import org.jumpmind.pos.core.model.Total;
import org.jumpmind.pos.core.model.Total.TotalType;
import org.jumpmind.pos.core.template.SellTemplate;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class ReturnScreen extends Screen {

    private static final long serialVersionUID = 1L;
    public static final String ITEM_TOTAL_NAME = "itemTotal";

    private List<SellItem> items = new ArrayList<>();
    private List<SellItem> selectedItems = new ArrayList<>();
    private List<TransactionReceipt> receipts = new ArrayList<>();

    private String itemActionName = "Item";
    private Transaction transaction = null;
    private List<Total> totals = new ArrayList<>();
    private List<MenuItem> multiSelectedMenuItems;

    public ReturnScreen() {
        this.setScreenType(ScreenType.Return);
        this.setTemplate(new SellTemplate().enableScan(false));
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

    public List<TransactionReceipt> getReceipts() {
        return receipts;
    }

    public void setReceipts(List<TransactionReceipt> receipts) {
        this.receipts = receipts;
    }

    public void addReceipt(TransactionReceipt receipt) {
        this.receipts.add(receipt);
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
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
        return this.totals.stream().filter(t -> t.getType() == TotalType.Quantity && ITEM_TOTAL_NAME.equalsIgnoreCase(t.getName()))
                .findFirst().orElse(null);
    }

    public List<MenuItem> getMultiSelectedMenuItems() {
        return multiSelectedMenuItems;
    }

    public void setMultiSelectedMenuItems(List<MenuItem> multiSelectedMenuItems) {
        this.multiSelectedMenuItems = multiSelectedMenuItems;
    }
}
