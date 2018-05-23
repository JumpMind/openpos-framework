package org.jumpmind.pos.translate;

import java.math.BigDecimal;

public interface ILegacyItemSummary {
    String getItemId();
    BigDecimal getUnitsSold();
}
