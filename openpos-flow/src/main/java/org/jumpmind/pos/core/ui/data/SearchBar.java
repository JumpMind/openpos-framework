package org.jumpmind.pos.core.ui.data;

import org.jumpmind.pos.core.model.FieldInputType;
import org.jumpmind.pos.core.ui.IconType;

import java.io.Serializable;

public class SearchBar implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer scanMinLength = 1;
    private Integer scanMaxLength = 22;
    private String scanIcon = IconType.Search;
    private String scanActionName = "Scan";
    private String keyedActionName = "Scan";
    private String searchText = "Scan/Key Something";
    private FieldInputType inputType = FieldInputType.WordText;

    public Integer getScanMinLength() {
        return scanMinLength;
    }

    public void setScanMinLength(Integer scanMinLength) {
        this.scanMinLength = scanMinLength;
    }

    public Integer getScanMaxLength() {
        return scanMaxLength;
    }

    public void setScanMaxLength(Integer scanMaxLength) {
        this.scanMaxLength = scanMaxLength;
    }

    public String getScanActionName() {
        return scanActionName;
    }

    public void setScanActionName(String scanActionName) {
        this.scanActionName = scanActionName;
    }

    public String getSearchText() {
        return searchText;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

    public FieldInputType getInputType() {
        return inputType;
    }

    public void setInputType(FieldInputType inputType) {
        this.inputType = inputType;
    }

    public String getScanIcon() {
        return scanIcon;
    }

    public void setScanIcon(String scanIcon) {
        this.scanIcon = scanIcon;
    }

    public void setKeyedActionName(String keyedActionName) {
        this.keyedActionName = keyedActionName;
    }

    public String getKeyedActionName() {
        return this.keyedActionName;
    }
}
