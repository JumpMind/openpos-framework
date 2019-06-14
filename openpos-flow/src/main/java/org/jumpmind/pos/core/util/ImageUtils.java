package org.jumpmind.pos.core.util;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import org.jumpmind.pos.core.model.Signature;

public class ImageUtils {
    
    public Point[] toPoints(Signature signatureData) {
        if (signatureData == null || signatureData.getPointGroups() == null) {
            return null;
        }
        
        List<Point> allPoints = new ArrayList<>();
        signatureData.getPointGroups().stream().forEachOrdered(pointGroup -> {
            pointGroup.stream().forEachOrdered(point -> {
                Point p = new Point(point.getX(), point.getY());
                allPoints.add(p);
            });
        });
        
        return allPoints.toArray(new Point[]{});
    }
    
    public String toPointsString(Signature signatureData) {
        Point[] sigPoints = toPoints(signatureData);
        StringBuilder sbuilder = new StringBuilder();
        if (sigPoints != null) {
            for (Point p : sigPoints) {
                sbuilder.append(String.format("x%dy%d", p.x, p.y ));
            }
            return sbuilder.toString();
        } else {
            return null;
        }
    }

}
