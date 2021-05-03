package org.jumpmind.pos.core.ui.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
public class AdditionalLabel implements Serializable {
    private static final long serialVersionUID = 1L;

    private String label;
    private String value;
}
