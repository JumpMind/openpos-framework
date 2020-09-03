package org.jumpmind.pos.core.ui.data;

import lombok.*;
import org.jumpmind.pos.core.ui.ActionItem;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionSummary implements Serializable {

    private Long sequenceNumber;
    private String customerName;
    private Integer items;
    private String transactionDate;
    private String deviceId;
    private String storeId;
    private String tillId;
    private String barcode;
    private String total;
    private String icon;
    private String businessDate;
    private String status;
    private String username;
    private String transactionType;
    private List<ActionItem> actions;

    private Map<String, String> labels;

    public void addLabel(String name, String label) {
        if (labels == null) {
            labels = new HashMap<>();
        }
        labels.put(name, label);
    }
}
