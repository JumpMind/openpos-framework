package org.jumpmind.pos.persist.cars;

import org.jumpmind.pos.persist.Column;
import org.jumpmind.pos.persist.Entity;
import org.jumpmind.pos.persist.Table;

@Table(name="car",
        description = "A basic concept of an automobile fit to drive down the road.")
public class CarEntity extends Entity {

    @Column(primaryKey=true)
    private String vin;
    @Column    
    private String modelYear;
    @Column
    private String make;
    @Column
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
