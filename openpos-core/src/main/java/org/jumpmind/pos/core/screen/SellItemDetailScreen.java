package org.jumpmind.pos.core.screen;

import java.util.ArrayList;
import java.util.List;

public class SellItemDetailScreen extends Screen {

    private static final long serialVersionUID = 1L;
    
    private SellItem item = new SellItem();
    private List<MenuItem> itemActions = new ArrayList<>();
    private Transaction transaction = new Transaction();
    private List<PromoItem> promos = new ArrayList<>();

    public SellItemDetailScreen() {
        setType(ScreenType.SellItemDetail);
    }

    public List<MenuItem> getItemActions() {
        return itemActions;
    }

    public void setItemActions(List<MenuItem> itemActions) {
        this.itemActions = itemActions;
    }

    public SellItem getItem() {
        return item;
    }

    public void setItem(SellItem item) {
        this.item = item;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

	public List<PromoItem> getPromos() {
		return promos;
	}

	public void setPromos(List<PromoItem> promos) {
		this.promos = promos;
	}

}
