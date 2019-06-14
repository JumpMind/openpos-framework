package org.jumpmind.pos.core.screen;

import java.util.List;

public class ItemOptionsScreen extends DynamicFormScreen {

    private static final long serialVersionUID = 1L;

    public ItemOptionsScreen() {
        super();
        setScreenType(ScreenType.ItemOptions);
    }
    
    private List<String> imageUrls;

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

}
