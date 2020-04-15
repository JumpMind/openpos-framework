package org.jumpmind.pos.util.model.typecode.replace;

import org.jumpmind.pos.util.model.AbstractTypeCode;

public class ReplacementWithNonExistingOriginalTypeCode extends AbstractTypeCode {
    public static final ReplacementWithNonExistingOriginalTypeCode VALUE_1 = new ReplacementWithNonExistingOriginalTypeCode("VALUE_1");
    public static final ReplacementWithNonExistingOriginalTypeCode VALUE_2 = new ReplacementWithNonExistingOriginalTypeCode("VALUE_2");

    private static final String[] DESERIALIZE_SEARCH_CLASSES = {
            "org.jumpmind.pos.util.model.typecode.old.NonExistentOriginalTypeCode",
            ReplacementWithNonExistingOriginalTypeCode.class.getName()
    };

    public static ReplacementWithNonExistingOriginalTypeCode of(String value) {
        return AbstractTypeCode.of(ReplacementWithNonExistingOriginalTypeCode.class, value);
    }

    private ReplacementWithNonExistingOriginalTypeCode(String value) {
        super(value);
    }

    @Override
    public String[] getDeserializationSearchClasses() {
        return DESERIALIZE_SEARCH_CLASSES;
    }

}
