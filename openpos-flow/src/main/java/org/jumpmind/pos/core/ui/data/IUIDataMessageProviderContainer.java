package org.jumpmind.pos.core.ui.data;

import java.util.Map;

/**
 * Classes (such as ITranslators) should implement this interface if it needs to
 * provide access to a map of UIDataMessageProviders for purposes of asynchronous
 * transmission of data from the server to the client.
 */
public interface IUIDataMessageProviderContainer {

    /** 
     * @return A map of string keys to UIDataMessageProviders that can be used
     * for async fetching of data from server to client.
     */
    public Map<String, UIDataMessageProvider<?>> getDataMessageProviderMap();
}