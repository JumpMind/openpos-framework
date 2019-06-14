package org.jumpmind.pos.core.model;

import java.io.Serializable;
import java.util.List;

import org.jumpmind.pos.util.model.Point;

public class Signature implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private List<List<Point>> pointGroups;
    private String mediaType;
    private String base64EncodedImage;

    public Signature() {}
    
    public List<List<Point>> getPointGroups() {
        return pointGroups;
    }


    public void setPointGroups(List<List<Point>> pointGroups) {
        this.pointGroups = pointGroups;
    }


    public String getMediaType() {
        return mediaType;
    }


    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }


    public String getBase64EncodedImage() {
        return base64EncodedImage;
    }


    public void setBase64EncodedImage(String base64EncodedImage) {
        this.base64EncodedImage = base64EncodedImage;
    }

}
