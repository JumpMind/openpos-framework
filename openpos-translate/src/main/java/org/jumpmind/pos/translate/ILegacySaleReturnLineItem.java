package org.jumpmind.pos.translate;

import java.math.BigDecimal;
import java.util.List;


public interface ILegacySaleReturnLineItem {
    String getPosItemID();
    int getLineNumber();
    String getItemID();
    String getItemDescription();
    BigDecimal getItemQuantityDecimal();
    ILegacyCurrency getExtendedDiscountedSellingPrice();
    String getItemSizeCode();
    List<String> getUPCList();
    BigDecimal getQuantityReturnedDecimal();
    boolean isOrderLineItem();
    boolean isItemPickedUp();
    boolean isFromTransaction();
    boolean isGiftReceiptItem();
    String getTruncatedGiftCardNumber();
    String getItemSerial();
    boolean getItemSendFlag();
    int getSendLabelCount();
    String getRegistryID();
    boolean getSalesAssociateModifiedFlag();
    String getSalesAssociateFirstName();
    String getReceiptDescription();
    ILegacyCurrency getSellingPrice();
    String getTaxStatusDescriptor();
    ILegacyCurrency getItemDiscountTotal();
}
