package org.jumpmind.pos.symds;

import org.jumpmind.symmetric.ISymmetricEngine;

import java.util.Properties;

public interface ISymDSConfigurator {

    public void beforeCreate(Properties properties);
    public void beforeStart(ISymmetricEngine engine);
    public String getWebContext();

}
