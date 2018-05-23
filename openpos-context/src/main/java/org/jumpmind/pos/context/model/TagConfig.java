package org.jumpmind.pos.context.model;

import java.util.ArrayList;
import java.util.List;

public class TagConfig {

    private List<TagModel> tags = new ArrayList<>();

    public List<TagModel> getTags() {
        return tags;
    }

    public void setTags(List<TagModel> tags) {
        this.tags = tags;
    }
    
}
