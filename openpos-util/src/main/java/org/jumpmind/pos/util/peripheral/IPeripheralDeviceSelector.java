package org.jumpmind.pos.util.peripheral;

import java.util.List;

public interface IPeripheralDeviceSelector {
    /**
     * Called on implementors in order to provide a handle to a invalidator
     * object.
     */
    void initialize(IInvalidationHandle invalidationHandle);

    /**
     * Gets the name of the category.
     */
    String getCategory();

    /**
     * Gets the current device selection. Use `null` to indicate that there is
     * currently no selection.
     */
    PeripheralDeviceDescription getCurrentSelection();

    /**
     * Gets a list of possible selections that this reporter instance could
     * be in. `null` may be used to indicate that selection is disabled.
     */
    List<PeripheralDeviceDescription> getPossibleSelections();
}
