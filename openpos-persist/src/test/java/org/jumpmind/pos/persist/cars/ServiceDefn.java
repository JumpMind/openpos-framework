package org.jumpmind.pos.persist.cars;

import lombok.Getter;
import lombok.Setter;
import org.joda.money.Money;
import org.jumpmind.pos.persist.ColumnDef;

import java.math.BigDecimal;

@Getter
@Setter
public class ServiceDefn {

    @ColumnDef(primaryKey = true)
    String effectiveStartDate;

    @ColumnDef
    String effectiveEndDate;

    @ColumnDef
    BigDecimal retailPrice;

    @ColumnDef(crossReference = "isoCurrencyCode")
    Money cost;

    @ColumnDef
    String isoCurrencyCode;

}
