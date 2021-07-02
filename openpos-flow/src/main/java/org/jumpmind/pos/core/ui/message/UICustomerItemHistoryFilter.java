package org.jumpmind.pos.core.ui.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UICustomerItemHistoryFilter implements Serializable {
    private String fromDate;
    private String toDate;
    private String text;

    private String fromDatePlaceholder;
    private String toDatePlaceholder;
    private String textPlaceholder;
}
