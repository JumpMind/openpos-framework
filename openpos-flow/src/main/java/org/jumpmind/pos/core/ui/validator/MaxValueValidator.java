package org.jumpmind.pos.core.ui.validator;


/**
 * A client side validator specification used to specify a maximum
 * value that should not be exceeded for a field on the client side.
 */
public class MaxValueValidator implements IValidatorSpec {
    private static final long serialVersionUID = 1L;


    private String maximumValue;
    
    public MaxValueValidator(String maximumValue) {
        this.maximumValue = maximumValue;
    }

    public MaxValueValidator(int maximumValue) {
        this.maximumValue = maximumValue + "";
    }
    
    public MaxValueValidator(float maximumValue) {
        this.maximumValue = maximumValue + "";
    }
    
    public MaxValueValidator(double maximumValue) {
        this.maximumValue = maximumValue + "";
        
    }
    
    public MaxValueValidator(Number maximumValue) {
        this.maximumValue = maximumValue != null ? maximumValue.toString() : null;
    }
    
    public String getMaximumValue() {
        return maximumValue;
    }
    
    public void setMaximumValue(String maximumValue) {
        this.maximumValue = maximumValue;
    }
    
}
