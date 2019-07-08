package org.jumpmind.pos.core.model;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;


public enum OpenposBarcodeType {
    AZTEK,
    CCA,
    CCB,
    CCC,
    CODABAR,
    CODABAR_ABC,
    CODABAR_CX,
    CODE11,
    CODE128,
    CODE25_I2OF5("CODE25_I20F5"),
    CODE25_NI2OF5("CODE25_NI20F5"),
    CODE39,
    CODE39_FULL,
    CODE93,
    CPCBINARY,
    DATAMATRIX,
    DUN14,
    EAN128,
    EAN13,
    EAN13_2,
    EAN13_5,
    EAN2,
    EAN5,
    EAN8,
    EAN8_2,
    EAN8_5,
    GS1DATABAR,
    IATA,
    INTELLIGENT_MAIL, 
    ITA_PHARMA,       
    ITF14,            
    KOREAN_POSTAL,    
    LATENT_IMAGE,     
    MATRIX_2OF5("MATRIX_20F5"),
    MAXICODE,
    MICROPDF417,
    MSI_PLESSEY,
    PDF417,
    PHARMACODE,
    PLANET,
    POSTBAR,
    QRCODE,
    RM4SCC,
    SCODE,
    TELEPEN,
    UK_PLESSEY,
    UPCA,
    UPCA_2,
    UPCA_5,
    UPCE,
    UPCE_2,
    UPCE_5,
    UPC_UNKNOWN;
    
    private List<String> variants = null;
    
    private static Map<String, OpenposBarcodeType> variantMap = new HashMap<String, OpenposBarcodeType>();
    static {
        for (OpenposBarcodeType barcode : values()) {
            if (barcode.variants != null) {
                barcode.variants.forEach(variant -> variantMap.put(variant, barcode));
            }
        }
    }
    
    private OpenposBarcodeType() {
    }
    
    private OpenposBarcodeType(String...variants) {
        if (variants != null) {
            this.variants = Arrays.asList(variants);
        }
    }

    @JsonCreator
    public static OpenposBarcodeType fromString(String barcodeTypeStr) {
        if (barcodeTypeStr != null && variantMap.containsKey(barcodeTypeStr)) {
            return variantMap.get(barcodeTypeStr);
        } else {
            return valueOf(barcodeTypeStr);
        }
    }
    
}
