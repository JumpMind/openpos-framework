package org.jumpmind.pos.devices.model;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.StreamSupport;

import lombok.*;
import org.apache.commons.collections4.map.CaseInsensitiveMap;
import org.jumpmind.pos.persist.*;
import org.jumpmind.pos.persist.model.ITaggedModel;
import org.jumpmind.util.AppUtils;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.MutablePropertySources;

@TableDef(name = "device", description = "A device used to transaction commerce for a Business Unit")
@Tagged(includeTagsInPrimaryKey = false)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class DeviceModel extends AbstractModel implements ITaggedModel {

    @ToString.Include
    @EqualsAndHashCode.Include
    @ColumnDef(primaryKey = true, description = "A unique identifier for this Device")
    private String deviceId;

    @ToString.Include
    @EqualsAndHashCode.Include
    @ColumnDef(primaryKey = true)
    private String appId;

    @ColumnDef(description = "The type of the Device.  Store/DC workstation or handheld, Customer handheld, website, etc.")
    private String deviceType; // STORE/DC/WORKSTATION/HANDELD/CUSTOMER
    // HANDHELD/WEBSITE, etc.

    @ColumnDef(size = "10", description = "The locale under which this Device currently operates")
    String locale;

    @ColumnDef(description = "The timezone offset under which this Device currently operates")
    String timezoneOffset = AppUtils.getTimezoneOffset();

    @ColumnDef(description = "The Business Unit under which this Device currently operates")
    String businessUnitId;

    @ColumnDef(size = "255", description = "A user defined name for the Device")
    private String description;

    private Map<String, String> tags = new CaseInsensitiveMap<String, String>();

    @Override
    public String getTagValue(String tagName) {
        return tags.get(tagName);
    }

    @Override
    public void setTagValue(String tagName, String tagValue) {
        tags.put(tagName, tagValue);
    }

    @Override
    public void setTags(Map<String, String> tags) {
        this.tags.clear();
        this.tags.putAll(tags);
    }

    @Override
    public void clearTagValue(String tagName) {
        tags.remove(tagName);
    }

    @Override
    public Map<String, String> getTags() {
        return tags != null ? new CaseInsensitiveMap<>(tags) : new CaseInsensitiveMap<>();
    }

    public void updateTags(AbstractEnvironment env) {
        MutablePropertySources propSrcs = env.getPropertySources();
        StreamSupport.stream(propSrcs.spliterator(), false)
                .filter(ps -> ps instanceof EnumerablePropertySource)
                .map(ps -> ((EnumerablePropertySource<?>) ps).getPropertyNames())
                .flatMap(Arrays::<String>stream)
                .filter(propName -> propName.startsWith("openpos.tags"))
                .forEach(propName ->
                        tags.put(propName.substring("openpos.tags".length() + 1), env.getProperty(propName) != null ? env.getProperty(propName) : "*"));

    }

    public String withOutBusinessUnitId() {
        String withOutBusinessUnitId = deviceId;
        int index = deviceId.indexOf(businessUnitId);
        if (index == 0) {
            withOutBusinessUnitId = deviceId.substring(businessUnitId.length());
            if (withOutBusinessUnitId.startsWith("-")) {
                withOutBusinessUnitId = withOutBusinessUnitId.substring(1);
            }
        }
        return withOutBusinessUnitId;
    }

    private List<DeviceParamModel> deviceParamModels;
}
