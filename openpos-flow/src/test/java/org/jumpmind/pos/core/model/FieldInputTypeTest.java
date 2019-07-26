package org.jumpmind.pos.core.model;

import static org.junit.Assert.*;

import org.junit.Test;

public class FieldInputTypeTest {

    @Test
    public void testFromStringWithValidValue() {
        assertEquals(FieldInputType.AlphanumericPassword, FieldInputType.fromString("ALPHANUMERICPASSWORD"));
        assertEquals(FieldInputType.Money, FieldInputType.fromString("money"));
        assertEquals(FieldInputType.ComboBox, FieldInputType.fromString("ComboBox"));
        assertEquals(FieldInputType.PopTart, FieldInputType.fromString("Poptart"));
    }

    @Test
    public void testFromStringWithInValidValue() {
        assertNull(FieldInputType.fromString("InvalidValue"));
        assertNull(FieldInputType.fromString(""));
        assertNull(FieldInputType.fromString(null));
    }
    
}
