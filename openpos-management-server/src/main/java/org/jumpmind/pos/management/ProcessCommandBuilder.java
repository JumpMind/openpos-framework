package org.jumpmind.pos.management;

import java.util.List;

public interface ProcessCommandBuilder {
    List<String> constructProcessCommandParts(DeviceProcessInfo pi);
    List<String> constructKillCommandParts(DeviceProcessInfo pi);
}
