package org.jumpmind.pos.core.model;


import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

import org.junit.Test;

public class FormTest {

    @Test
    public void isAssignableFrom_FormSubclass() {
        assertTrue(Form.isAssignableFrom(new FormSubclass()));
    }

    @Test
    public void isAssignableFrom_Noncompatible_Class() {
        assertFalse(Form.isAssignableFrom("foo"));
    }
    
    @Test
    public void isAssignableFrom_MapWithAcceptableKey() {
        Map<String,Object> objMap = new HashMap<>();
        objMap.put("formElements", "anyValue");
        assertTrue(Form.isAssignableFrom(objMap));
    }

    @Test
    public void isAssignableFrom_MapWithUnacceptableKeys() {
        Map<String,Object> objMap = new HashMap<>();
        objMap.put("nonFormAttribute", "anyValue");
        objMap.put("name", "anyValue");
        
        assertFalse(Form.isAssignableFrom(objMap));
    }
    
    /**
     * This method exists as a compile time guard to ensure that if the formElements
     * or formErrors attributes are changed or removed from the Form class, that this
     * test will break and hopefully cause the developer to make implementation fixes
     * to the {@link Form#isAssignableFrom(Object)} method.
     */
    void guardAssignableFrom() {
        Form f = new Form();
        f.getFormElements();
        f.setFormElements(null);
        f.getFormErrors();
        f.setFormErrors(null);
    }
    
    
    public class FormSubclass extends Form {
        private static final long serialVersionUID = 1L;
        
        String additionalAttr;

        public String getAdditionalAttr() {
            return additionalAttr;
        }

        public void setAdditionalAttr(String additionalAttr) {
            this.additionalAttr = additionalAttr;
        }
        
    }
    
}
