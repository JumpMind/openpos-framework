package org.jumpmind.pos.core.screen;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public class URLMenuItem extends MenuItem {

    private static final long serialVersionUID = 1L;

    public enum URLTargetMode {
        /** Opens URL in client application within In-Application Browser*/
        Blank("_blank"),
        /** Opens in view of client application if permitted to do so, otherwise behaves like {@link #Blank}  */
        Self("_self"),
        /** Opens in client system's web browser. */
        System("_system");
        
        private String value;
        
        private URLTargetMode(String value) {
            this.value = value;
        }
        public String getValue() {
            return value;
        }

        @JsonValue
        @Override
        public String toString() {
            return this.getValue();
        }

        @JsonCreator
        public static URLTargetMode fromString(String value) {
            for(URLTargetMode v : values()) {
                if (v.getValue().equalsIgnoreCase(value)) {
                    return v;
                }
            }
            throw new IllegalArgumentException(String.format("%s is not a valid value for URLTargetMode", value));
        }
        
    }
    
    private URLTargetMode targetMode = URLTargetMode.Blank;
    
    private String url;
    private String options;

    public URLMenuItem() {
    }
    
    public URLMenuItem(String url, String title, String icon) {
        this(url, title, icon, (String) null);
    }

    public URLMenuItem(String url, String title, String icon, String options) {
        setTitle(title);
        setIcon(icon);
        this.url = url;
        this.options = options;
    }
    
    public URLMenuItem(String url, String title, String icon, URLTargetMode target) {
        setTitle(title);
        setIcon(icon);
        this.url = url;
        this.targetMode = target;
    }
    
    public URLMenuItem(String url, String title, URLTargetMode target, String action) {
        this(url, title, target, action, null);
    }    
    
    public URLMenuItem(String url, String title, URLTargetMode target, String action, String options) {
        setTitle(title);
        this.setAction(action);
        this.url = url;
        this.targetMode = target;
        this.options = options;
    }    

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public URLTargetMode getTargetMode() {
        return targetMode;
    }

    public void setTargetMode(URLTargetMode targetMode) {
        this.targetMode = targetMode;
    }

    public String getOptions() {
        return options;
    }
    
    public void setOptions(String options) {
        this.options = options;
    }
    
}
