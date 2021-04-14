package org.jumpmind.pos.persist.impl;

import java.util.List;

public class ShadowTablesConfigModel {
    private String deviceMode;
    private String tablePrefix;
    private boolean validateTablesInQueries;
    private List<String> includesList;
    private List<String> excludesList;

    public ShadowTablesConfigModel(String deviceMode, String tablePrefix, boolean validateTablesInQueries, List<String> includesList, List<String> excludesList) {
        this.deviceMode = deviceMode;
        this.tablePrefix = tablePrefix;
        this.validateTablesInQueries = validateTablesInQueries;
        this.includesList = includesList;
        this.excludesList = excludesList;
    }

    public String getDeviceMode() { return deviceMode; }

    public void setDeviceMode(String deviceMode) { this.deviceMode = deviceMode; }

    public String getTablePrefix() { return tablePrefix; }

    public void setTablePrefix(String tablePrefix) { this.tablePrefix = tablePrefix; }

    public boolean validateTablesInQueries() { return validateTablesInQueries; }

    public void setValidateTablesInQueries(boolean validateTablesInQueries) { this.validateTablesInQueries = validateTablesInQueries; }

    public List<String> getIncludesList() {
        return includesList;
    }

    public void setIncludesList(List<String> includesList) {
        this.includesList = includesList;
    }

    public List<String> getExcludesList() {
        return excludesList;
    }

    public void setExcludesList(List<String> excludesList) {
        this.excludesList = excludesList;
    }
}
