package org.jumpmind.pos.core.ui.data;

import java.beans.PropertyChangeListener;

public interface IHasObservableUIDataMessageProviderProperty {

    public void addPropertyChangeListener(PropertyChangeListener listener);
    public void removePropertyChangeListener(PropertyChangeListener listener);
}
