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
        this.getForm().addTextField("attention", "key:customerdisplay:customer.form.field.attention", "", true);
        this.getForm().addTextField("streetAddress", "key:customerdisplay:customer.form.field.addressLine1", "", true);
        this.getForm().addTextField("addressLine2", "key:customerdisplay:customer.form.field.addressLine2", "", false);
        this.getForm().addTextField("locality", "key:customerdisplay:customer.form.field.city", "", true);
        this.getForm().addTextField("state", "key:customerdisplay:customer.form.field.state", "", true);
        this.getForm().addPostalCodeField("postalCode", "key:customerdisplay:customer.form.field.postalCode", "", true);
        this.getForm().addTextField("country", "key:customerdisplay:customer.form.field.country", "", true);
    }

    public void addDefaultAddressFields(String attention, String streetAddress, String addressLine2, String locality, String state, String postalCode, String country) {
        this.getForm().addTextField("attention", "key:customerdisplay:customer.form.field.attention", attention, true);
        this.getForm().addTextField("streetAddress", "key:customerdisplay:customer.form.field.addressLine1", streetAddress, true);
        this.getForm().addTextField("addressLine2", "key:customerdisplay:customer.form.field.addressLine2", addressLine2, false);
        this.getForm().addTextField("locality", "key:customerdisplay:customer.form.field.city", locality, true);
        this.getForm().addTextField("state", "key:customerdisplay:customer.form.field.state", state, true);
        this.getForm().addPostalCodeField("postalCode", "key:customerdisplay:customer.form.field.postalCode", postalCode, true);
        this.getForm().addTextField("country", "key:customerdisplay:customer.form.field.country", country, true);
    }


    public void addAddressFieldsWithComboState(List<String> states) {
        this.getForm().addTextField("attention", "key:customerdisplay:customer.form.field.attention", "", true);
        this.getForm().addTextField("streetAddress", "key:customerdisplay:customer.form.field.addressLine1", "", true);
        this.getForm().addTextField("addressLine2", "key:customerdisplay:customer.form.field.addressLine2", "", false);
        this.getForm().addTextField("locality", "key:customerdisplay:customer.form.field.city", "", true);
        this.getForm().addComboBox("state", "key:customerdisplay:customer.form.field.state", states, true);
        this.getForm().addPostalCodeField("postalCode", "key:customerdisplay:customer.form.field.postalCode", "", true);
        this.getForm().addTextField("country", "key:customerdisplay:customer.form.field.country", "", true);
    }

    public void addAddressFieldsWithComboState(List<String> states, String attention, String streetAddress, String addressLine2, String locality, String state, String postalCode, String country) {
        this.getForm().addTextField("attention", "key:customerdisplay:customer.form.field.attention", attention, true);
        this.getForm().addTextField("streetAddress", "key:customerdisplay:customer.form.field.addressLine1", streetAddress, true);
        this.getForm().addTextField("addressLine2", "key:customerdisplay:customer.form.field.addressLine2", addressLine2, false);
        this.getForm().addTextField("locality", "key:customerdisplay:customer.form.field.city", locality, true);
        this.getForm().addComboBox("state", "key:customerdisplay:customer.form.field.state", state, states, true);
        this.getForm().addPostalCodeField("postalCode", "key:customerdisplay:customer.form.field.postalCode", postalCode, true);
        this.getForm().addTextField("country", "key:customerdisplay:customer.form.field.country", country, true);
    }


    public void addAddressFieldsWithComboState(List<String> states, int streetAddressCharacterLimit, int addressLine2CharacterLimit, int cityCharacterLimit) {
        this.getForm().addTextField("attention", "key:customerdisplay:customer.form.field.attention", "", true);
        this.getForm().addTextField("streetAddress", "key:customerdisplay:customer.form.field.addressLine1", "", true).setMaxLength(streetAddressCharacterLimit);
        this.getForm().addTextField("addressLine2", "key:customerdisplay:customer.form.field.addressLine2", "", false).setMaxLength(addressLine2CharacterLimit);
        this.getForm().addTextField("locality", "key:customerdisplay:customer.form.field.city", "", true).setMaxLength(cityCharacterLimit);
        this.getForm().addComboBox("state", "key:customerdisplay:customer.form.field.state", states, true);
        this.getForm().addPostalCodeField("postalCode", "key:customerdisplay:customer.form.field.postalCode", "", true);
        this.getForm().addTextField("country", "key:customerdisplay:customer.form.field.country", "", true);
    }

    public void addAddressFieldsWithComboState(List<String> states, String attention, String streetAddress, String addressLine2, String locality, String state, String postalCode, String country, int streetAddressCharacterLimit, int addressLine2CharacterLimit, int cityCharacterLimit) {
        this.getForm().addTextField("attention", "key:customerdisplay:customer.form.field.attention", attention, true);
        this.getForm().addTextField("streetAddress", "key:customerdisplay:customer.form.field.addressLine1", streetAddress, true).setMaxLength(streetAddressCharacterLimit);
        this.getForm().addTextField("addressLine2", "key:customerdisplay:customer.form.field.addressLine2", addressLine2, false).setMaxLength(addressLine2CharacterLimit);
        this.getForm().addTextField("locality", "key:customerdisplay:customer.form.field.city", locality, true).setMaxLength(cityCharacterLimit);
        this.getForm().addComboBox("state", "key:customerdisplay:customer.form.field.state", state, states, true);
        this.getForm().addPostalCodeField("postalCode", "key:customerdisplay:customer.form.field.postalCode", postalCode, true);
        this.getForm().addTextField("country", "key:customerdisplay:customer.form.field.country", country, true);
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
