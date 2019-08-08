package org.jumpmind.pos.core.ui.message;

import org.jumpmind.pos.core.model.DisplayProperty;
import org.jumpmind.pos.core.model.Form;
import org.jumpmind.pos.core.ui.IHasForm;
import org.jumpmind.pos.core.ui.ActionItem;
import org.jumpmind.pos.core.ui.UIMessage;
import org.jumpmind.pos.core.ui.data.TenderItem;

import java.util.ArrayList;
import java.util.List;


public class TenderUIMessage extends UIMessage implements IHasForm {
    private String instructions;
    private Form form;
    private DisplayProperty balanceDue;
    private List<String> tenderTypeActionNames;
    private String completedTenderListLabel;
    private List<TenderItem> tenderItems;
    private List<ActionItem> tenderItemActions;
    private String noCompletedTendersMessage;
    private String noCompletedTendersIcon;

    public TenderUIMessage(){
        setScreenType(UIMessageType.TENDER);
    }

    @Override
    public void setForm(Form form) {
        this.form = form;
    }

    @Override
    public Form getForm() {
        return form;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public DisplayProperty getBalanceDue() {
        return balanceDue;
    }

    public void setBalanceDue(DisplayProperty balanceDue) {
        this.balanceDue = balanceDue;
    }

    public String getCompletedTenderListLabel() {
        return completedTenderListLabel;
    }

    public void setCompletedTenderListLabel(String completedTenderListLabel) {
        this.completedTenderListLabel = completedTenderListLabel;
    }

    public List<TenderItem> getTenderItems() {
        return tenderItems;
    }

    public void setTenderItems(List<TenderItem> tenderItems) {
        this.tenderItems = tenderItems;
    }

    public void addTenderItem(String typeName, String amount) {
        if( tenderItems == null ) {
            tenderItems = new ArrayList<>();
        }

        tenderItems.add(new TenderItem(typeName, amount));
    }

    public List<ActionItem> getTenderItemActions() {
        return tenderItemActions;
    }

    public void setTenderItemActions(List<ActionItem> tenderItemActions) {
        this.tenderItemActions = tenderItemActions;
    }

    public void addTenderItemAction(ActionItem tenderItemAction) {
        if( tenderItemActions == null ){
            tenderItemActions = new ArrayList<>();
        }

        tenderItemActions.add(tenderItemAction);
    }

    public String getNoCompletedTendersMessage() {
        return noCompletedTendersMessage;
    }

    public void setNoCompletedTendersMessage(String noCompletedTendersMessage) {
        this.noCompletedTendersMessage = noCompletedTendersMessage;
    }

    public List<String> getTenderTypeActionNames() {
        return tenderTypeActionNames;
    }

    public void setTenderTypeActionNames(List<String> tenderTypeActionNames) {
        this.tenderTypeActionNames = tenderTypeActionNames;
    }

    public void addTenderTypeActionName(String tenderTypeActionName) {
        if(this.tenderTypeActionNames == null){
            this.tenderTypeActionNames = new ArrayList<>();
        }

        this.tenderTypeActionNames.add(tenderTypeActionName);
    }

    public String getNoCompletedTendersIcon() {
        return noCompletedTendersIcon;
    }

    public void setNoCompletedTendersIcon(String noCompletedTendersIcon) {
        this.noCompletedTendersIcon = noCompletedTendersIcon;
    }
}
