package org.jumpmind.pos.tax.model;

import java.util.ArrayList;
import java.util.List;

public class ItemTax {

    private int sequenceNumber;

    private List<TaxAmount> itemTaxAmounts = new ArrayList<TaxAmount>();

    public ItemTax(int sequenceNumber, TaxAmount itemTaxAmount) {
        this.sequenceNumber = sequenceNumber;
        itemTaxAmounts.add(itemTaxAmount);
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(int sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public void addItemTaxAmount(TaxAmount itemTaxAmount) {
        itemTaxAmounts.add(itemTaxAmount);
    }

    public List<TaxAmount> getItemTaxAmounts() {
        return itemTaxAmounts;
    }

    public void setItemTaxAmounts(List<TaxAmount> itemTaxAmounts) {
        this.itemTaxAmounts = itemTaxAmounts;
    }
}
