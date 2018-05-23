package org.jumpmind.pos.translate;

import java.util.Properties;

public interface ITranslatorFactory {

    public ITranslator createScreenTranslator(ILegacyScreen orposScreen, String appId, Properties properties);
    
}
