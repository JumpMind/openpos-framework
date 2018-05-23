package org.jumpmind.pos.translate;

import java.util.List;

public interface ILegacyTenderBeanModel {

    List<ILegacyTenderLineItem> getTenderLineItems();

    ILegacyCurrency getBalanceDue();

    ILegacyNavigationButtonBeanModel getGlobalButtonBeanModel();
}
