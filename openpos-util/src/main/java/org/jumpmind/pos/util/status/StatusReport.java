package org.jumpmind.pos.util.status;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@EqualsAndHashCode
@NoArgsConstructor
public class StatusReport {

    public final static String DEFAULT_ICON_NAME = "apps"; // grid of squares

    @EqualsAndHashCode.Exclude
    private Date timestamp;
    private String name;
    private String iconName = DEFAULT_ICON_NAME;
    private Status status;
    private String message;

    public StatusReport(String name, String iconName, Status status) {
        this.name = name;
        this.iconName = iconName;
        this.status = status;
        this.timestamp = new Date();
    }

    public StatusReport(String name, String iconName, Status status, String message) {
        this(name, iconName, status);
        this.message = message;
    }

}
