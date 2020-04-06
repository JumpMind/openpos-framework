package org.jumpmind.pos.persist.cars;

import org.jumpmind.pos.persist.ColumnDef;
import org.joda.money.Money;
import org.jumpmind.pos.persist.TableDef;
import org.jumpmind.pos.persist.model.AbstractTaggedModel;
import org.jumpmind.pos.persist.model.ITaggedModel;

@TableDef(name="car",
        description = "A basic concept of an automobile fit to drive down the road.")
public class CarModel extends AbstractTaggedModel implements ITaggedModel {

    @SuppressWarnings("unused")
    private static final long serialVersionUID = 1L;
    
    @ColumnDef(primaryKey=true)
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

    /*
     This tests deprecating a field and mapping to a new field, while keeping the column name the same.  The subModel
     attribute used to be mapped to the sub_model column (by default), but I changed the mapping to the subModelCode
     property in order to test use of the 'name' attribute on a ColumnDef.
     */
    @Deprecated
    private String subModel;
    @ColumnDef(name="sub_model")
    private SubModelCode subModelCode;
    
    public String getModelYear() {
        return modelYear;
    }
    public void setModelYear(String modelYear) {
        this.modelYear = modelYear;
    }
    public String getMake() {
        return make;
    }
    public void setMake(String make) {
        this.make = make;
    }
    public String getModel() {
        return model;
    }
    public void setModel(String model) {
        this.model = model;
    }
    public String getVin() {
        return vin;
    }
    public void setVin(String vin) {
        this.vin = vin;
    }
    public Money getEstimatedValue() {
        return estimatedValue;
    }
    public void setEstimatedValue(Money estimatedValue) {
        this.estimatedValue = estimatedValue;
    }
    public String getIsoCurrencyCode() {
        return isoCurrencyCode;
    }
    public void setIsoCurrencyCode(String isoCurrencyCode) {
        this.isoCurrencyCode = isoCurrencyCode;
    }
    public CarTrimTypeCode getCarTrimTypeCode() {
        return carTrimTypeCode;
    }
    public void setCarTrimTypeCode(CarTrimTypeCode carTrimTypeCode) {
        this.carTrimTypeCode = carTrimTypeCode;
    }
    public byte[] getImage() {
        return image;
    }
    public void setImage(byte[] image) {
        this.image = image;
    }

    public boolean isAntique() {
        return antique;
    }

    public void setAntique(boolean antique) {
        this.antique = antique;
    }

    /*
        The following four methods (getSubModel, setSubModel, setSubModelCode, getSubModelCode) test use of the
        @ColumnDef 'name' attribute when building queries.  There was
        a case where we needed to change the Java type of an existing column from a String to an ITypeCode and so I
        handled it by adding a new attribute in the model class, but keeping the same column name.
     */
    @Deprecated
    public String getSubModel() { return subModel; }
    @Deprecated
    public void setSubModel(String subModel) { this.subModel = subModel; }

    public void setSubModelCode(SubModelCode subModelCode) {
        this.subModel = subModelCode != null ? subModelCode.toString() : null;
        this.subModelCode = subModelCode;
    }

    public SubModelCode getSubModelCode() {
        if (this.subModelCode == null && this.subModel != null) {
            return SubModelCode.of(subModel);
        }
        return subModelCode;
    }

}
