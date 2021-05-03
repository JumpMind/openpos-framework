package org.jumpmind.pos.core.ui.data;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class TenderItem implements Serializable {
    private String typeName;
    private String amount;
    private String icon;
    private String text;
    private String cardLastFourDigits;
}
