package org.jumpmind.pos.core.content;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

@Component("fileSystemContentProvider")
@Scope("device")
public class FileSystemContentProvider extends AbstractFileContentProvider {

    @Value("${openpos.ui.content.file-system.baseContentPath:content/}")
    String baseContentPath;

    @Override
    public String getContentUrl(String deviceId, String key) {
        String filePathContent = "file:" + this.baseContentPath;
        String contentPath = getMostSpecificContent(deviceId, key, filePathContent);

        if (contentPath != null) {
            StringBuilder urlBuilder = new StringBuilder(AbstractFileContentProvider.SERVER_URL);
            urlBuilder.append(contentPath);
            urlBuilder.append(PROVIDER_TOKEN);
            urlBuilder.append("fileSystemContentProvider");
            if (this.contentVersion != null) {
                urlBuilder.append(VERSION_TOKEN);
                urlBuilder.append(this.contentVersion);
            }

            return urlBuilder.toString();
        }

        return null;
    }

    public void setBaseContentPath(String baseContentPath) {
        this.baseContentPath = baseContentPath;
    }

    public String getBaseContentPath() {
        return this.baseContentPath;
    }

    @Override
    public InputStream getContentInputStream(String contentPath) throws IOException {
        if (isFileSupported(contentPath)) {
            StringBuilder pathBuilder = new StringBuilder("file:");
            if (!contentPath.contains(this.baseContentPath)) {
                pathBuilder.append(this.baseContentPath);
            }
            pathBuilder.append(contentPath);
            String filePathContent = pathBuilder.toString();

            ClassLoader cl = this.getClass().getClassLoader();
            ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(cl);
            Resource[] resources = resolver.getResources(filePathContent);

            if (resources != null && resources.length > 0) {
                return resources[0].getInputStream();
            }
        }

        return null;
    }

}
