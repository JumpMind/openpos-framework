package org.jumpmind.pos.tax.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaxCalculationResponse {

    private Map<Integer, ItemTax> itemTaxes = new HashMap<Integer, ItemTax>();

    private List<TaxAmount> taxAmounts = new ArrayList<TaxAmount>();

    public void addItemTaxAmount(int sequenceNumber, TaxAmount itemTaxAmount) {

        if (itemTaxes.containsKey(sequenceNumber)) {
            itemTaxes.get(sequenceNumber).addItemTaxAmount(itemTaxAmount);
        } else {
            ItemTax itemTax = new ItemTax(sequenceNumber, itemTaxAmount);
            itemTaxes.put(sequenceNumber, itemTax);
        }
    }

    public Collection<ItemTax> getItemTaxes() {
        return itemTaxes.values();
    }

    public ItemTax getItemTax(int sequenceNumber) {
        return itemTaxes.get(sequenceNumber);
    }

    public void addTaxAmount(TaxAmount amount) {
        taxAmounts.add(amount);
    }

    public List<TaxAmount> getTaxAmounts() {
        return taxAmounts;
    }

    public TaxAmount getTaxAmount(String authorityId, String groupId) {
        for (TaxAmount taxAmount : taxAmounts) {
            if (taxAmount.getAuthorityId().equals(authorityId) && taxAmount.getGroupId().equals(groupId)) {
                return taxAmount;
            }
        }
        return null;
    }
}
