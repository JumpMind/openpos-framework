package org.jumpmind.pos.util.model.typecode.replace;

import org.jumpmind.pos.util.model.AbstractTypeCode;

public class ReplacementTypeCode extends AbstractTypeCode {
    public static final ReplacementTypeCode VALUE_1 = new ReplacementTypeCode("VALUE_1");
    public static final ReplacementTypeCode VALUE_2 = new ReplacementTypeCode("VALUE_2");

    private static final String[] DESERIALIZE_SEARCH_CLASSES = {
            "org.jumpmind.pos.util.model.typecode.old.OriginalTypeCode",
            ReplacementTypeCode.class.getName()
    };

    public static ReplacementTypeCode of(String value) {
        return AbstractTypeCode.of(ReplacementTypeCode.class, value);
    }

    private ReplacementTypeCode(String value) {
        super(value);
    }

    @Override
    public String[] getDeserializationSearchClasses() {
        return DESERIALIZE_SEARCH_CLASSES;
    }

}
