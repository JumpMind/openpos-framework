package org.jumpmind.pos.context.model;

public class TagModel {

    public static final String TAG_PREFIX = "TAG_";
    public static final String TAG_ALL = "*";
    public static final String TAG_NUMERIC_TYPE = "NUMERIC";
    public static final String TAG_CODE_TYPE = "CODE";
    
    public static final String BRAND_ID_TAG = "BRAND_ID";

    private String name; // e.g. STORE
    private String group;  // e.g. LOCATION
    private int level;
    private String dataType; // NUMERIC or CODE
    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getGroup() {
        return group;
    }
    public void setGroup(String group) {
        this.group = group;
    }
    public int getLevel() {
        return level;
    }
    public void setLevel(int level) {
        this.level = level;
    }
    public String getDataType() {
        return dataType;
    }
    public void setDataType(String dataType) {
        this.dataType = dataType;
    }
    

    
}
