package org.jumpmind.pos.core.ui.message;

import java.io.Serializable;

public class WebcamViewerPart implements Serializable {

    private static final long serialVersionUID = 1L;

    public WebcamViewerPart() {
    }

    public WebcamViewerPart(String title) {
        this.title = title;
    }

    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
