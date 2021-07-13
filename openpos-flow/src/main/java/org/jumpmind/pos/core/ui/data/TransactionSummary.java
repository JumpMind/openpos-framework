package org.jumpmind.pos.core.ui.data;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.jumpmind.pos.core.ui.ActionItem;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionSummary extends TransactionIdentifier {
    private String sequenceNumberFormatted;
    private String voidedSequenceNumberFormatted;
    private String customerName;
    private Integer items;
    private String itemsFormatted;
    private Date endTime;
    private String transactionDate;
    private String storeId;
    private String tillId;
    private String barcode;
    private String total;
    private String status;
    private String statusText;
    private String statusIcon;
    private boolean voidable;
    private String username;
    private String transactionType;
    private String transactionTypeText;
    private String transactionTypeIcon;
    private List<String> tenderTypeIcons;
    private List<ActionItem> actions;
    private String loyaltyNumber;

    private Map<String, String> labels;

    public void addLabel(String name, String label) {
        if (labels == null) {
            labels = new HashMap<>();
        }
        labels.put(name, label);
    }
}
