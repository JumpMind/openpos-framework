package org.jumpmind.pos.core.screen;

import java.util.ArrayList;
import java.util.List;

import org.jumpmind.pos.core.screenpart.BaconStripPart;
import org.jumpmind.pos.core.screenpart.StatusStripPart;

public class AutoCompleteAddressScreen extends DynamicFormScreen implements IHasAutoCompleteAddress {

    private static final long serialVersionUID = 1L;

    private BaconStripPart baconStrip = new BaconStripPart();
    private List<ActionItem> sausageLinks = new ArrayList<>();
    private StatusStripPart statusStrip = new StatusStripPart();

    public AutoCompleteAddressScreen() {
        setScreenType(ScreenType.AutoCompleteAddress);
        setTemplate(null);
        ActionItem submitButton = new ActionItem("Next", "Next", IconType.Forward);
        submitButton.setKeybind(KeyConstants.KEY_ENTER);
        setSubmitButton(submitButton);
    }

    public void addDefaultAddressFields() {
        this.getForm().addTextField("streetAddress", "Street Address", "", true);
        this.getForm().addTextField("addressLine2", "Address Line 2", "", false);
        this.getForm().addTextField("locality", "City", "", true);
        this.getForm().addTextField("state", "State", "", true);
        this.getForm().addTextField("postalCode", "Postal Code", "", true);
        this.getForm().addTextField("country", "Country", "", true);
    }

    public void addAddressFieldsWithComboState(List<String> states) {
        this.getForm().addTextField("streetAddress", "Street Address", "", true);
        this.getForm().addTextField("addressLine2", "Address Line 2", "", false);
        this.getForm().addTextField("locality", "City", "", true);
        this.getForm().addSearchablePopTart("state", "State", states, true);
        this.getForm().addTextField("postalCode", "Postal Code", "", true);
        this.getForm().addTextField("country", "Country", "", true);
    }

    public BaconStripPart getBaconStrip() {
        return baconStrip;
    }

    public void setBaconStrip(BaconStripPart baconStrip) {
        this.baconStrip = baconStrip;
    }

    public List<ActionItem> getSausageLinks() {
        return sausageLinks;
    }

    public void setSausageLinks(List<ActionItem> sausageLinks) {
        this.sausageLinks = sausageLinks;
    }

    public StatusStripPart getStatusStrip() {
        return statusStrip;
    }

    public void setStatusStrip(StatusStripPart statusStrip) {
        this.statusStrip = statusStrip;
    }

}
