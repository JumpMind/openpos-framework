package org.jumpmind.pos.core.screen;

public class EmbeddedWebPageScreen extends Screen {

    private static final long serialVersionUID = 1L;
    
    private String url;
            
    public EmbeddedWebPageScreen() {
        setType(ScreenType.EmbeddedWebPage);
    }

    public EmbeddedWebPageScreen(String url) {
        this();
        this.url = url;
    }
    
    public void setUrl(String url) {
        this.url = url;
    }
        
    public String getUrl() {
        return url;
    }
}
