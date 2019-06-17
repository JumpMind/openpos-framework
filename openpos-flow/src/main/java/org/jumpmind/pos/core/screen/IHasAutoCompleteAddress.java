package org.jumpmind.pos.core.screen;

import java.util.List;

public interface IHasAutoCompleteAddress extends IHasForm {

    public void addDefaultAddressFields();

    public void addAddressFieldsWithComboState(List<String> states);

}
