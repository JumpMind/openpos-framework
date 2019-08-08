package org.jumpmind.pos.translate;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jumpmind.pos.core.model.DisplayProperty;
import org.jumpmind.pos.core.model.Form;
import org.jumpmind.pos.core.model.FormDisplayField;
import org.jumpmind.pos.core.ui.ActionItem;
import org.jumpmind.pos.core.ui.data.SellItem;
import org.jumpmind.pos.core.ui.message.ItemDetailUIMessage;
import org.jumpmind.pos.core.ui.messagepart.BaconStripPart;
import org.jumpmind.pos.core.ui.messagepart.MessagePartConstants;
import org.jumpmind.pos.server.model.Action;

public class SellItemDetailsUIMessageTranslator extends AbstractLegacyUIMessageTranslator<ItemDetailUIMessage> {

    public SellItemDetailsUIMessageTranslator(ILegacyScreen legacyScreen, Class<ItemDetailUIMessage> screenClass) {
        super(legacyScreen, screenClass);
    }

    @Override
    public void buildMainContent() {       
        super.buildMainContent();
        
        ILegacyPOSListModel legacyPOSListModel = this.getLegacyPOSBeanService().getLegacyPOSListModel(this.getLegacyScreen());

        BaconStripPart baconStripPart = new BaconStripPart();
        List<ActionItem> sausageLinksPart = new ArrayList<>();
        screen.addMessagePart(MessagePartConstants.BaconStrip, baconStripPart);
        screen.addMessagePart(MessagePartConstants.SausageLinks, sausageLinksPart);

        baconStripPart.setHeaderText(getScreenName());
        baconStripPart.setOperatorText(getOperatorText());
        baconStripPart.setBackButton(getBackButton());
        baconStripPart.setDeviceId(getDeviceId());
        
        if (!legacyPOSListModel.isEmpty()) {
            ILegacySaleReturnLineItem saleItem = this.legacyPOSBeanService.toILegacyInstance(legacyPOSListModel.firstElement());
            
            SellItem item = new SellItem();
            
            item.setPosItemId(saleItem.getPosItemID());
            item.setIndex(saleItem.getLineNumber());
            item.setID(saleItem.getItemID());
            item.setDescription(saleItem.getItemDescription());
            item.setQuantity(saleItem.getItemQuantityDecimal().toPlainString());
            item.setAmount(saleItem.getExtendedDiscountedSellingPrice().toFormattedString());
            
            List<FormDisplayField> fields = new ArrayList<>();
            
            fields.add( new FormDisplayField("Item Number", "itemNumber", item.getPosItemId()));
            fields.add( new FormDisplayField("UPC Number", "upcNumber", formatUPCNumbers(saleItem.getUPCList())));
            fields.add( new FormDisplayField("Description", "description", item.getDescription()));
            
            fields.add( new FormDisplayField("Current Price", "currentPrice", item.getAmount()));
            fields.add( new FormDisplayField("Size", "size", saleItem.getItemSizeCode()));
           
            item.setFields(fields);
            
            this.screen.setItemName(String.format("%s - $%s", item.getDescription(), item.getAmount()));
            this.screen.addItemProperty( new DisplayProperty("Item Number", item.getPosItemId()));
            this.screen.addItemProperty( new DisplayProperty("UPC Number", formatUPCNumbers(saleItem.getUPCList())));
            this.screen.addItemProperty( new DisplayProperty( "Description", item.getDescription()));
            this.screen.addItemProperty( new DisplayProperty( "Current Price", item.getAmount()));
            this.screen.addItemProperty( new DisplayProperty( "Size", saleItem.getItemSizeCode()));
        }

        sausageLinksPart.add(new ActionItem("Remove Item", "Remove", false));
    }

    @Override
    public void handleAction(ITranslationManagerSubscriber subscriber, TranslationManagerServer tmServer, Action action, Form formResults)  {
        if (action.getName().equals("Remove")) {
            ILegacyPOSListModel legacyPOSListModel = this.getLegacyPOSBeanService().getLegacyPOSListModel(this.getLegacyScreen());

            if (!legacyPOSListModel.isEmpty()) {
                ILegacySaleReturnLineItem saleItem = this.legacyPOSBeanService.toILegacyInstance(legacyPOSListModel.firstElement());
                
                tmServer.executeMacro(new InteractionMacro().sendLetter("Undo").waitForScreen("SELL_ITEM").doOnScreen((s) -> {
                    ILegacySellItemUtils legacySellItemUtils = this.getLegacyPOSBeanService().getLegacySellItemUtils();
                    legacySellItemUtils.setSelectedSellItem(s, saleItem.getLineNumber());
                }).sendLetter("Clear"));
            }
        } else {
            super.handleAction(subscriber, tmServer, action, formResults);
        }
    }
    
    protected String formatUPCNumbers(List<String> upcList) {
        StringBuilder upcFormatter = new StringBuilder();
        if (upcList != null) {
            if (upcList.size() <= 5) {
                upcFormatter.append(StringUtils.join(upcList, ", "));
            } else {
                upcFormatter.append(StringUtils.join(upcList.subList(0, 5), ", ")).append(", +more");
            }
        }

        return upcFormatter.toString();
    }
    
}
