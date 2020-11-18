package org.jumpmind.pos.util.web;

import org.apache.commons.lang3.StringUtils;

import javax.activation.MimetypesFileTypeMap;

public class MimeTypeUtil {
    public static String getContentType(String filename) {
        if (StringUtils.isEmpty(filename)) {
            return null;
        }

        MimetypesFileTypeMap mimetypesFileTypeMap = new MimetypesFileTypeMap();
        return mimetypesFileTypeMap.getContentType(filename);
    }

    public static boolean isContentTypeAudio(String contentType) {
        return StringUtils.startsWith(contentType, "audio/");
    }

    public static boolean isContentTypeImage(String contentType) {
        return StringUtils.startsWith(contentType, "image/");
    }
}
