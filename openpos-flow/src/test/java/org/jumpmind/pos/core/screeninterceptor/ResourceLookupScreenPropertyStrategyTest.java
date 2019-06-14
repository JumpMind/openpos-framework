package org.jumpmind.pos.core.screeninterceptor;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

import org.jumpmind.pos.core.service.IResourceLookupService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ResourceLookupScreenPropertyStrategyTest {

    @Mock
    IResourceLookupService lookupService;
    
    @InjectMocks
    ResourceLookupScreenPropertyStrategy testedStrategy;
    
    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testVariableSubstitutionFromParameter() {
        // Mocks getting a string from the ResourceLookupService from a property named 'some.property'
        when(lookupService.getString("pos", "12345-11", "common", "some.property")).thenReturn("String with {{variable1}}");
        
        // Create JSON property string with 'value1' as the value for referenced variable with name 'variable1'
        String json = new ResourceLookupStringBuilder("some.property").addParameter("variable1", "value1").toJson();
        
        // Check that result has 'value1' substituted for {{variable1}}
        Object result = this.testedStrategy.doStrategy("pos", "12345-11", json, String.class, null, null);
        String resultString = result.toString();
        assertEquals(resultString, "String with value1");
    }

    @Test
    public void testVariableSubstitutionFromLookupService() {
        // Mocks getting a string from the ResourceLookupService from a property named 'some.property'
        when(lookupService.getString("pos", "12345-11", "common", "some.property")).thenReturn("String with {{variable1}}");
        when(lookupService.getString("pos", "12345-11", "common", "variable1")).thenReturn("value1");
        
        // Create JSON property string with no given parameters, which should cause
        // lookup service to be used to resolve value of 'variable1'
        String json = new ResourceLookupStringBuilder("some.property").toJson();
        
        // Check that result has 'value1' substituted for {{variable1}}
        Object result = this.testedStrategy.doStrategy("pos", "12345-11", json, String.class, null, null);
        String resultString = result.toString();
        assertEquals(resultString, "String with value1");
    }

    @Test
    public void testMultipleVariableSubstitutionsFromLookupService() {
        // Mocks getting a string from the ResourceLookupService from a property named 'some.property'
        when(lookupService.getString("pos", "12345-11", "common", "some.property")).thenReturn("String with {{variable1}} and {{variable2}}");
        when(lookupService.getString("pos", "12345-11", "common", "variable1")).thenReturn("value1");
        when(lookupService.getString("pos", "12345-11", "common", "variable2")).thenReturn("value2");
        
        // Create JSON property string with no given parameters, which should cause
        // lookup service to be used to resolve value of 'variable1'
        String json = new ResourceLookupStringBuilder("some.property").toJson();
        
        // Check that result has 'value1' substituted for {{variable1}}
        Object result = this.testedStrategy.doStrategy("pos", "12345-11", json, String.class, null, null);
        String resultString = result.toString();
        assertEquals(resultString, "String with value1 and value2");
    }
    
    /**
     * Nested variable references are not supported.
     */
    @Test(expected=RuntimeException.class)
    public void testNestedVariableSubstitutionFromLookupService() {
        // Mocks getting a string from the ResourceLookupService from a property named 'some.property'
        when(lookupService.getString("pos", "12345-11", "common", "some.property")).thenReturn("String with {{variable1}}");
        when(lookupService.getString("pos", "12345-11", "common", "variable1")).thenReturn("some value with nested variable {{variable2}}");
        when(lookupService.getString("pos", "12345-11", "common", "variable2")).thenReturn("value2");
        
        // Create JSON property string with no given parameters, which should cause
        // lookup service to be used to resolve value of 'variable1'
        String json = new ResourceLookupStringBuilder("some.property").toJson();
        
        Object result = this.testedStrategy.doStrategy("pos", "12345-11", json, String.class, null, null);
    }
    
}
