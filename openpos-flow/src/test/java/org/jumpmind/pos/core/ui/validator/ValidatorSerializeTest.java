package org.jumpmind.pos.core.ui.validator;


import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;

import org.jumpmind.pos.core.model.FormField;
import org.jumpmind.pos.core.ui.validator.IValidatorSpec;
import org.jumpmind.pos.core.ui.validator.MinValueValidator;
import org.jumpmind.pos.util.DefaultObjectMapper;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertTrue;

import com.fasterxml.jackson.core.JsonProcessingException;

public class ValidatorSerializeTest {
    IValidatorSpec spec;
    FormField formField;
    
    @Before
    public void setUp() throws Exception {
        spec = new MinValueValidator(10);
        formField = new FormField("json", "x");
    }

    /**
     * Confirm that serialized version of spec contains 'class' attribute
     * indicating that class info was also serialized
     * @throws JsonProcessingException
     */
    @Test
    public void testSimpleJsonSerialize() throws IOException {
        assertTrue(DefaultObjectMapper.defaultObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(spec).contains("@class"));
    }
    
    @SuppressWarnings("deprecation")
    @Test
    public void testFormFieldRoundTripWithSetImpl() throws IOException {
        formField.setValidators(new HashSet<>(Arrays.asList(spec)));
        String serialized = DefaultObjectMapper.defaultObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(formField);
        assertTrue(serialized.contains("org.jumpmind.pos.core.ui.validator.MinValueValidator"));
        // Just test that it can be read without throwing exception
        @SuppressWarnings("unused")
        FormField f2 = DefaultObjectMapper.defaultObjectMapper().readValue(serialized, FormField.class);
    }
    
    @Test
    public void testFormFieldRoundTripWithListImpl() throws IOException {
        formField.setValidators(Arrays.asList(spec));
        String serialized = DefaultObjectMapper.defaultObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(formField);
        assertTrue(serialized.contains("org.jumpmind.pos.core.ui.validator.MinValueValidator"));
        // Just test that it can be read without throwing exception
        @SuppressWarnings("unused")
        FormField f2 = DefaultObjectMapper.defaultObjectMapper().readValue(serialized, FormField.class);
    }

    @Test
    public void testFormFieldRoundTripWithArrayImpl() throws IOException {
        formField.setValidators(new IValidatorSpec[] {spec});
        String serialized = DefaultObjectMapper.defaultObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(formField);
        assertTrue(serialized.contains("org.jumpmind.pos.core.ui.validator.MinValueValidator"));
        // Just test that it can be read without throwing exception
        @SuppressWarnings("unused")
        FormField f2 = DefaultObjectMapper.defaultObjectMapper().readValue(serialized, FormField.class);
    }

    @Test
    public void testFormFieldRoundTripWithAdd() throws IOException {
        formField.addValidators(spec);
        String serialized = DefaultObjectMapper.defaultObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(formField);
        assertTrue(serialized.contains("org.jumpmind.pos.core.ui.validator.MinValueValidator"));
        // Just test that it can be read without throwing exception
        @SuppressWarnings("unused")
        FormField f2 = DefaultObjectMapper.defaultObjectMapper().readValue(serialized, FormField.class);
    }
}
