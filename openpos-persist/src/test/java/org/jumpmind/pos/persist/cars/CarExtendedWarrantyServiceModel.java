package org.jumpmind.pos.persist.cars;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Delegate;
import org.joda.money.Money;
import org.jumpmind.pos.persist.*;

@Getter
@Setter
@TableDef(name="extended_warranty_service",
        primaryKey = {"warrantyId", "effectiveStartDate"})
@IndexDefs({
        @IndexDef(name = "idx_currency_code_term", columns = {"isoCurrencyCode", "effectiveStartDate"}),
        @IndexDef(name = "idx_vin", column = "vin")
})
public class CarExtendedWarrantyServiceModel extends AbstractModel {

    @Delegate
    @CompositeDef
    @JsonIgnore
    ServiceDefn serviceDefn;

    @ColumnDef
    String warrantyId;

    @ColumnDef
    int termInMonths;

    @ColumnDef
    String vin;

    @ColumnDef(crossReference = "isoCurrencyCode")
    Money crossRefField;

}
