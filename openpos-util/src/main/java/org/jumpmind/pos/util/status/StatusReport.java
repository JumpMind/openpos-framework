package org.jumpmind.pos.util.status;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@EqualsAndHashCode
@NoArgsConstructor
public class StatusReport {

    private Date timestamp;
    private String name;
    private Status status;
    private String message;

    public StatusReport(String name, Status status) {
        this.name = name;
        this.status = status;
        this.timestamp = new Date();
    }

    public StatusReport(String name, Status status, String message) {
        this(name, status);
        this.message = message;
    }

}
