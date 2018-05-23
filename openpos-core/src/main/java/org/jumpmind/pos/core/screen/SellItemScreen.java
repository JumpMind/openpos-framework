package org.jumpmind.pos.core.screen;

import java.util.ArrayList;
import java.util.List;

import org.jumpmind.pos.core.model.Total;
import org.jumpmind.pos.core.template.SellTemplate;

public class SellItemScreen extends PromptScreen {

    private static final long serialVersionUID = 1L;

    private List<SellItem> items = new ArrayList<>();
    
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

    public SellItemScreen() {
        this.setType(ScreenType.Transaction);
        this.setTemplate(new SellTemplate().enableScan(false));
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
}
