package org.jumpmind.pos.core.ui.messagepart;

import java.io.Serializable;

public class ProgressBarPart implements Serializable {

    private static final long serialVersionUID = 1L;

    // Integer representing progress bar percent (0-100)
    private int progress = -1;

    public ProgressBarPart() {
    }

    public ProgressBarPart(int progress) {
        this.progress = progress;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

}
