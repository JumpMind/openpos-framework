package org.jumpmind.pos.core.ui.message;

import org.jumpmind.pos.core.ui.ActionItem;
import org.jumpmind.pos.core.ui.IconType;
import org.jumpmind.pos.core.ui.KeyConstants;
import org.jumpmind.pos.core.ui.messagepart.BaconStripPart;
import org.jumpmind.pos.core.ui.messagepart.StatusStripPart;

import java.util.ArrayList;
import java.util.List;

public class AddressUIMessage extends DynamicFormUIMessage {

    private static final long serialVersionUID = 1L;

    private BaconStripPart baconStrip = new BaconStripPart();
    private List<ActionItem> sausageLinks = new ArrayList<>();
    private StatusStripPart statusStrip = new StatusStripPart();

    public AddressUIMessage() {
        ActionItem submitButton = new ActionItem("Next", "Next", IconType.Forward);
        submitButton.setKeybind(KeyConstants.KEY_ENTER);
        setSubmitButton(submitButton);
    }

    public AddressUIMessage(String screenType) {
        this();
        setScreenType(screenType);
    }

    public void addDefaultAddressFields() {
        this.getForm().addTextField("streetAddress", "Street Address", "", true);
        this.getForm().addTextField("addressLine2", "Address Line 2", "", false);
        this.getForm().addTextField("locality", "City", "", true);
        this.getForm().addTextField("state", "State", "", true);
        this.getForm().addUSPostalCodeField("postalCode", "Postal Code", "", true);
        this.getForm().addTextField("country", "Country", "", true);
    }

    public void addDefaultAddressFields(String streetAddress, String addressLine2, String locality, String state, String postalCode, String country) {
        this.getForm().addTextField("streetAddress", "Street Address", streetAddress, true);
        this.getForm().addTextField("addressLine2", "Address Line 2", addressLine2, false);
        this.getForm().addTextField("locality", "City", locality, true);
        this.getForm().addTextField("state", "State", state, true);
        this.getForm().addUSPostalCodeField("postalCode", "Postal Code", postalCode, true);
        this.getForm().addTextField("country", "Country", country, true);
    }


    public void addAddressFieldsWithComboState(List<String> states) {
        this.getForm().addTextField("streetAddress", "Street Address", "", true);
        this.getForm().addTextField("addressLine2", "Address Line 2", "", false);
        this.getForm().addTextField("locality", "City", "", true);
        this.getForm().addComboBox("state", "State", states, true);
        this.getForm().addUSPostalCodeField("postalCode", "Postal Code", "", true);
        this.getForm().addTextField("country", "Country", "", true);
    }

    public void addAddressFieldsWithComboState(List<String> states, String streetAddress, String addressLine2, String locality, String state, String postalCode, String country) {
        this.getForm().addTextField("streetAddress", "Street Address", streetAddress, true);
        this.getForm().addTextField("addressLine2", "Address Line 2", addressLine2, false);
        this.getForm().addTextField("locality", "City", locality, true);
        this.getForm().addComboBox("state", "State", state, states, true);
        this.getForm().addUSPostalCodeField("postalCode", "Postal Code", postalCode, true);
        this.getForm().addTextField("country", "Country", country, true);
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
