package org.jumpmind.pos.core.ui.message;

import org.jumpmind.pos.core.ui.IHasAutoCompleteAddress;

import java.util.List;

public class CustomerDisplayAddressUIMessage extends CustomerDisplayFormUIMessage implements IHasAutoCompleteAddress {

    private static final long serialVersionUID = 1L;

    private String streetAddressPlaceholder = "Street Address";
    private String addressLine2Placeholder = "Address Line 2";
    private String localityPlaceholder = "City";
    private String statePlaceholder = "State";
    private String postalCodePlaceholder = "Postal Code";
    private String countryPlaceholder = "Country";

    private boolean autoComplete = true;

    public CustomerDisplayAddressUIMessage() {
        super();
    }

    public void addDefaultAddressFields() {
        this.getForm().addTextField("streetAddress", streetAddressPlaceholder, "", true);
        this.getForm().addTextField("addressLine2", addressLine2Placeholder, "", false);
        this.getForm().addTextField("locality", localityPlaceholder, "", true);
        this.getForm().addTextField("state", statePlaceholder, "", true);
        this.getForm().addTextField("postalCode", postalCodePlaceholder, "", true);
        this.getForm().addTextField("country", countryPlaceholder, "", true);
    }

    public void addAddressFieldsWithComboState(List<String> states) {
        this.getForm().addTextField("streetAddress", streetAddressPlaceholder, "", true);
        this.getForm().addTextField("addressLine2", addressLine2Placeholder, "", false);
        this.getForm().addTextField("locality", localityPlaceholder, "", true);
        this.getForm().addComboBox("state", statePlaceholder, states, true);
        this.getForm().addTextField("postalCode", postalCodePlaceholder, "", true);
        this.getForm().addTextField("country", countryPlaceholder, "", true);
    }

    public void setPlaceholders(String streetAddressPlaceholder, String addressLine2Placeholder, String localityPlaceholder,
            String statePlaceholder, String postalCodePlaceholder, String countryPlaceholder) {
        this.streetAddressPlaceholder = streetAddressPlaceholder;
        this.addressLine2Placeholder = addressLine2Placeholder;
        this.localityPlaceholder = localityPlaceholder;
        this.statePlaceholder = statePlaceholder;
        this.postalCodePlaceholder = postalCodePlaceholder;
        this.countryPlaceholder = countryPlaceholder;
    }

    public boolean isAutoComplete() {
        return autoComplete;
    }

    public void setAutoComplete(boolean autoComplete) {
        this.autoComplete = autoComplete;
    }

}
