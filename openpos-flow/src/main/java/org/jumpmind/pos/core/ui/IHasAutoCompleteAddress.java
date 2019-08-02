package org.jumpmind.pos.core.ui;

import org.jumpmind.pos.core.screen.IHasForm;

import java.util.List;

public interface IHasAutoCompleteAddress extends IHasForm {

    public void addDefaultAddressFields();

    public void addAddressFieldsWithComboState(List<String> states);

}
