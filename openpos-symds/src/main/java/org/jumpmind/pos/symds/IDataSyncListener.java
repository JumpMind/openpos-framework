package org.jumpmind.pos.symds;

import java.util.Map;

public interface IDataSyncListener {

    public boolean isApplicable(String channelId, String tableName);

    public void onDataWrite(SyncData data);

}
