package org.jumpmind.pos.core.ui.data;

import lombok.Data;

import java.beans.PropertyChangeListener;
import java.util.List;

@Data
public abstract class UIDataMessageProvider<T> {

    private String providerKey;
    private int seriesId;
    private boolean newSeries;

    abstract public List<T> getNextDataChunk();
    abstract public void reset();
}
