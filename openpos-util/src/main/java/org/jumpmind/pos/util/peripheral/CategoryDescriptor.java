package org.jumpmind.pos.util.peripheral;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryDescriptor {
    /**
     * A referencable unique id of the category. This id should be shared with the equivalent status so they may be
     * grouped.
     */
    private String id;

    /**
     * The i18n key that describes the display of the category.
     */
    private String localizationDisplayKey;
}
