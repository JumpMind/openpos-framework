package org.jumpmind.pos.util;

import static org.junit.Assert.assertEquals;

import org.jumpmind.pos.util.ReflectUtils;
import org.junit.Test;

public class ReflectUtilsTest {

    @Test
    public void testSettingAnEnum() {
        TestEnum test = new TestEnum();
        ReflectUtils.setProperty(test, "field", "ONE");
        assertEquals(TestEnum.Test.ONE, test.field);
    }
    
    static class TestEnum {
        enum Test {ONE,TWO};
        
        Test field;
    }
}
