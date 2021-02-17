package org.jumpmind.pos.peripheral.scale;

import java.math.BigDecimal;
import java.util.Map;

public class VirtualCheckoutScale implements ICheckoutScale{

    private Map<String, Object> settings;

    @Override
    public void initialize(Map<String, Object> settings) {
        this.settings = settings;
    }

    @Override
    public ScaleWeightData getScaleWeightData() {
        ScaleWeightData scaleWeightData = new ScaleWeightData();
        scaleWeightData.setWeight(new BigDecimal(10.00));
        scaleWeightData.setSuccessful(true);
        return scaleWeightData;
    }

    @Override
    public String getScaleUnit() {
        return (String)settings.get("scaleUnit");
    }

    @Override
    public long getPollInterval() {
        return 300;
    }

    @Override
    public long getReadTimeout() {
        return 30000;
    }

    @Override
    public Map<String, Object> getSettings() {
        return settings;
    }
}
