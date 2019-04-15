package org.jumpmind.pos.core.screen;

import java.util.ArrayList;
import java.util.List;

import org.jumpmind.pos.core.model.FormField;
import org.jumpmind.pos.core.model.IFormElement;
import org.jumpmind.pos.core.ui.AssignKeyBindings;

@AssignKeyBindings
public class TenderingScreen extends Screen {
    private static final long serialVersionUID = 1L;

    private List<IItem> tenderItems = new ArrayList<>();
    private SelectionMode selectionMode = SelectionMode.Single;
    private String text;
    private FormField tenderAmount;
    
    private IFormElement balanceDueAmount;
    @Deprecated private String balanceDue;
    private IFormElement totalAmount;
    private List<ActionItem> itemActions = new ArrayList<>();
    private ActionItem actionButton;
    
    public TenderingScreen() {
        setScreenType(ScreenType.Tendering);
    }
    
    public List<IItem> getTenderItems() {
        return tenderItems;
    }
    public void setTenderItems(List<IItem> tenderItems) {
        this.tenderItems = tenderItems;
    }
    public void addTenderItem(IItem tenderItem) {
        this.tenderItems.add(tenderItem);
    }
    
    public SelectionMode getSelectionMode() {
        return selectionMode;
    }
    public void setSelectionMode(SelectionMode selectionMode) {
        this.selectionMode = selectionMode;
    }
    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }
    public FormField getTenderAmount() {
        return tenderAmount;
    }
    public void setTenderAmount(FormField tenderAmount) {
        this.tenderAmount = tenderAmount;
    }
    
    public IFormElement getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(IFormElement totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void setBalanceDueAmount(IFormElement balanceDueAmount) {
        this.balanceDueAmount = balanceDueAmount;
    }
    
    public IFormElement getBalanceDueAmount() {
        return this.balanceDueAmount;
    }
    
    @Deprecated
    public String getBalanceDue() {
        return balanceDue;
    }
    @Deprecated
    public void setBalanceDue(String balanceDue) {
        this.balanceDue = balanceDue;
    }
    public List<ActionItem> getItemActions() {
        return itemActions;
    }
    public void setItemActions(List<ActionItem> itemActions) {
        this.itemActions = itemActions;
    }
    public void addItemAction(ActionItem itemAction) {
        this.itemActions.add(itemAction);
    }
    
    public ActionItem getActionButton() {
        return actionButton;
    }

    public void setActionButton(ActionItem actionButton) {
        this.actionButton = actionButton;
    }
    
}
