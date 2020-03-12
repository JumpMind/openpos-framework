package org.jumpmind.pos.devices.service;

import lombok.extern.slf4j.Slf4j;
import org.jumpmind.pos.devices.DeviceNotAuthorizedException;
import org.jumpmind.pos.devices.DeviceNotFoundException;
import org.jumpmind.pos.devices.model.DeviceModel;
import org.jumpmind.pos.devices.model.DeviceParamModel;
import org.jumpmind.pos.devices.model.DevicesRepository;
import org.jumpmind.pos.devices.service.model.PersonalizationRequest;
import org.jumpmind.pos.devices.service.model.PersonalizationResponse;
import org.jumpmind.pos.persist.ITagProvider;
import org.jumpmind.pos.service.Endpoint;
import org.jumpmind.util.AppUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@Endpoint(path="/devices/personalize")
public class PersonalizeEndpoint {

    @Autowired
    DevicesRepository devicesRepository;

    @Value("${openpos.businessunitId:undefined}")
    String businessUnitId;

    @Autowired(required = false)
    List<ITagProvider> tagProviders;

    @Autowired
    Environment env;

    public PersonalizationResponse personalize(@RequestBody PersonalizationRequest request){
        String authToken = request.getDeviceToken();
        String deviceId = request.getDeviceId();
        String appId = request.getAppId();

        DeviceModel deviceModel;

        if(isNotBlank(deviceId) && isNotBlank(appId)){
            // TODO add a configuration map of appIds that are allowed to share deviceIds. IE probably shouldn't allow a self-checkout share with pos
            try{
                log.info("Validating auth request of {} as {}", deviceId, appId);
                String auth = devicesRepository.getDeviceAuth(request.getDeviceId(), request.getAppId());

                if( !auth.equals(authToken)) {
                    throw new DeviceNotAuthorizedException();
                }

            } catch (DeviceNotFoundException ex){
                log.info("Registering {} as {}", deviceId, appId);
                // if device doesn't exist create a new unique code
                authToken = UUID.randomUUID().toString();
                devicesRepository.saveDeviceAuth(appId, deviceId, authToken);

            }

            deviceModel = new DeviceModel();
            deviceModel.setAppId(request.getAppId());
            deviceModel.setDeviceId(request.getDeviceId());
            if( request.getPersonalizationParameters() != null ) {
                deviceModel.setDeviceParamModels(
                        request.getPersonalizationParameters().keySet().stream().map( key -> new DeviceParamModel( key, request.getPersonalizationParameters().get(key))).collect(Collectors.toList())
                );
            }
        } else if ( isNotBlank(authToken)){
            deviceModel = devicesRepository.getDeviceByAuth(authToken);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "DeviceId and AppId or AuthToken are required for personalization");
        }



        deviceModel.setDeviceType(request.getDeviceType());
        deviceModel.setTimezoneOffset(AppUtils.getTimezoneOffset());
        deviceModel.setBusinessUnitId(businessUnitId);
        // TODO check properties also before using default
        deviceModel.setLocale(Locale.getDefault().toString());
        deviceModel.setLastUpdateTime(new Date());
        deviceModel.setLastUpdateBy("personalization");
        deviceModel.updateTags((AbstractEnvironment) env);

        if (this.tagProviders != null && tagProviders.size() > 0) {
            MutablePropertySources propSrcs = ((AbstractEnvironment) env).getPropertySources();
            StreamSupport.stream(propSrcs.spliterator(), false)
                    .filter(ps -> ps instanceof EnumerablePropertySource)
                    .map(ps -> ((EnumerablePropertySource<?>) ps).getPropertyNames())
                    .flatMap(Arrays::<String>stream)
                    .filter(propName -> propName.startsWith("openpos.tags")).map(propName -> propName.substring("openpos.tags".length() + 1))
                    .forEach(propName -> {
                        for (ITagProvider tagProvider:
                                this.tagProviders) {
                            String value = tagProvider.getTagValue(deviceId, businessUnitId, propName);
                            if (isNotBlank(value)) {
                                deviceModel.setTagValue(propName, value);
                            }
                        }
                    });
        }

        devicesRepository.saveDevice(deviceModel);

        return PersonalizationResponse.builder()
                .authToken(authToken)
                .deviceModel(deviceModel)
                .build();
    }
}
