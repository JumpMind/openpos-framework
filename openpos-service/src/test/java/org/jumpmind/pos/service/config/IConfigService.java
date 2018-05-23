package org.jumpmind.pos.service.config;

import java.util.Date;

public interface IConfigService {
    public int getInt(String configName);
    public Date getDate(String configName);
    public long getLong(String configName);
    public Configuration getConfig(String configName);
}
