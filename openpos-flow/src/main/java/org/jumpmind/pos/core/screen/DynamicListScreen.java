package org.jumpmind.pos.core.screen;


import java.util.ArrayList;
import java.util.List;

import org.jumpmind.pos.core.model.DynamicList;
import org.jumpmind.pos.core.template.SellTemplate;

public class DynamicListScreen extends Screen {

    private static final long serialVersionUID = 1L;

    private DynamicList list = new DynamicList();
    
    private ActionItem submitButton;
    
    private List<String> alternateSubmitActions = new ArrayList<String>();

    public DynamicListScreen() {
        setScreenType(ScreenType.DynamicList);
        setTemplate(new SellTemplate());
        submitButton = new ActionItem("Next", "Next", IconType.Forward);
    }

    public void setList(DynamicList list) {
        this.list = list;
    }

    public DynamicList getList() {
        return this.list;
    }
    
    public void addAlternateSubmitAction(String action) {
        this.alternateSubmitActions.add( action );
    }

    public List<String> getAlternateSubmitActions() {
        return alternateSubmitActions;
    }

    public void setAlternateSubmitActions(List<String> alternateActions) {
        this.alternateSubmitActions = alternateActions;
    }

	public ActionItem getSubmitButton() {
		return submitButton;
	}

	public void setSubmitButton(ActionItem submitButton) {
		this.submitButton = submitButton;
	}
}