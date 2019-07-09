package org.jumpmind.pos.core.ui.validator;



/**
 * A client side validator specification used to specify that a client
 * side field should not have a value under a specified minimum value.
 */
public class MinValueValidator implements IValidatorSpec {
    private static final long serialVersionUID = 1L;


    private String minimumValue;
    private String name;
    
    MinValueValidator() {
    }
    
    public MinValueValidator(String minimumValue) {
        this.minimumValue = minimumValue;
    }
    public MinValueValidator(String name, String minimumValue) {
        this(minimumValue);
        this.name = name;
    }

    public MinValueValidator(int minimumValue) {
        this.minimumValue = minimumValue + "";
    }
    public MinValueValidator(String name, int minimumValue) {
        this(minimumValue);
        this.name = name;
    }
    
    public MinValueValidator(float minimumValue) {
        this.minimumValue = minimumValue + "";
    }
    public MinValueValidator(String name, float minimumValue) {
        this(minimumValue);
        this.name = name;
    }
    
    public MinValueValidator(double minimumValue) {
        this.minimumValue = minimumValue + "";
        
    }
    public MinValueValidator(String name, double minimumValue) {
        this(minimumValue);
        this.name = name;
    }
    
    public MinValueValidator(Number minimumValue) {
        this.minimumValue = minimumValue != null ? minimumValue.toString() : null;
    }
    public MinValueValidator(String name, Number minimumValue) {
        this(minimumValue);
        this.name = name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    @Override
    public String getName() {
        return this.name != null? this.name : IValidatorSpec.super.getName();
    }
    
    public String getMinimumValue() {
        return minimumValue;
    }
    
    public void setMinimumValue(String minimumValue) {
        this.minimumValue = minimumValue;
    }
    
}
