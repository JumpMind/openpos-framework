package org.jumpmind.pos.print;

import java.util.Map;

public interface IConnectionFactory {

    public PeripheralConnection open(Map<String, Object> settings);
    public void close(PeripheralConnection peripheralConnection);

}
