package org.jumpmind.pos.persist.cars;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.money.Money;
import org.jumpmind.pos.persist.*;
import org.jumpmind.pos.persist.model.AbstractAugmentedModel;
import org.jumpmind.pos.persist.model.IAugmentedModel;


@Data
@NoArgsConstructor
@TableDef(name="augmented_car",
        description = "A basic concept of an automobile fit to drive down the road, with options augments.",
        primaryKey = "vin")
@IndexDefs({
        @IndexDef(name = "idx_aug_car_year", column = "modelYear"),
        @IndexDef(name = "idx_aug_car_make_model", columns = {"make", "model"})
})
@Augmented(name = "options", indexPrefix = "aug")
public class AugmentedCarModel extends AbstractAugmentedModel implements IAugmentedModel {

    @ColumnDef
    private String vin;
    @ColumnDef
    private String modelYear;
    @ColumnDef
    private String make;
    @ColumnDef
    private String model;
    @ColumnDef(crossReference="isoCurrencyCode")
    private Money estimatedValue;
    @ColumnDef
    private String isoCurrencyCode;
    @ColumnDef
    private CarTrimTypeCode carTrimTypeCode;
    @ColumnDef
    private byte[] image;
    @ColumnDef
    private boolean antique;
}
