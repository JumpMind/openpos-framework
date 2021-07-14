package org.jumpmind.pos.core.ui.data;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BillingAddress {
    private String nameOnCard;
    private String line1;
    private String line2;
    private String city;
    private String state;
    private String postalCode;
}
