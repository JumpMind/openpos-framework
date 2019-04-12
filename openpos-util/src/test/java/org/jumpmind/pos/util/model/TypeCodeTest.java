package org.jumpmind.pos.util.model;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Set;

import org.apache.commons.lang3.SerializationUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TypeCodeTest {

    public static final class MyTypeCode extends AbstractTypeCode {
        private static final long serialVersionUID = 1L;
        
        public static final MyTypeCode CODE1 = new MyTypeCode("CODE1");
        public static final MyTypeCode CODE2 = new MyTypeCode("CODE2");
        public static final MyTypeCode CODE3 = new MyTypeCode("CODE3");
        
        public static MyTypeCode of(String value) {
            return AbstractTypeCode.of(MyTypeCode.class, value);
        }

        private MyTypeCode(String value) {
            super(value);
        }
        
    }
    
    /**
     * Note: If codes are added dynamically by declaring them in this way,
     * the declaring class (MyAddedTypeCode in this case), must be loaded before
     * any attempts to dynamically reference a type code value using MyTypeCode.of(stringValue);
     * the 'of' method will return null if the type code has not been created yet.
     *
     */
    public static final class MyAddedTypeCodes {
        public static final MyTypeCode EXTRA_TYPE_CODE_1 = new MyTypeCode("EXTRA_TYPE_CODE_1");
        public static final MyTypeCode EXTRA_TYPE_CODE_2 = new MyTypeCode("EXTRA_TYPE_CODE_2");
        
    }

    public static final class MyTypeCodeContainer {
        MyTypeCode myTypeCodeValue;

        public MyTypeCode getMyTypeCodeValue() {
            return myTypeCodeValue;
        }

        public void setMyTypeCodeValue(MyTypeCode myTypeCodeValue) {
            this.myTypeCodeValue = myTypeCodeValue;
        }
        
    }
    
    @BeforeClass
    public static void setUpOnTestStart() {
        // Forces MyAddedTypeCodes to load at startup of test.  If this isn't
        // done, then testStaticAddedCodes test will fail when it tests for
        // a dynamically created type code whose value matches one of the type
        // code values defined in MyAddedTypeCodes
        // 
        // Users of the openpos framework may need to ensure the class that contains
        // their type code declarations is loaded early in application startup.
        @SuppressWarnings("unused")
        MyTypeCode extraTypeCode1 = MyAddedTypeCodes.EXTRA_TYPE_CODE_1;
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    
    @Test
    public void testConstantCodes() {
        MyTypeCode staticCode = MyTypeCode.CODE1;
        MyTypeCode runtimeCode = MyTypeCode.of("CODE1");
        
        // Objects are equal
        assertEquals(staticCode, runtimeCode);
        
        // Objects are the same since, .of method first looks for matching static
        // member
        assertSame(staticCode, runtimeCode);
        
        assertEquals(staticCode.hashCode(), runtimeCode.hashCode());
    }

    @Test
    public void testRuntimeCodesViaMake() {
        MyTypeCode runtimeTypeCode = ITypeCode.make(MyTypeCode.class, "NEW_VALUE");
        assertEquals("NEW_VALUE", runtimeTypeCode.value());
        assertEquals("NEW_VALUE", runtimeTypeCode.toString());
        
        assertNotEquals(MyTypeCode.CODE1, runtimeTypeCode);
    }

    @Test
    public void testRuntimeCodeUsingOf() {
        MyTypeCode runtimeTypeCode = MyTypeCode.of("OF_VALUE");
        
        // Since this is a new MyTypeCode value that is not declared as a static
        // member of the MyTypeCode class, of() returns null.  The ITypeCode.make()
        // method must be used to force creation of a new value.
        assertNull(runtimeTypeCode);
    }
    
    @Test
    public void testStaticAddedCodes() {
        MyTypeCode runtimeTypeCode = MyTypeCode.of("EXTRA_TYPE_CODE_1");
        
        // Objects are equal
        assertEquals(MyAddedTypeCodes.EXTRA_TYPE_CODE_1, runtimeTypeCode);
        
        // String values are equal
        assertEquals(MyAddedTypeCodes.EXTRA_TYPE_CODE_1.toString(), runtimeTypeCode.toString());
        assertEquals(MyAddedTypeCodes.EXTRA_TYPE_CODE_1.value(), runtimeTypeCode.value());
        assertEquals(MyAddedTypeCodes.EXTRA_TYPE_CODE_1.toString(), runtimeTypeCode.value());
        
        // Not the same object, because MyAddedTypeCodes.EXTRA_TYPE_CODE_1 is
        // not declared in the MyTypeCode class.
        assertNotSame(MyAddedTypeCodes.EXTRA_TYPE_CODE_1, runtimeTypeCode);
        
        // Hashcodes will be the same
        assertEquals(MyAddedTypeCodes.EXTRA_TYPE_CODE_1.hashCode(), runtimeTypeCode.hashCode());

    }


    static final class ExTypeCode extends AbstractTypeCode {
        private static final long serialVersionUID = 1L;
        
        public static final ExTypeCode CODE1 = new ExTypeCode("CODE1");
        public static final ExTypeCode CODE2 = new ExTypeCode("CODE2");
        public static final ExTypeCode CODE3 = new ExTypeCode("CODE3");
        
        public static ExTypeCode of(String value) {
            return AbstractTypeCode.of(ExTypeCode.class, value);
        }
        
        private ExTypeCode(String value) {
            super(value);
        }

    }

    static final ExTypeCode EXTRA_TYPE_CODE_1 = ExTypeCode.of("EXTRA_TYPE_CODE_1");
    static final ExTypeCode EXTRA_TYPE_CODE_2 = ITypeCode.make(ExTypeCode.class, "EXTRA_TYPE_CODE_2");

    @Test
    public void testRegistry() {

        Set<ExTypeCode> values = ITypeCodeRegistry.values(ExTypeCode.class);
        assertEquals(4, values.size());
        assertTrue(values.contains(ExTypeCode.CODE1));
        assertTrue(values.contains(ExTypeCode.CODE2));
        assertTrue(values.contains(ExTypeCode.CODE3));
        assertTrue(values.contains(EXTRA_TYPE_CODE_2));
        assertNull(EXTRA_TYPE_CODE_1);
        
    }

    @Test
    public void testStaticTypeCodeExistsInRegistry() {
        assertTrue(ITypeCodeRegistry.exists(MyTypeCode.class, "CODE1"));
    }

    @Test
    public void testTypeCodeDoesNotExistInRegistry() {
        assertFalse(ITypeCodeRegistry.exists(ExTypeCode.class, "CODE"));
    }
    
    @Test
    public void testDynamicTypeCodeExistsInRegistry() {
        assertTrue(ITypeCodeRegistry.exists(ExTypeCode.class, "EXTRA_TYPE_CODE_2"));
    }
    
    @Test
    public void testDuplicateTypeCode() {
        final ExTypeCode DUPLICATE = ExTypeCode.of("CODE1");

        Set<ExTypeCode> values = ITypeCodeRegistry.values(ExTypeCode.class);
        assertEquals(4, values.size());
        assertSame(ExTypeCode.CODE1, DUPLICATE);
        
    }

    @Test
    public void testIsAssignable() {
        assertTrue(ITypeCode.class.isAssignableFrom(MyTypeCode.class));
    }
    
    
    @Test
    public void testMakeTypeCodeWithNullValue() {
        assertNull(ITypeCode.make(ExTypeCode.class, null));
    }

    @Test
    public void testOfMethodWithNullValue() {
        assertNull(ExTypeCode.of(null));
    }

    @Test(expected = NullPointerException.class)
    public void testMakeTypeCodeWithNullClass() {
        ITypeCode.make(null, "VALUE1");
    }
    
    @Test
    public void testCompare() {
        assertTrue(0 == ExTypeCode.CODE1.compareTo(ExTypeCode.CODE1));
        assertTrue(0 > ExTypeCode.CODE1.compareTo(ExTypeCode.CODE2));
        assertTrue(0 > ExTypeCode.CODE1.compareTo(ExTypeCode.CODE3));
        
        assertTrue(0 < ExTypeCode.CODE2.compareTo(ExTypeCode.CODE1));
        assertTrue(0 == ExTypeCode.CODE2.compareTo(ExTypeCode.CODE2));
        assertTrue(0 > ExTypeCode.CODE2.compareTo(ExTypeCode.CODE3));

        assertTrue(0 < ExTypeCode.CODE3.compareTo(ExTypeCode.CODE1));
        assertTrue(0 < ExTypeCode.CODE3.compareTo(ExTypeCode.CODE2));
        assertTrue(0 == ExTypeCode.CODE3.compareTo(ExTypeCode.CODE3));
        
        assertTrue(0 < EXTRA_TYPE_CODE_2.compareTo(ExTypeCode.CODE1));
        assertTrue(0 < EXTRA_TYPE_CODE_2.compareTo(ExTypeCode.CODE2));
        assertTrue(0 < EXTRA_TYPE_CODE_2.compareTo(ExTypeCode.CODE3));
        
    }
    
    static final class TypeCodeWithDups extends AbstractTypeCode {
        private static final long serialVersionUID = 1L;
        
        public static final TypeCodeWithDups CODE1 = new TypeCodeWithDups("CODE1");
        public static final TypeCodeWithDups CODE1_DUP = new TypeCodeWithDups("CODE1");
        public static final TypeCodeWithDups CODE1_DUP_2 = TypeCodeWithDups.of("CODE1");
        
        
        public static TypeCodeWithDups of(String value) {
            return AbstractTypeCode.of(TypeCodeWithDups.class, value);
        }

        private TypeCodeWithDups(String value) {
            super(value);
        }

    }
    
    @Test
    public void testSameValueDifferentTypes() {
        assertNotEquals(TypeCodeWithDups.CODE1, ExTypeCode.CODE1);
    }

    @Test
    public void testSameValueDups() {
        // All should be equal since they have the same string value
        assertEquals(TypeCodeWithDups.CODE1, TypeCodeWithDups.CODE1_DUP);
        assertEquals(TypeCodeWithDups.CODE1, TypeCodeWithDups.CODE1_DUP_2);
        assertEquals(TypeCodeWithDups.CODE1_DUP, TypeCodeWithDups.CODE1_DUP_2);
        
        // None of them should be the same object because the first two were
        // created with new and when the third is created using .of, there will be two other
        // static fields with duplicate values and since it's indeterminate which
        // one should be picked, a new instance will be returned.
        assertNotSame(TypeCodeWithDups.CODE1, TypeCodeWithDups.CODE1_DUP);
        assertNotSame(TypeCodeWithDups.CODE1, TypeCodeWithDups.CODE1_DUP_2);
        assertNotSame(TypeCodeWithDups.CODE1_DUP, TypeCodeWithDups.CODE1_DUP_2);
        
        Set<TypeCodeWithDups> values = ITypeCodeRegistry.values(TypeCodeWithDups.class);
        assertEquals(1, values.size());
    }
    
    public static final String EXPECTED_MyTypeCode_CODE1_JSON = 
        "{\"value\":\"CODE1\",\"class\":\"org.jumpmind.pos.util.model.TypeCodeTest$MyTypeCode\"}";
        
    @Test 
    public void testJsonSerialize() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        MyTypeCodeContainer container = new MyTypeCodeContainer();
        container.setMyTypeCodeValue(MyTypeCode.CODE1);
        
        String json = mapper.writeValueAsString(container);
        assertEquals("{\"myTypeCodeValue\":" + EXPECTED_MyTypeCode_CODE1_JSON + "}" , json);
    }

    @Test 
    public void testJsonDeserialize() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        MyTypeCode myTypeCodeReadFromJson = mapper.readValue(EXPECTED_MyTypeCode_CODE1_JSON, MyTypeCode.class);
        assertEquals(MyTypeCode.CODE1, myTypeCodeReadFromJson);
    }
    
    @Test
    public void testSerialize() throws IOException {
        byte[] serialized = SerializationUtils.serialize(MyTypeCode.CODE1);
        Object deserialized = SerializationUtils.deserialize(serialized);
        assertEquals(MyTypeCode.CODE1, deserialized);
        assertSame(MyTypeCode.CODE1, deserialized);
    }
    
    @Test
    public void testNameMethod() throws IOException {
        assertEquals(MyTypeCode.CODE1.value(), MyTypeCode.CODE1.name());
        assertEquals("CODE1", MyTypeCode.CODE1.name());
    }
    
}
