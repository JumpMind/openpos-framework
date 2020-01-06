package org.jumpmind.pos.core.ui.messagepart;

import java.io.Serializable;

public class ImagePanelPart implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String instructions;
    private String image;
    
    public String getInstructions() {
        return instructions;
    }
    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }
    public String getImage() {
        return image;
    }
    public void setImage(String image) {
        this.image = image;
    }
}
