package org.jumpmind.pos.core.ui.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.jumpmind.pos.core.ui.ActionItem;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class UIPurchasedItem implements Serializable {
    private static final long serialVersionUID = 1L;

    private String title;
    private List<UILabel> labels;
    private String imageUrl;
    private String salePrice;
    private String originalPrice;

    private List<ActionItem> actions;
}
