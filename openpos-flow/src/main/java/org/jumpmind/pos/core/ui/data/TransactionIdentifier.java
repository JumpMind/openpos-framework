package org.jumpmind.pos.core.ui.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionIdentifier implements Serializable {
    private Long sequenceNumber;
    private String deviceId;
    private String businessDate;

    private Long voidedSequenceNumber;
}
