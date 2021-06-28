package org.jumpmind.pos.core.ui.message;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class UICustomerSubscriptionsItem implements Serializable {
    private String name;
    private List<UIMembership> memberships = new ArrayList<>();
}
