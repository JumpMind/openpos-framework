package org.jumpmind.pos.peripheral.scale;

import java.util.Map;

public interface ICheckoutScale {

    public void initialize(Map<String,Object> settings);

    public ScaleWeightData getScaleWeightData();

    public String getScaleUnit();
    public long getPollInterval();
    public long getReadTimeout();


}
