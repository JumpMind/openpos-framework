package org.jumpmind.pos.core.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Form implements Serializable {

    public static final String PATTERN_EMAIL =  "^([a-zA-Z0-9_\\-\\.]+)@([a-zA-Z0-9_\\-\\.]+)\\.([a-zA-Z]{2,5})$";
    public static final String PATTERN_MONEY =  "^(\\d{0,9}\\.\\d{0,2}|\\d{1,9})$";
    public static final String PATTERN_PERCENT =  "^100$|^\\d{0,2}(\\.\\d{1,2})?$|^\\d{0,2}(\\.)?"; // 100-0, Only two decimal places allowed.
    public static final String PATTERN_DATE = "^(\\d{2})/(\\d{2})/(\\d{4}$)";
    public static final String PATTERN_NO_YEAR_DATE = "^(\\d{2})/(\\d{2})$";
    // TODO: This pattern may be too restrictive. 
    public static final String PATTERN_US_PHONE_NUMBER = "^\\d{10}$";
    private static final long serialVersionUID = 1L;

    private List<IFormElement> formElements = new ArrayList<IFormElement>();
    
    private List<String> formErrors = new ArrayList<String>();
    
    private boolean requiresAtLeastOneValue = false;
    
    private String name;

    public List<IFormElement> getFormElements() {
        return formElements;
    }

    public void setFormElements(List<IFormElement> formElements) {
        this.formElements = formElements;
    }
    
    public IFormElement addFormElement(IFormElement formElement) {
        formElements.add(formElement);
        return formElement;
    }
    
    public CheckboxField addCheckbox(String id, String label) {
        return this.addCheckbox(id, label, false);
    }
    
    public CheckboxField addCheckbox(String id, String label, boolean checked) {
        CheckboxField field = new CheckboxField(id, label, checked);
        formElements.add(field);
        return field;
    }
    
    public ComboField addComboBox(String fieldId, String label, String... values) {
        return addComboBox(fieldId, label, values != null && values.length > 0 ? Arrays.asList(values) : new ArrayList<>());
    }
    
    public ComboField addComboBox(String fieldId, String label, List<String> values) {
        ComboField field = new ComboField(fieldId, label, null, values);
        formElements.add(field);
        return field;        

    }
    
    public ComboField addComboBox(String fieldId, String label, List<String> values, boolean required) {
        ComboField field = new ComboField(fieldId, label, null, values);
        field.setRequired(required);
        formElements.add(field);
        return field;        

    }
    
    public ComboField addComboBox(String fieldId, String label, String value, List<String> values, boolean required) {
        ComboField field = new ComboField(fieldId, label, null, values);
        field.setRequired(required);
        field.setValue(value);
        formElements.add(field);
        return field;        
    }
    
    public PopTartField addPopTart(String fieldId, String label, String... values) {
        return addPopTart(fieldId, label, values != null && values.length > 0 ? Arrays.asList(values) : new ArrayList<>());
    }
    
    public PopTartField addPopTart(String fieldId, String label, List<String> values) {
        PopTartField field = new PopTartField(fieldId, label, null, values);
        formElements.add(field);
        return field;        

    }
    
    public PopTartField addPopTart(String fieldId, String label, List<String> values, boolean required) {
        PopTartField field = new PopTartField(fieldId, label, null, values);
        field.setRequired(required);
        formElements.add(field);
        return field;        

    }
    
    public PopTartField addPopTart(String fieldId, String label, String value, List<String> values, boolean required) {
        PopTartField field = new PopTartField(fieldId, label, null, values);
        field.setRequired(required);
        field.setValue(value);
        formElements.add(field);
        return field;        
    }
    
    public FormListField addListField(String fieldId, String label, String placeholder, boolean required, List<String> values) {
    		FormListField field = new FormListField(fieldId, label, placeholder, values);
    		field.setRequired(required);
    		formElements.add(field);
    		return field;
    }
    
    public ToggleField addToggleButton(String fieldId, String label, List<String> values, String defaultVal) {
    		ToggleField field = new ToggleField(fieldId, label, values, defaultVal);
    		formElements.add(field);
    		return field;
    }
    
    public static FormField createDateField(String fieldId, String label, String value, boolean required, boolean hideCalendar) {
        FormField formField = new FormField(fieldId, label, FieldElementType.Input, FieldInputType.Date, required);
        formField.setPattern(PATTERN_DATE);
        formField.setValue(value);
        formField.put("hideCalendar", hideCalendar);
        return formField;
    }
    
    public FormField addDateField(String fieldId, String label, String value, boolean required, boolean hideCalendar) {
        FormField formField = createDateField(fieldId, label, value, required, hideCalendar);
        formElements.add(formField);
        return formField;
    }

    public FormField addDateField(String fieldId, String label, String value, boolean required) {
        return this.addDateField(fieldId, label, value, required, false);
    }
    
	public static FormField createNoYearDateField(String fieldId, String label, String value, boolean required,
			boolean hideCalendar) {
		FormField formField = new FormField(fieldId, label, FieldElementType.Input, FieldInputType.NoYearDate, required);
		formField.setPattern(PATTERN_NO_YEAR_DATE);
		formField.setValue(value);
		formField.put("hideCalendar", hideCalendar);
		return formField;
	}

	public FormField addNoYearDateField(String fieldId, String label, String value, boolean required,
			boolean hideCalendar) {
		FormField formField = createNoYearDateField(fieldId, label, value, required, hideCalendar);
		formElements.add(formField);
		return formField;
	}

	public FormField addNoYearDateField(String fieldId, String label, String value, boolean required) {
		return this.addNoYearDateField(fieldId, label, value, required, false);
	}
    
    public FormField addIncomeField(String fieldId, String label, String value, boolean required) {
        FormField formField = createIncomeField(fieldId, label, value, required);
        formElements.add(formField);
        return formField;
    }
    
    public FormField addNumericField(String fieldId, String label, String value, boolean required) {
        FormField formField = createNumericField(fieldId, label, value, required);
        formElements.add(formField);
        return formField;
    }
    
    public static FormField createNumericField(String fieldId, String label, String value, boolean required) {
        FormField formField = new FormField(fieldId, label, FieldElementType.Input, FieldInputType.NumericText, required);
        formField.setValue(value);
        return formField;
    }
    
    public FormField addDecimalField(String fieldId, String label, String value, boolean required) {
        FormField formField = createDecimalField(fieldId, label, value, required);
        formElements.add(formField);
        return formField;
    }
    
    public static FormField createDecimalField(String fieldId, String label, String value, boolean required) {
        FormField formField = new FormField(fieldId, label, FieldElementType.Input, FieldInputType.Decimal, required);
        formField.setValue(value);
        return formField;
    }

    public static FormField createIncomeField(String fieldId, String label, String value, boolean required) {
        FormField formField = new FormField(fieldId, label, FieldElementType.Input, FieldInputType.Income, required);
        formField.setValue(value);
        return formField;
    }
    
    public static FormField createMoneyField(String fieldId, String label, String value, boolean required) {
        FormField formField = new FormField(fieldId, label, FieldElementType.Input, FieldInputType.Money, required);
        formField.setPattern(PATTERN_MONEY);
        formField.setValue(value);
        return formField;
    }
    
    public FormField addMoneyField(String fieldId, String label, String value, boolean required) {
        FormField formField = createMoneyField(fieldId, label, value, required);
        formElements.add(formField);
        return formField;
    }
    
    public static FormField createEmailField(String fieldId, String label, String value, boolean required) {
        FormField formField = new FormField(fieldId, label, FieldElementType.Input, FieldInputType.AlphanumericText, required);
        formField.setPattern(PATTERN_EMAIL);
        formField.setValue(value);
        return formField;
    }
    
    public FormField addEmailField(String fieldId, String label, String value, boolean required) {
        FormField formField = createEmailField(fieldId, label, value, required);
        formElements.add(formField);
        return formField;
    }
    
    public static FormField createPostalCodeField(String fieldId, String label, String value, boolean required) {
        FormField formField = new FormField(fieldId, label, FieldElementType.Input, FieldInputType.PostalCode, required);
        formField.setValue(value);
        return formField;
    }
    
    public FormField addPostalCodeField(String fieldId, String label, String value, boolean required) {
        FormField formField = createPostalCodeField(fieldId, label, value, required);
        formElements.add(formField);
        return formField;
    }
    
    public static FormField createPhoneField(String fieldId, String label, String value, boolean required) {
        FormField formField = new FormField(fieldId, label, FieldElementType.Input, FieldInputType.Phone, required);
        formField.setValue(value);
        return formField;
    }
    
    public FormField addPhoneField(String fieldId, String label, String value, boolean required) {
        FormField formField = createPhoneField(fieldId, label, value, required);
        formElements.add(formField);
        return formField;
    }
    
    public FormField addPercentField(String fieldId, String label, String value, boolean required) {
        FormField formField = new FormField(fieldId, label, FieldElementType.Input, FieldInputType.Percent, required);
        formField.setValue(value);
        formElements.add(formField);
        return formField;
    }
    
    public FormField addTextField(String fieldId, String label, String value, boolean required) {
        FormField formField = new FormField(fieldId, label, FieldElementType.Input, FieldInputType.AlphanumericText, required);
        formField.setValue(value);
        formElements.add(formField);
        return formField;
    }
    
    public FormField addTextAreaField(String fieldId, String label, String value, boolean required) {
    		FormField formField = new FormField(fieldId, label, FieldElementType.Input, FieldInputType.TextArea, required);
    		formField.setValue(value);
    		formElements.add(formField);
    		return formField;
    }
    
    public FormField addDisplayField( String fieldId, String label, String value, FieldInputType type ) {
    		FormField formField = new FormField(fieldId, label, null );
    		formField.setElementType(FieldElementType.Display);
    		formField.setInputType(type);
    		formField.setValue(value);
    		formElements.add(formField);
    		return formField;
    }
    
    public FormField getTextField(String fieldId) {
        IFormElement formElement = this.getFormElement(fieldId);
        if (formElement != null && formElement instanceof FormField) {
            return (FormField) formElement;
        }
        
        return null;
    }

    public ComboField getComboField(String fieldId) {
        IFormElement formElement = this.getFormElement(fieldId);
        if (formElement != null && formElement instanceof ComboField) {
            return (ComboField) formElement;
        }
        
        return null;
    }
    
    public void addSeparator() {
        formElements.add(new FormDisplayField(FieldElementType.Separator));
    }

    public void removeFormElement(IFormElement formElement) {
        formElements.remove(formElement);
    }
    
    public void removeFormElement(String elementId) {
        IFormElement elementToRemove = this.getFormElement(elementId);
        if (null != elementToRemove) {
            formElements.remove(elementToRemove);
        }
    }
        
    public IFormElement getFormElement(String elementId) {
        return formElements.stream().filter(f->elementId.equals(f.getId())).findFirst().orElse(null);
    }
    
    public String getFormElementValue(String elementId) {
        String returnValue = null;
        IFormElement formElement = this.getFormElement(elementId);
        if (formElement != null && formElement instanceof FormField) {
            returnValue = ((FormField) formElement).getValue();
        }
        
        return returnValue;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
    public void setRequiresAtLeastOneValue(boolean requiresAtLeastOneValue) {
        this.requiresAtLeastOneValue = requiresAtLeastOneValue;
    }
    
    public boolean isRequiresAtLeastOneValue() {
        return requiresAtLeastOneValue;
    }
    
    // TODO: Move this off the form and onto the element. 
    // This means different things to different elements. 
    public String getString(String id) {
        for (IFormElement element : formElements) {
            if (element.getId().equals(id)) {
                if (element instanceof FormField) {
                    return ((FormField)element).getValue();                            
                }
            }
        }
        return null;
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
