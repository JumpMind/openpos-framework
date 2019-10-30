package org.jumpmind.pos.core.ui.data;

import lombok.Data;

import java.util.List;

@Data
public abstract class UIDataMessageProvider<T> {

    private int seriesId;
    private boolean newSeries;

    abstract public List<T> getNextDataChunk();
    abstract public void reset();
}
