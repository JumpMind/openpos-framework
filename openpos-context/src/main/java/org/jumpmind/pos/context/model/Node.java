package org.jumpmind.pos.context.model;

import java.util.LinkedHashMap;
import java.util.Map;

import org.jumpmind.pos.persist.Column;
import org.jumpmind.pos.persist.Entity;
import org.jumpmind.pos.persist.Table;

@Table
public class Node extends Entity {
    
    @Column(primaryKey=true)
    private String nodeId;
    @Column
    private String nodeType; // STORE/DC/WORKSTATION/HANDELD/CUSTOMER HANDHELD/WEBSITE, etc.
    @Column
    private String address1;
    @Column
    private String address2;
    @Column
    private String address3;
    @Column
    private String city;
    @Column
    private String nodeState;
    @Column
    private String postalCode;
    @Column(size="10") 
    String locale;
    @Column(size="254")
    private String description;
    
    private Map<String, String> tags = new LinkedHashMap<String, String>();

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getNodeType() {
        return nodeType;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getAddress3() {
        return address3;
    }

    public void setAddress3(String address3) {
        this.address3 = address3;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTagValue(String tagName) {
        return tags.get(tagName);
    }

    public void setTagValue(String tagName, String tagValue) {
        tags.put(tagName, tagValue);
    }
    
    public void clearTagValue(String tagName) {
        tags.remove(tagName);
    }    
    
    public Map<String, String> getTags() {
        return new LinkedHashMap<>(tags);
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

}
