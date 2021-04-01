package org.jumpmind.pos.core.ui.message;

import org.jumpmind.pos.core.ui.IconType;
import org.jumpmind.pos.core.ui.KeyConstants;
import org.jumpmind.pos.core.ui.ActionItem;
import org.jumpmind.pos.core.ui.IHasAutoCompleteAddress;
import org.jumpmind.pos.core.ui.messagepart.BaconStripPart;
import org.jumpmind.pos.core.ui.messagepart.StatusStripPart;

import java.util.ArrayList;
import java.util.List;


public class AutoCompleteAddressUIMessage extends AddressUIMessage implements IHasAutoCompleteAddress {

    public AutoCompleteAddressUIMessage() {
        super(UIMessageType.AUTO_COMPLETE_ADDRESS);
    }
}
