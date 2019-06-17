package org.jumpmind.pos.core.util;

final public class NodeUtils {

    private NodeUtils() {
    }

    public static String parseStoreId(String nodeId) {
        return nodeId.substring(0, 5);
    }

    public static String parseWorkstationId(String nodeId) {
        return nodeId.substring(6);
    }

    public static String makeNodeId(String storeId, String workStationId) {
        return String.format("%s-%s", storeId, workStationId);
    }
}
