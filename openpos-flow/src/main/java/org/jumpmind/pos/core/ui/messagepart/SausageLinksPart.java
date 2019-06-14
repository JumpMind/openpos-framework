package org.jumpmind.pos.core.ui.messagepart;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jumpmind.pos.core.screen.ActionItem;
import org.jumpmind.pos.core.ui.NotificationItem;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;

public class SausageLinksPart implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<ActionItem> links;
    /**
     * Put properties in this map if they are optional. When not set, they don't
     * show up in the json which means less overhead.
     */
    private Map<String, Object> optionalProperties = new HashMap<String, Object>();    

    public SausageLinksPart() {
    }
    
    public SausageLinksPart(List<ActionItem> links) {
        this.links = links;
    }
    
    public SausageLinksPart(List<ActionItem> links, List<NotificationItem> notificationItems) {
        this(links);
        this.setNotificationItems(notificationItems);
    }
    
    

    public List<ActionItem> getLinks() {
        return links;
    }

    public void setLinks(List<ActionItem> links) {
        this.links = links;
    }
    
    @SuppressWarnings("unchecked")
    public List<NotificationItem> getNotificationItems() {
        return (List<NotificationItem>)get("notificationItems");
    }

    public void setNotificationItems(List<NotificationItem> notificationItems) {
        put("notificationItems", notificationItems);
    }

    public void addNotificationItem(NotificationItem item){
        List<NotificationItem> items;
        if(contains("notificationItems")) {
            items = this.getNotificationItems();
        } else {
            items = new ArrayList<>();
            put("notificationItems", items);
        }

        items.add(item);
    }
    
    @JsonAnyGetter
    public Map<String, Object> any() {
        return this.optionalProperties;
    }

    @JsonAnySetter
    public void put(String name, Object value) {
        this.optionalProperties.put(name, value);
    }

    public boolean contains(String name) {
        return this.optionalProperties.containsKey(name);
    }

    public Object get(String name) {
        return optionalProperties.get(name);
    }

    public void clearAdditionalProperties() {
        this.optionalProperties.clear();
    }
    
}
