package org.jumpmind.pos.core.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class Form implements Serializable {

    /** Use {@link FieldPattern#EMAIL} instead*/@Deprecated()
    public static final String PATTERN_EMAIL =  FieldPattern.EMAIL;
    /** Use {@link FieldPattern#MONEY} instead*/@Deprecated()
    public static final String PATTERN_MONEY =  FieldPattern.MONEY;
    /** Use {@link FieldPattern#PERCENT} instead*/@Deprecated()
    public static final String PATTERN_PERCENT =  FieldPattern.PERCENT;
    /** Use {@link FieldPattern#DATE} instead*/@Deprecated()
    public static final String PATTERN_DATE = FieldPattern.DATE;
    /** Use {@link FieldPattern#YY_DATE} instead*/@Deprecated()
    public static final String PATTERN_YY_DATE = FieldPattern.YY_DATE;
    /** Use {@link FieldPattern#NO_YEAR_DATE} instead*/@Deprecated()
    public static final String PATTERN_NO_YEAR_DATE = FieldPattern.NO_YEAR_DATE;
    /** Use {@link FieldPattern#US_PHONE_NUMBER} instead*/@Deprecated()
    public static final String PATTERN_US_PHONE_NUMBER = FieldPattern.US_PHONE_NUMBER;
    
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
    
    public CheckboxField addCheckbox(String id, String label, boolean checked, boolean required) {
        CheckboxField field = new CheckboxField(id, label, checked);
        field.setRequired(required);
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
    
    public SearchablePopTartField addSearchablePopTart(String fieldId, String label, String... values) {
        return addSearchablePopTart(fieldId, label, values != null && values.length > 0 ? Arrays.asList(values) : new ArrayList<>());
    }
    
    public SearchablePopTartField addSearchablePopTart(String fieldId, String label, List<String> values) {
    	SearchablePopTartField field = new SearchablePopTartField(fieldId, label, null, values);
        formElements.add(field);
        return field;        

    }
    
    public SearchablePopTartField addSearchablePopTart(String fieldId, String label, List<String> values, boolean required) {
    	SearchablePopTartField field = new SearchablePopTartField(fieldId, label, null, values);
        field.setRequired(required);
        formElements.add(field);
        return field;        

    }
    
    public SearchablePopTartField addSearchablePopTart(String fieldId, String label, String value, List<String> values, boolean required) {
    	SearchablePopTartField field = new SearchablePopTartField(fieldId, label, null, values);
        field.setRequired(required);
        field.setValue(value);
        formElements.add(field);
        return field;        
    }
    
    public SearchablePopTartField addSearchablePopTart(String fieldId, String label, String value, List<String> values, boolean required, String instructions) {
    	SearchablePopTartField field = new SearchablePopTartField(fieldId, label, null, values);
        field.setRequired(required);
        field.setValue(value);
        field.setInstructions(instructions);
        formElements.add(field);
        return field;        
    }

    public CounterField addCounterField(String fieldId, String label) {
        return addCounterField(fieldId, label, false, null, null);
    }

    public CounterField addCounterField(String fieldId, String label, boolean required) {
        return addCounterField(fieldId, label, required, null, null);
    }

    public CounterField addCounterField(String fieldId, String label, boolean required, Integer minValue) {
        return addCounterField(fieldId, label, required, minValue, null);
    }

    public CounterField addCounterField(String fieldId, String label, boolean required, Integer minValue, Integer maxValue) {
        CounterField field = new CounterField(fieldId, label, required, minValue, maxValue);
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
    
    public static DateField createDateField(String fieldId, String label, String value, boolean required, boolean hideCalendar) {
        return createDateField(fieldId, label, value, required, hideCalendar, null, null);
    }
    
    public static DateField createDateField(String fieldId, String label, String value, boolean required, boolean hideCalendar, Date minDate, Date maxDate) {
        DateField formField = new DateField(fieldId, label, required, hideCalendar, minDate, maxDate);
        formField.setValue(value);
        return formField;
    }

    public DateField addDateField(String fieldId, String label, String value, boolean required, boolean hideCalendar, Date minDate) {
        DateField dateField = createDateField(fieldId, label, value, required, hideCalendar, minDate, null);
        formElements.add(dateField);
        return dateField;
    }
    
    public DateField addDateField(String fieldId, String label, String value, boolean required, boolean hideCalendar) {
        DateField formField = createDateField(fieldId, label, value, required, hideCalendar, null, null);
        formElements.add(formField);
        return formField;
    }

    public DateField addDateField(String fieldId, String label, String value, boolean required) {
        return this.addDateField(fieldId, label, value, required, false);
    }
    
	public static FormField createNoYearDateField(String fieldId, String label, String value, boolean required,
			boolean hideCalendar) {
		FormField formField = new FormField(fieldId, label, FieldElementType.Input, FieldInputType.NoYearDate, required);
		formField.setPattern(FieldPattern.NO_YEAR_DATE);
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
    
	public FormField addTimeField(String fieldId, String label, String value, boolean required) {
        FormField formField = new FormField(fieldId, label, FieldElementType.Input, FieldInputType.Time, required);
        formField.setValue(value);
        formElements.add(formField);
        return formField;
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
        formField.setPattern(FieldPattern.EMAIL);
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
    
    public FormField createUSPostalCodeField(String fieldId, String label, String value, boolean required) {
        FormField formField = new FormField(fieldId, label, FieldElementType.Input, FieldInputType.USPostalCode, required);
        formField.setValue(value);
        return formField;
    }
    
    public FormField addUSPostalCodeField(String fieldId, String label, String value, boolean required) {
        FormField formField = createUSPostalCodeField(fieldId, label, value, required);
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
    
    public FormField addPasswordField(String fieldId, String label, String value, boolean required) {
        FormField formField = new FormField(fieldId, label, FieldElementType.Input, FieldInputType.AlphanumericPassword, required);
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

    public FormField addDisplayField(String fieldId, String label, String value, FieldInputType type) {
        FormField formField = new FormField(fieldId, label, null);
        formField.setElementType(FieldElementType.Display);
        formField.setInputType(type);
        formField.setValue(value);
        formElements.add(formField);
        return formField;
    }
    
    public FormButton addButton( String id, String label, String buttonAction ) {
    	FormButton formButton = new FormButton(id, label, buttonAction);
    	
    	formElements.add(formButton);
    	return formButton;
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
    
    public FormListField getListField(String fieldId) {
        IFormElement formElement = this.getFormElement(fieldId);
        if (formElement != null && formElement instanceof FormListField) {
            return (FormListField) formElement;
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
        
    public boolean hasFormElement(String elementId) {
        return this.getFormElement(elementId) != null;
    }

    public IFormElement getFormElement(String elementId) {
        return formElements.stream().filter(f -> elementId.equals(f.getId())).findFirst().orElse(null);
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
            if (id.equals(element.getId())) {
                if (element instanceof FormField) {
                    return ((FormField) element).getValue();
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
