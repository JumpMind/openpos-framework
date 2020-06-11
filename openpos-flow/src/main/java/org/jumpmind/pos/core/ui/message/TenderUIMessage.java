package org.jumpmind.pos.core.ui.message;

import org.jumpmind.pos.core.model.Form;
import org.jumpmind.pos.core.model.Tender;
import org.jumpmind.pos.core.model.Total;
import org.jumpmind.pos.core.ui.AssignKeyBindings;
import org.jumpmind.pos.core.ui.IHasForm;
import org.jumpmind.pos.core.ui.UIMessage;
import org.jumpmind.pos.core.ui.messagepart.OptionsListPart;

import java.util.ArrayList;
import java.util.List;

@AssignKeyBindings
public class TenderUIMessage extends UIMessage implements IHasForm {
    private static final long serialVersionUID = 1L;

    private Form form;
    private String title;
    private String prompt;
    private Total amountDue;
    private List<Tender> amounts;
    private OptionsListPart optionsList;
    private String imageUrl;

    public TenderUIMessage() {
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public Total getAmountDue() {
        return amountDue;
    }

    public void setAmountDue(Total amountDue) {
        this.amountDue = amountDue;
    }

    public List<Tender> getAmounts() {
        return amounts;
    }

    public void setAmounts(List<Tender> amounts) {
        this.amounts = amounts;
    }

    public void addAmount(Tender amount) {
        if (this.amounts == null) {
            this.amounts = new ArrayList<>();
        }
        this.amounts.add(amount);
    }

    public OptionsListPart getOptionsList() {
        return optionsList;
    }

    public void setOptionsList(OptionsListPart optionsList) {
        this.optionsList = optionsList;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

}
