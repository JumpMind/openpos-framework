package org.jumpmind.pos.core.ui.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UILabel implements Serializable {
    private static final long serialVersionUID = 1L;

    private String icon;
    private String text;
}
