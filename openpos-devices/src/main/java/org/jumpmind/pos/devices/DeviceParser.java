package org.jumpmind.pos.devices;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Component
@ConfigurationProperties("openpos.device")
public class DeviceParser {

    DeviceIdFormat deviceIdFormat = new DeviceIdFormat();

    public String getWorkstationId(String deviceId) {
        List<DeviceIdFormat.DeviceIdElement> elements = deviceIdFormat.getElements();
        for (DeviceIdFormat.DeviceIdElement element : elements) {
            if (element.getName().equalsIgnoreCase("workstationid")) {
                if (deviceId.length() >= element.getEndIndex()) {
                    return deviceId.substring(element.getStartIndex(), element.getEndIndex());
                }
            }
        }
        return "";
    }

    public String getBusinessUnitId(String deviceId) {
        List<DeviceIdFormat.DeviceIdElement> elements = deviceIdFormat.getElements();
        for (DeviceIdFormat.DeviceIdElement element : elements) {
            if (element.getName().equalsIgnoreCase("businessunitid")) {
                if (deviceId.length() >= element.getEndIndex()) {
                    return deviceId.substring(element.getStartIndex(), element.getEndIndex());
                }
            }
        }
        return "";
    }


    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DeviceIdFormat {

        String format;
        List<DeviceIdElement> elements = new ArrayList<>();

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        public static class DeviceIdElement {
            String name;
            int startIndex;
            int endIndex;
        }

    }

}
