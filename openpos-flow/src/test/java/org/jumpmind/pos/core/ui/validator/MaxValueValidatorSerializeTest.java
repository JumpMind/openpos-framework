package org.jumpmind.pos.core.ui.validator;


import java.io.IOException;

import org.jumpmind.pos.core.model.FormField;
import org.jumpmind.pos.core.ui.validator.IValidatorSpec;
import org.jumpmind.pos.util.DefaultObjectMapper;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertTrue;

import com.fasterxml.jackson.core.JsonProcessingException;

public class MaxValueValidatorSerializeTest {
    IValidatorSpec spec;
    FormField formField;
    
    @Before
    public void setUp() throws Exception {
        spec = new MaxValueValidator(10);
        formField = new FormField("json", "x");
    }

    /**
     * Confirm that serialized version of spec contains 'class' attribute
     * indicating that class info was also serialized
     * @throws JsonProcessingException
     */
    @Test
    public void testSimpleJsonSerialize() throws IOException {
        assertTrue(DefaultObjectMapper.defaultObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(spec).contains("org.jumpmind.pos.core.ui.validator.MaxValueValidator"));
    }
    
    @Test
    public void testFormFieldRoundTripWithArrayImpl() throws IOException {
        formField.setValidators(new IValidatorSpec[] {spec});
        String serialized = DefaultObjectMapper.defaultObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(formField);
        assertTrue(serialized.contains("org.jumpmind.pos.core.ui.validator.MaxValueValidator"));
        // Just test that it can be read without throwing exception
        @SuppressWarnings("unused")
        FormField f2 = DefaultObjectMapper.defaultObjectMapper().readValue(serialized, FormField.class);
        int i = 0;
    }

}
