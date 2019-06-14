package org.jumpmind.pos.core.content;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("device")
public class ContentProviderService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${openpos.ui.content.providers:null}")
    String[] providers;

    @Autowired
    protected Map<String, IContentProvider> contentProviders;

    public String resolveContent(String deviceId, String key) {
        String contentUrl = null;
        List<IContentProvider> providerPriorities = getProviderPriorities();
        for (IContentProvider provider : providerPriorities) {
            contentUrl = provider.getContentUrl(deviceId, key);
            if (contentUrl != null) {
                return contentUrl;
            }
        }

        return contentUrl;
    }

    private List<IContentProvider> getProviderPriorities() {
        List<IContentProvider> providerPriorities = new ArrayList<>();

        if (providers != null) {
            for (String provider : providers) {
                if (contentProviders.containsKey(provider)) {
                    providerPriorities.add(contentProviders.get(provider));
                }
            }
        }

        return providerPriorities;
    }

    public InputStream getContentInputStream(String contentPath, String provider) {

        IContentProvider contentProvider = contentProviders.get(provider);
        InputStream contentInputStream = null;
        try {
            contentInputStream = contentProvider.getContentInputStream(contentPath);
            if (contentInputStream != null) {
                return contentInputStream;
            }
        } catch (IOException e) {
            logger.debug("Unable to get content input stream", e);

        }

        logger.debug("Resource not found for content: {}", contentPath);

        return null;
    }

}
