package org.jumpmind.pos.translate;

import java.math.BigDecimal;

public interface ILegacyPOSBeanService {

    ILegacyAssignmentSpec getLegacyAssignmentSpec(ILegacyScreen legacyScreen, String panelKey);
    ILegacyBeanSpec getLegacyBeanSpec(ILegacyScreen legacyScreen, String beanSpecName);
    ILegacyRegisterStatusService getLegacyRegisterStatusService(ILegacyScreen legacyScreen);
    ILegacyUtilityManager getLegacyUtilityManager(ILegacyScreen legacyScreen);
    ILegacyUIModel getLegacyUIModel(ILegacyScreen legacyScreen);
    ILegacyBus getLegacyBus(ILegacyScreen legacyScreen);
    ILegacyPromptAndResponseModel getLegacyPromptAndResponseModel(ILegacyScreen legacyScreen);
    ILegacyPOSBaseBeanModel getLegacyPOSBaseBeanModel(ILegacyScreen legacyScreen);
    ILegacyResourceBundleUtil getLegacyResourceBundleUtil();
    ILegacyLocaleUtilities getLegacyLocaleUtilities();
    ILegacyUIUtilities getLegacyUIUtilities();
    ILegacySummaryTenderMenuBeanModel getLegacySummaryTenderMenuBeanModel(ILegacyScreen legacyScreen);
    ILegacyStatusBeanModel getLegacyStatusBeanModel(ILegacyScreen legacyScreen);
    ILegacyCurrencyDetailBeanModel getLegacyCurrencyDetailBeanModel(ILegacyScreen legacyScreen);
    ILegacyOtherTenderDetailBeanModel getLegacyOtherTenderDetailBeanModel(ILegacyScreen legacyScreen);
    ILegacyCheckEntryBeanModel getLegacyCheckEntryBeanModel(ILegacyScreen legacyScreen);
    ILegacyDialogBeanModel getLegacyDialogBeanModel(ILegacyScreen legacyScreen);
    ILegacyGiftCardBeanModel getLegacyGiftCardBeanModel(ILegacyScreen legacyScreen);
    ILegacyGiftPriceBeanModel getLegacyGiftPriceBeanModel(ILegacyScreen legacyScreen);
    ILegacyTenderBeanModel getLegacyTenderBeanModel(ILegacyScreen legacyScreen);
    ILegacyReasonBeanModel getLegacyReasonBeanModel(ILegacyScreen legacyScreen);
    ILegacyBooleanWithReasonBeanModel getLegacyBooleanWithReasonBeanModel(ILegacyScreen legacyScreen);
    ILegacyParameterManager getLegacyParameterManager(ILegacyScreen legacyScreen);
    
    <T> T toILegacyInstance(Object legacyObject);
    
    ILegacyPOSListModel getLegacyPOSListModel(ILegacyScreen legacyScreen);
    ILegacyListBeanModel getLegacyListBeanModel(ILegacyScreen legacyScreen);

    ILegacySellItemUtils getLegacySellItemUtils();
    
    ILegacyCurrency getLegacyCurrencyInstance(String currencyStringValue);
    ILegacyCurrency getLegacyCurrencyInstance(BigDecimal currencyDecimalValue);
}
