package org.jumpmind.pos.translate;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jumpmind.pos.core.flow.Action;
import org.jumpmind.pos.core.model.Form;
import org.jumpmind.pos.core.model.FormDisplayField;
import org.jumpmind.pos.core.screen.MenuItem;
import org.jumpmind.pos.core.screen.SellItem;
import org.jumpmind.pos.core.screen.SellItemDetailScreen;
import org.jumpmind.pos.core.template.SellTemplate;

public class SellItemDetailsScreenTranslator extends AbstractLegacyScreenTranslator<SellItemDetailScreen> {

    public SellItemDetailsScreenTranslator(ILegacyScreen legacyScreen, Class<SellItemDetailScreen> screenClass) {
        super(legacyScreen, screenClass);
    }

    @Override
    public void buildMainContent() {       
        super.buildMainContent();
        
        ILegacyPOSListModel legacyPOSListModel = this.getLegacyPOSBeanService().getLegacyPOSListModel(this.getLegacyScreen());
       
        screen.setTemplate(new SellTemplate());
        
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
            
            this.screen.setPrompt(String.format("%s - $%s", item.getDescription(), item.getAmount()));
            this.screen.setItem(item);
        }

        ILegacyCargo legacyCargo = getBus().getLegacyCargo();
        if (legacyCargo != null && legacyCargo.getRetailTransaction() != null) {
            ILegacyRetailTransaction retailTransaction = legacyCargo.getRetailTransaction();
            if (retailTransaction != null) {
                this.screen.getTransaction().setTransactionNumber(retailTransaction.getFormattedTransactionSequenceNumber());
            }
        }
        
        buildLocalMenuItems();
    }

    protected void buildLocalMenuItems() {        
        addLocalMenuItem(new MenuItem("Remove Item", "Remove", false));
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
