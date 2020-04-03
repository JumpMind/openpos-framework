package org.jumpmind.pos.persist.cars;

import org.jumpmind.pos.util.model.AbstractTypeCode;

public class SubModelCode extends AbstractTypeCode {
    private static final long serialVersionUID = 1L;

    public static final SubModelCode DELUXE = new SubModelCode("DELUXE");
    public static final SubModelCode ECONOLINE = new SubModelCode("ECONOLINE");
    public static final SubModelCode HD = new SubModelCode("HD");

    public static SubModelCode of(String value) {
        return AbstractTypeCode.of(SubModelCode.class, value);
    }

    private SubModelCode(String value) {
        super(value);
    }
}
