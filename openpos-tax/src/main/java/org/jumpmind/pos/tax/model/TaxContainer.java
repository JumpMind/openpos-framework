package org.jumpmind.pos.tax.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public class TaxContainer {

    private SortedMap<GroupRule, List<TaxableItem>> itemsMap;

    public TaxContainer() {
        itemsMap = new TreeMap<GroupRule, List<TaxableItem>>(new GroupRuleComparator());
    }

    public void add(Collection<GroupRule> groupRules, TaxableItem item) {
        for (GroupRule groupRule : groupRules) {
            add(groupRule, item);
        }
    }

    public void add(GroupRule groupRule, TaxableItem item) {
        if (groupRule != null) {
            List<TaxableItem> items = itemsMap.get(groupRule);
            if (items == null) {
                items = new ArrayList<TaxableItem>();
                itemsMap.put(groupRule, items);
            }
            items.add(item);
        }
    }

    /**
     * Collapse tax groups into unique groups for calculations under the tax
     * authority. If multiple tax groups are performing the same calculation for
     * an authority, we eliminate the duplicates to prevent rounding errors.
     * 
     */
    public void collapseTaxGroups() {
        // TODO: implement collapseTaxGroups()
    }

    public Collection<GroupRule> getGroupRules() {
        return itemsMap.keySet();
    }

    public Collection<TaxableItem> getItems(GroupRule groupRule) {
        return itemsMap.get(groupRule);
    }

    /**
     * Determine if compounding will be used or not. All group rules must have
     * the compound sequence number set for compounding to be used.
     * 
     * @return boolean whether to use compounding on tax calculation
     */
    public boolean usesCompounding() {
        for (GroupRule groupRule : itemsMap.keySet()) {
            if (!groupRule.usesCompounding()) {
                return false;
            }
        }
        return true;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder(getClass().getSimpleName() + ": ");
        for (GroupRule groupRule : itemsMap.keySet()) {
            if (itemsMap.get(groupRule) != null) {
                builder.append(groupRule.toString() + " (" + itemsMap.get(groupRule).size() + " line items)\n");
            }
        }
        return builder.toString();
    }

    class GroupRuleComparator implements Comparator<GroupRule> {

        public int compare(GroupRule o1, GroupRule o2) {
            Integer i1 = o1.getCompoundSequenceNumber();
            Integer i2 = o2.getCompoundSequenceNumber();
            if (i1 != null && i2 != null && !i1.equals(i2)) {
                return i1.compareTo(i2);
            }
            return o1.compareTo(o2);
        }
    }
}
