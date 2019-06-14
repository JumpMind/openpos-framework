package org.jumpmind.pos.core.screen;

import org.jumpmind.pos.core.template.SellTemplate;

public class DetailTextScreen extends Screen {
    
    private static final long serialVersionUID = 1L;
    
    private String text;
    
    private String title;
    
    public DetailTextScreen() {
        setScreenType(ScreenType.DetailText);
        setTemplate(new SellTemplate());
    }
    
    public String getText() {
        return text;
    }
    
    public void setText(String text) {
        this.text = text;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    
}
