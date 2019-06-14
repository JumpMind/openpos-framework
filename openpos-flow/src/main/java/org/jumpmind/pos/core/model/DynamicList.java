package org.jumpmind.pos.core.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class DynamicList implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private List<String> formErrors = new ArrayList<String>();
    
    private boolean requiresAtLeastOneValue = false;
    
    private String name;
    
    private FieldInputType valueType          = null;
    private String         valueListHeader    = null;
    private List<String>   valueList          = new ArrayList<String>();
    private String         valueListIconName  = null;
    private FormField      addValueField      = null;
    private FormField      summaryField       = null;
    private boolean        isRemovingAllowed  = false;
    private int            removedIndex       = -1;
    private double         minValue           = 0.0;
    private double         maxValue           = Double.NaN;
    private String         valueChangedAction = null;
    
    private static final String SUMMARY_FIELD_NAME   = "summary";
    private static final String ADD_VALUE_FIELD_NAME = "additionalValue";
    
    public static final String ADD_VALUE_ACTION    = "AddValue";
    public static final String DELETE_VALUE_ACTION = "DeleteValue";
    
    public DynamicList()  {
    	// nothing to do here
    }
    
    public DynamicList(String name, boolean isRemovingAllowed)  {
    	this.name = name;
    	this.isRemovingAllowed = false;
    	this.requiresAtLeastOneValue = false;
    	this.addValueField = null;
    	this.summaryField = null;
    }
    
    public DynamicList(String name, String valueListHeader, String valueListIconName, FieldInputType valueType, String addValueFieldLabel, String addValueFieldValue, String summaryFieldLabel, String summaryFieldValue, boolean isRemovingAllowed, boolean requiresAtLeastOneValue)  {
    	this.name = name;
    	this.valueListHeader = valueListHeader;
    	this.valueListIconName = valueListIconName;
    	this.valueType = valueType;
    	this.isRemovingAllowed = isRemovingAllowed;
    	this.requiresAtLeastOneValue = requiresAtLeastOneValue;
    	
    	initializeAddValueField(valueType, addValueFieldLabel, addValueFieldValue);
    	initializeSummaryField(valueType, summaryFieldLabel, summaryFieldValue);
    }
    
    public FieldInputType getValueType()  {
    	return valueType;
    }

    public List<String> getValueList() {
        return valueList;
    }

    public void setValueList(List<String> list) {
        this.valueList = list;
    }

    /*
    @JsonIgnore
    public void setValueList(String[] list) {
        this.valueList = new ArrayList<String>();
        for (String value : list)  {
        	this.valueList.add(value);
        }
    }
    */
    
    public String getValueListHeader()  {
    	return this.valueListHeader;
    }
    
    public boolean hasValueListHeader()  {
    	return StringUtils.isNotEmpty(this.valueListHeader);
    }
    
    public void setValueListHeader(String header)  {
    	this.valueListHeader = header;
    }
    
    public String getValueListIconName()  {
    	return this.valueListIconName;
    }
    
    public void setValueListIconName(String name)  {
    	this.valueListIconName = name;
    }
    
    public double getMinValue()  {
    	return this.minValue;
    }
    
    public void setMinValue(double value)  {
    	this.minValue = value;
    }
    
    public double getMaxValue()  {
    	return this.maxValue;
    }
    
    public void setMaxValue(double value)  {
    	this.maxValue = value;
    }
    
    public void addValue(String value)  {
    	if (hasAddValueField())  {
    		this.valueList.add(value);
    	}
    }
    
    public void addValue(int index, String value)  {
    	if (hasAddValueField())  {
    		this.valueList.add(index, value);
    	}
    }
    
    public void removeValue(int index)  {
    	if (isRemovingAllowed)  {
    		this.valueList.remove(index);
    		this.removedIndex = index;
    	}
    }
    
    public int getRemovedIndex()  {
    	return (isRemovingAllowed ? this.removedIndex : -1);
    }
    
    private void initializeAddValueField(FieldInputType valueType, String addValueFieldLabel, String addValueFieldValue)  {
    	if (valueType != null)  {
    		switch (valueType)  {
    		case AlphanumericText:
    		case NumericText:
    		case Decimal:
    		case Money:
    			addValueField = new FormField(ADD_VALUE_FIELD_NAME, addValueFieldLabel, FieldElementType.Input, valueType, false);
    			addValueField.setValue(addValueFieldValue);
    			addValueField.setValueChangedAction(ADD_VALUE_ACTION);
    			break;
    			
    		default:
    			addValueField = null;
    		}
    	}
    }
    
    public FormField getAddValueField()  {
    	return this.addValueField;
    }
    
    public boolean hasAddValueField()  {
    	return (addValueField != null);
    }
    
    public String getAddValueFieldValue()  {
    	return (hasAddValueField() ? addValueField.getValue() : null);
    }
    
    private void initializeSummaryField(FieldInputType valueType, String summaryFieldLabel, String summaryFieldValue)  {
    	if (valueType != null)  {
    		switch (valueType)  {
    		case AlphanumericText:
    		case NumericText:
    		case Decimal:
    		case Money:
    			summaryField = new FormField(SUMMARY_FIELD_NAME, summaryFieldLabel, FieldElementType.Display, valueType, false);
    			summaryField.setValue(summaryFieldValue);
    			summaryField.setDisabled(true);
    			break;
    			
    		default:
    			summaryField = null;
    		}
    	}
    }
    
    public FormField getSummaryField()  {
    	return this.summaryField;
    }
    
    public boolean hasSummaryField()  {
    	return (summaryField != null);
    }
    
    public String getSummaryFieldValue()  {
    	return (hasSummaryField() ? summaryField.getValue() : null);
    }
    
    public void setSummaryFieldValue(String value)  {
    	if (hasSummaryField())  {
    		summaryField.setValue(value);
    	}
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
    public boolean isAddingAllowed()  {
    	return hasAddValueField();
    }
    
    public void setRemovingAllowed(boolean removingAllowed)  {
    	this.isRemovingAllowed = removingAllowed;
    }
    
    public boolean isRemovingAllowed()  {
    	return this.isRemovingAllowed;
    }
    
    public void setRequiresAtLeastOneValue(boolean requiresAtLeastOneValue) {
        this.requiresAtLeastOneValue = requiresAtLeastOneValue;
    }
    
    public boolean isRequiresAtLeastOneValue() {
        return requiresAtLeastOneValue;
    }
    
    public String getValueChangedAction()  {
    	return this.valueChangedAction;
    }
    
    public boolean hasValueChangedAction()  {
    	return (this.valueChangedAction != null);
    }
    
    public void setValueChangedAction(String action)  {
    	this.valueChangedAction = (StringUtils.isNotEmpty(action) ? action : null);
    }

	public List<String> getFormErrors() {
		return formErrors;
	}

	public void setFormErrors(List<String> formErrors) {
		this.formErrors = formErrors;
	}
	
	public void addFormError(String error) {
		formErrors.add(error);
	}
}