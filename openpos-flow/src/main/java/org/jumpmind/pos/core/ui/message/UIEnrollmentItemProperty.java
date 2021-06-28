package org.jumpmind.pos.core.ui.message;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class UIEnrollmentItemProperty implements Serializable {
    private String name;
    private String value;
    private String type;
}
