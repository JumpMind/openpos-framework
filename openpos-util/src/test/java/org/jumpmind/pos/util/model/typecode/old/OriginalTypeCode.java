package org.jumpmind.pos.util.model.typecode.old;

import org.jumpmind.pos.util.model.AbstractTypeCode;

public class OriginalTypeCode extends AbstractTypeCode {
    public static final OriginalTypeCode VALUE_1 = new OriginalTypeCode("VALUE_1");
    public static final OriginalTypeCode VALUE_2 = new OriginalTypeCode("VALUE_2");

    public static OriginalTypeCode of(String value) {
        return AbstractTypeCode.of(OriginalTypeCode.class, value);
    }

    private OriginalTypeCode(String value) {
        super(value);
    }

}
