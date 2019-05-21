package org.jumpmind.pos.core.content;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
    List<String> providers;

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

    public File getContentStream() {
        return null;
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

    public InputStream getContentInputStream(String contentPath) throws FileNotFoundException {

        InputStream in = getClass().getResourceAsStream("/content/" + contentPath);

        if (in == null) {
            in = System.class.getResourceAsStream("/content/" + contentPath);
        }

        File file = new File(contentPath);

        /*
         * If we find the content on the file system use that over the class
         * path
         */
        if (file.exists()) {
            in = new FileInputStream(file);
        } else {
            logger.info("File resource not found for asset: {}", contentPath);
        }

        return in;
    }

}
