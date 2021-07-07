package org.jumpmind.pos.util.peripheral;

import java.util.List;

public interface IPeripheralDeviceSelector {
    /**
     * Called on implementors in order to provide a handle to a invalidator
     * object.
     */
    void initialize(IInvalidationHandle invalidationHandle);

    /**
     * Called to determine if the status bar detail item should be enabled
     */
    boolean isEnabled();

    /**
     * Gets the the category.
     */
    CategoryDescriptor getCategory();

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

    /**
     * Updates the peripheral selection to the specified id.
     *
     * @param id The unique ID of the device.
     */
    void setSelection(String id);
}
