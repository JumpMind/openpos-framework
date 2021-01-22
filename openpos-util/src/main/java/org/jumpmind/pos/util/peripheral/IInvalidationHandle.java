package org.jumpmind.pos.util.peripheral;

public interface IInvalidationHandle {
    /**
     * Informs the handle provider that the targeted data source has been
     * updated.
     */
    void invalidate();
}
