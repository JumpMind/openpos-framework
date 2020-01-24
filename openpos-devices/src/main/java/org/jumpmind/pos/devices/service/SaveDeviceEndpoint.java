package org.jumpmind.pos.devices.service;

import org.jumpmind.pos.devices.DeviceNotAuthorizedException;
import org.jumpmind.pos.devices.DeviceNotFoundException;
import org.jumpmind.pos.devices.model.DeviceModel;
import org.jumpmind.pos.devices.model.DevicesRepository;
import org.jumpmind.pos.devices.service.model.SaveDeviceRequest;
import org.jumpmind.pos.devices.service.model.SaveDeviceResponse;
import org.jumpmind.pos.service.Endpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Endpoint(path="/devices/device")
@Transactional(transactionManager = "devTxManager")
public class SaveDeviceEndpoint {

    @Autowired
    DevicesRepository devicesRepository;

    public SaveDeviceResponse saveDevice(SaveDeviceRequest request) {

        DeviceModel modelToSave = request.getDeviceModel();
        String authToken = request.getAuthToken();

        // TODO add a configuration map of appIds that are allowed to share deviceIds. IE probabaly shouldn't allow a self-checkout share with pos
        try{
            DeviceModel deviceModel = devicesRepository.getDevice(modelToSave.getDeviceId(), modelToSave.getAppId());
        } catch (DeviceNotFoundException ex){
            // if device doesn't exist create a new unique code
            authToken = UUID.randomUUID().toString();
            devicesRepository.saveDeviceAuth(modelToSave.getAppId(), modelToSave.getDeviceId(), authToken);
        }

        //Make sure we are authorized to make this update
        String registeredAuthToken = null;
        try{
            registeredAuthToken = devicesRepository.getDeviceAuth(modelToSave.getDeviceId(), modelToSave.getAppId());
        } catch( DeviceNotFoundException ex ) {
            // TODO what should we do here?
        }

        // If we have a token for our deviceId and AppId it needs to match the request
        if( registeredAuthToken != null && !registeredAuthToken.equals(authToken)){
            throw new DeviceNotAuthorizedException();
        }

        // If we are authorized we can save the model
        devicesRepository.saveDevice(request.getDeviceModel());

        return SaveDeviceResponse.builder()
                .deviceModel(request.getDeviceModel())
                .build();
    }
    
}
