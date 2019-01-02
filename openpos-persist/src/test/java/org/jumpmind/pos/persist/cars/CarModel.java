package org.jumpmind.pos.persist.cars;

import org.jumpmind.pos.persist.ColumnDef;
import org.jumpmind.pos.persist.AbstractModel;
import org.jumpmind.pos.persist.TableDef;

@TableDef(name="car",
        description = "A basic concept of an automobile fit to drive down the road.")
public class CarModel extends AbstractModel {

    private static final long serialVersionUID = 1L;
    
    @ColumnDef(primaryKey=true)
    private String vin;
    @ColumnDef    
    private String modelYear;
    @ColumnDef
    private String make;
    @ColumnDef
    private String model;
    
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
    
    
}
