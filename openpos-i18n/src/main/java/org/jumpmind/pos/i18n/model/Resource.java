package org.jumpmind.pos.i18n.model;

import org.jumpmind.pos.persist.Column;
import org.jumpmind.pos.persist.Entity;
import org.jumpmind.pos.persist.Table;

@Table(description = "Internationalization resource table")
public class Resource extends Entity {

    @Column(primaryKey = true)
    private String baseName;
    @Column(primaryKey = true)
    private String brand;
    @Column(primaryKey = true)
    private String locale;
    @Column(primaryKey = true)
    private String stringKey;

    @Column
    private String pattern;

    /**
     * 
     * @return the base name of a resource
     */
    public String getBaseName() {
        return baseName;
    }

    public void setBaseName(String baseName) {
        this.baseName = baseName;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getStringKey() {
        return stringKey;
    }

    public void setStringKey(String stringKey) {
        this.stringKey = stringKey;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String string) {
        this.pattern = string;
    }
}
