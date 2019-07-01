package org.jumpmind.pos.core.ui.message;

import org.jumpmind.pos.core.model.DisplayProperty;
import org.jumpmind.pos.core.screen.ActionItem;
import org.jumpmind.pos.core.ui.UIMessage;

import java.util.ArrayList;
import java.util.List;

public class PriceCheckerItemDetailsUIMessage extends UIMessage {

    private ActionItem printButton;
    private String itemDescription;
    private List<DisplayProperty> itemProperties;
    private DisplayProperty pricePerUnit;
    private DisplayProperty currentPrice;
    private List<DisplayProperty> associatedItems;
    private String logoUrl;
    private List<String> itemMessages;
    private String disclaimer;
    private String itemNotFoundMessage;
    private String helpMessage;
    private String scanActionName;

    public PriceCheckerItemDetailsUIMessage() {
        setScreenType(UIMessageType.PRICE_CHECKER_ITEM_DETAIL);
    }

    public String getItemNotFoundMessage() {
        return itemNotFoundMessage;
    }

    public void setItemNotFoundMessage(String itemNotFoundMessage) {
        this.itemNotFoundMessage = itemNotFoundMessage;
    }

    public String getHelpMessage() {
        return helpMessage;
    }

    public void setHelpMessage(String helpMessage) {
        this.helpMessage = helpMessage;
    }

    public ActionItem getPrintButton() {
        return printButton;
    }

    public void setPrintButton(ActionItem printButton) {
        this.printButton = printButton;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    public List<DisplayProperty> getItemProperties() {
        return itemProperties;
    }

    public void setItemProperties(List<DisplayProperty> itemProperties) {
        this.itemProperties = itemProperties;
    }

    public void addItemProperty(String label, String value, String formatter) {
        if( itemProperties == null ) {
            itemProperties = new ArrayList<>();
        }
        this.itemProperties.add(new DisplayProperty(label, value, formatter));
    }

    public DisplayProperty getPricePerUnit() {
        return pricePerUnit;
    }

    public void setPricePerUnit(DisplayProperty pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
    }

    public DisplayProperty getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(DisplayProperty currentPrice) {
        this.currentPrice = currentPrice;
    }

    public List<DisplayProperty> getAssociatedItems() {
        return associatedItems;
    }

    public void setAssociatedItems(List<DisplayProperty> associatedItems) {
        this.associatedItems = associatedItems;
    }

    public void addAssociatedItem(String label, String value, String formatter) {
        if( associatedItems == null ) {
            associatedItems = new ArrayList<>();
        }
        associatedItems.add(new DisplayProperty(label, value, formatter));
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public List<String> getItemMessages() {
        return itemMessages;
    }

    public void setItemMessages(List<String> itemMessages) {
        this.itemMessages = itemMessages;
    }

    public void addItemMessage(String message) {
        if( itemMessages == null ) {
            itemMessages = new ArrayList<>();
        }
        itemMessages.add(message);
    }

    public String getDisclaimer() {
        return disclaimer;
    }

    public void setDisclaimer(String disclaimer) {
        this.disclaimer = disclaimer;
    }

    public String getScanActionName() {
        return scanActionName;
    }

    public void setScanActionName(String scanActionName) {
        this.scanActionName = scanActionName;
    }
}
