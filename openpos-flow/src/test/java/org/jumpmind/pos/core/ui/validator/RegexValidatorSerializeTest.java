package org.jumpmind.pos.core.ui.validator;


import java.io.IOException;
import java.util.Map;

import org.jumpmind.pos.core.model.FormField;
import org.jumpmind.pos.core.ui.validator.IValidatorSpec;
import org.jumpmind.pos.util.DefaultObjectMapper;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertTrue;

import com.fasterxml.jackson.core.JsonProcessingException;

public class RegexValidatorSerializeTest {
    IValidatorSpec spec;
    FormField formField;
    
    @Before
    public void setUp() throws Exception {
        spec = new RegexValidator(".*", "g");
        formField = new FormField("json", "x");
    }

    /**
     * Confirm that serialized version of spec contains 'class' attribute
     * indicating that class info was also serialized
     * @throws JsonProcessingException
     */
    @Test
    public void testSimpleJsonSerialize() throws IOException {
        assertTrue(DefaultObjectMapper.defaultObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(spec).contains("org.jumpmind.pos.core.ui.validator.RegexValidator"));
    }
    
    @Test
    public void testFormFieldRoundTripWithArrayImpl() throws IOException {
        formField.setValidators(new IValidatorSpec[] {spec});
        String serialized = DefaultObjectMapper.defaultObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(formField);
        assertTrue(serialized.contains("org.jumpmind.pos.core.ui.validator.RegexValidator"));
        // Just test that it can be read without throwing exception
        @SuppressWarnings("unused")
        FormField f2 = DefaultObjectMapper.defaultObjectMapper().readValue(serialized, FormField.class);
    }

}
