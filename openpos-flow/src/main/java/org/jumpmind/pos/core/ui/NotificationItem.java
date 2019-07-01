package org.jumpmind.pos.core.ui;

import java.io.Serializable;

public class NotificationItem implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String value;
    private NotificationType type;

    public NotificationItem(String id, String value, NotificationType type) {
        this.id = id;
        this.value = value;
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
