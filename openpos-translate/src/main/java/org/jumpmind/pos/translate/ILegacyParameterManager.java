package org.jumpmind.pos.translate;

public interface ILegacyParameterManager {

    Boolean getBooleanValue(String paramName);
    Boolean getBooleanValue(String paramName, Boolean defaultValue);
    String getStringValue(String paramName);
    Integer getIntegerValue(String paramName);
    Integer getIntegerValue(String paramName, Integer defaultValue);
}
