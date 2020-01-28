package org.jumpmind.pos.core.model;

import java.io.Serializable;

import lombok.Data;
import org.jumpmind.pos.util.model.Point;

@Data
public class Signature implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private Point[][] pointGroups;
    private String mediaType;
    private String base64EncodedImage;

}
