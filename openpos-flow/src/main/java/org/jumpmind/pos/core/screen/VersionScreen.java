package org.jumpmind.pos.core.screen;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class VersionScreen extends Screen {
    private static final long serialVersionUID = 1L;

    private List<Version> versions = null;
    private String title;
    private List<ActionItem> localMenuItems = new ArrayList<>();

    public VersionScreen() {
        setScreenType(ScreenType.Version);
    }

    public List<Version> getVersions() {
        return versions;
    }

    public VersionScreen addVersion(Version v) {
        if (this.versions == null) {
            this.versions = new ArrayList<>();
        }
        this.versions.add(v);
        return this;
    }
    
    public VersionScreen addVersion(String id, String text, String value) {
        return this.addVersion(new Version(id, text, value));
    }
    
    public void setVersions(List<Version> versions) {
        this.versions = versions;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    
    public void addLocalMenuItem(ActionItem menuItem) {
        this.localMenuItems.add(menuItem);
    }
    
    public void setLocalMenuItems(List<ActionItem> localMenuItems) {
        this.localMenuItems = localMenuItems;
    }
    
    public List<ActionItem> getLocalMenuItems() {
        return localMenuItems;
    }
    
    
    public static class Version implements Serializable {
        private static final long serialVersionUID = 1L;

        private String id;
        private String name;
        private String version;
        
        public Version(String id, String name, String version) {
            this.id = id;
            this.name = name;
            this.version = version;
        }
        
        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        public String getVersion() {
            return version;
        }
        public void setVersion(String version) {
            this.version = version;
        }
        
    }
}
