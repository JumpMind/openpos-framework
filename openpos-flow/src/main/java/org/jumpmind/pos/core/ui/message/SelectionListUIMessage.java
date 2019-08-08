package org.jumpmind.pos.core.ui.message;

import java.util.ArrayList;
import java.util.List;

import org.jumpmind.pos.core.ui.ActionItem;
import org.jumpmind.pos.core.ui.data.SelectionListItem;
import org.jumpmind.pos.core.ui.AssignKeyBindings;
import org.jumpmind.pos.core.ui.UIMessage;

@AssignKeyBindings
public class SelectionListUIMessage extends UIMessage {

    private static final long serialVersionUID = -4859870631964238380L;

    private String instructions;
    private String noListItemsPlaceholderText;
    private String noListItemsPlaceholderIcon;
    private boolean showScan = false;
    private List<ActionItem> selectionButtons = new ArrayList<>();
    private List<ActionItem> nonSelectionButtons = new ArrayList<>();

    private List<SelectionListItem> selectionList = new ArrayList<SelectionListItem>();
    private boolean multiSelect = false;
    private boolean defaultSelect = false;
    private int defaultSelectItemIndex = 0;
    private int numberItemsPerPage;
    private int numberTotalItems;
    private String selectionChangedAction;
    private String fetchDataAction;

    
    public SelectionListUIMessage() {
        this.setScreenType(UIMessageType.SELECTION_LIST);
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public String getNoListItemsPlaceholderText() {
        return noListItemsPlaceholderText;
    }

    public void setNoListItemsPlaceholderText(String noListItemsPlaceholderText) {
        this.noListItemsPlaceholderText = noListItemsPlaceholderText;
    }

    public String getNoListItemsPlaceholderIcon() {
        return noListItemsPlaceholderIcon;
    }

    public void setNoListItemsPlaceholderIcon(String noListItemsPlaceholderIcon) {
        this.noListItemsPlaceholderIcon = noListItemsPlaceholderIcon;
    }

    public boolean isShowScan() {
        return showScan;
    }

    public void setShowScan(boolean showScan) {
        this.showScan = showScan;
    }

    public void setSelectionButtons(List<ActionItem> selectionButtons) {
        this.selectionButtons = selectionButtons;
    }

    public List<ActionItem> getSelectionButtons() {
        return selectionButtons;
    }

    public void addSelectionButton(ActionItem actionItem) {
        this.selectionButtons.add(actionItem);
    }

    public List<ActionItem> getNonSelectionButtons() {
        return nonSelectionButtons;
    }

    public void setNonSelectionButtons(List<ActionItem> nonSelectionButtons) {
        this.nonSelectionButtons = nonSelectionButtons;
    }

    public void addNonSelectionButton(ActionItem nonSelectionButton) {
        if(this.nonSelectionButtons == null) {
            this.nonSelectionButtons = new ArrayList<ActionItem>();
        }
        this.nonSelectionButtons.add(nonSelectionButton);
    }

    public List<SelectionListItem> getSelectionList() {
        return selectionList;
    }

    public void setSelectionList(List<SelectionListItem> selectionList) {
        this.selectionList = selectionList;
    }

    public boolean isMultiSelect() {
        return multiSelect;
    }

    public void setMultiSelect(boolean multiSelect) {
        this.multiSelect = multiSelect;
    }

    public boolean isDefaultSelect() {
        return defaultSelect;
    }

    public void setDefaultSelect(boolean defaultSelect) {
        this.defaultSelect = defaultSelect;
    }

    public int getDefaultSelectItemIndex() {
        return defaultSelectItemIndex;
    }

    public void setDefaultSelectItemIndex(int defaultSelectItemIndex) {
        this.defaultSelectItemIndex = defaultSelectItemIndex;
    }

    public int getNumberItemsPerPage() {
        return  numberItemsPerPage;
    }

    public void setNumberItemsPerPage(int numberItemsPerPage) {
        this.numberItemsPerPage = numberItemsPerPage;
    }

    public int getNumberTotalItems() {
        return numberTotalItems;
    }

    public void setNumberTotalItems(int numberTotalItems) {
        this.numberTotalItems = numberTotalItems;
    }

    public String getSelectionChangedAction() {
        return selectionChangedAction;
    }

    public void setSelectionChangedAction(String selectionChangedAction) {
        this.selectionChangedAction = selectionChangedAction;
    }

    public String getFetchDataAction() {
        return fetchDataAction;
    }

    public void setFetchDataAction(String fetchDataAction) {
        this.fetchDataAction = fetchDataAction;
    }
}