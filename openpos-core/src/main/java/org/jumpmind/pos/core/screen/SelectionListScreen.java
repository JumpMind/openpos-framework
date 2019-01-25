package org.jumpmind.pos.core.screen;

import java.util.ArrayList;
import java.util.List;

public class SelectionListScreen extends Screen {

    private static final long serialVersionUID = 1L;

    private List<SelectionListItem> selectionList = new ArrayList<SelectionListItem>();

    private List<MenuItem> buttons = new ArrayList<>();
    
    private List<MenuItem> nonSelectionButtons = new ArrayList<>();

    private boolean multiSelect = false;
    
    private String instructions;

    public SelectionListScreen() {
        this.setScreenType(ScreenType.SelectionList);
    }

    public List<SelectionListItem> getSelectionList() {
        return selectionList;
    }
    
    @Override
    public Screen asDialog() {
        this.setScreenType(ScreenType.SelectionListDialog);
        return super.asDialog();
    }
    
    @Override
    public Screen asDialog(DialogProperties dialogProperties) {
        this.setScreenType(ScreenType.SelectionListDialog);
        return super.asDialog(dialogProperties);
    }

    public void setSelectionList(List<SelectionListItem> selectionList) {
        this.selectionList = selectionList;
    }

    public void addSelection(SelectionListItem selection) {
        selectionList.add(selection);
    }

    public void addSelection(String title, SelectionListItemDisplayProperty column) {
        SelectionListItem selection = new SelectionListItem(title, column);
        selectionList.add(selection);
    }

    public List<MenuItem> getButtons() {
        return buttons;
    }

    public void setButtons(List<MenuItem> buttons) {
        this.buttons = buttons;
    }

    public void addButton(MenuItem button) {
        this.buttons.add(button);
    }

    public List<MenuItem> getNonSelectionButtons() {
        return nonSelectionButtons;
    }

    public void setNonSelectionButtons(List<MenuItem> nonSelectionButtons) {
        this.nonSelectionButtons = nonSelectionButtons;
    }

    public void addNonSelectionButton(MenuItem button) {
        this.nonSelectionButtons.add(button);
    }
    
    public boolean isMultiSelect() {
        return multiSelect;
    }

    public void setMultiSelect(boolean multiSelect) {
        this.multiSelect = multiSelect;
    }
    
    public String getInstructions() {
        return instructions;
    }
    
    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

}
