package org.jumpmind.pos.core.ui.messagepart;

import java.io.Serializable;

public class ImageTextPanelPart implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String instructions;
    private String image;
    
    public ImageTextPanelPart() {}
    public ImageTextPanelPart(String instructions, String image) {
        this.instructions = instructions;
        this.image = image;
    }
    public ImageTextPanelPart(String instructions) {
        this.instructions = instructions;
    }
    
    public String getInstructions() {
        return instructions;
    }
    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }
    public ImageTextPanelPart instructions(String instructions) {
        this.setInstructions(instructions);
        return this;
    }
    public String getImage() {
        return image;
    }
    public void setImage(String image) {
        this.image = image;
    }
    public ImageTextPanelPart image(String image) {
        this.setImage(image);
        return this;
    }
}
