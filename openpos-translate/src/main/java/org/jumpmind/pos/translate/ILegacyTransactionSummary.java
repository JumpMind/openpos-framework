package org.jumpmind.pos.translate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Predicate;

public interface ILegacyTransactionSummary {
    
    public LocalDate getBusinessDate();
    public String getStoreId();
    public String getStoreLocation();
    public List<ILegacyItemSummary> getItemSummaries();
    public ILegacyCurrency getTransactionGrandTotal();

    public String getTransactionId();
    public String getDescription();
    public String getRegisterId();
    public BigDecimal getUnitsSold(Predicate<ILegacyItemSummary> evaluateItem);

}
