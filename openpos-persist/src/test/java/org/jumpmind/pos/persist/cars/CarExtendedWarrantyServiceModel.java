package org.jumpmind.pos.persist.cars;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Delegate;
import org.joda.money.Money;
import org.jumpmind.pos.persist.AbstractModel;
import org.jumpmind.pos.persist.ColumnDef;
import org.jumpmind.pos.persist.CompositeDef;
import org.jumpmind.pos.persist.TableDef;

@Getter
@Setter
@TableDef(name="extended_warranty_service")
public class CarExtendedWarrantyServiceModel extends AbstractModel {

    @ColumnDef(primaryKey = true)
    String warrantyId;

    @ColumnDef
    int termInMonths;

    @Delegate
    @CompositeDef
    ServiceDefn serviceDefn;

    @ColumnDef
    String vin;

    @ColumnDef(crossReference = "isoCurrencyCode")
    Money crossRefField;

}
