package org.jumpmind.pos.core.content;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component("fileSystemContentProvider")
@ConfigurationProperties(prefix = "openpos.ui.content.file-system")
@Scope("device")
public class FileSystemContentProvider extends AbstractFileContentProvider {

    String baseContentPath;

    @Override
    public String getContentUrl(String deviceId, String key) {
        String filePathContent = "file:" + this.baseContentPath;
        String contentPath = getMostSpecificContent(deviceId, key, filePathContent);

        if (contentPath != null) {
            StringBuilder urlBuilder = new StringBuilder(AbstractFileContentProvider.SERVER_URL);
            urlBuilder.append(this.baseContentPath);
            if (!this.baseContentPath.endsWith("/")) {
                urlBuilder.append("/");
            }
            urlBuilder.append(contentPath);

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

    public String getUrlPattern() {
        return "${file-system}";
    }

}
