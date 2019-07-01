package org.jumpmind.pos.core.model;

import static org.junit.Assert.*;

import org.jumpmind.pos.util.DefaultObjectMapper;
import org.junit.Before;
import org.junit.Test;

public class OpenposBarcodeTypeTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testJsonVariantDeserialize() {
        OpenposBarcodeType variantBarcode = DefaultObjectMapper.defaultObjectMapper().convertValue("CODE25_I20F5", OpenposBarcodeType.class);
        assertEquals(OpenposBarcodeType.CODE25_I2OF5, variantBarcode);
    }

    @Test
    public void testNoVariantDeserialize() {
        OpenposBarcodeType barcode = DefaultObjectMapper.defaultObjectMapper().convertValue("CODE39", OpenposBarcodeType.class);
        assertEquals(OpenposBarcodeType.CODE39, barcode);
    }
    
}
