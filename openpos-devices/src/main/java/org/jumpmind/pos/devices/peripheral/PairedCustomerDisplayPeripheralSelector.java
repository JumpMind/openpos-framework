package org.jumpmind.pos.devices.peripheral;

import org.jumpmind.pos.devices.model.DeviceModel;
import org.jumpmind.pos.devices.service.IDevicesService;
import org.jumpmind.pos.devices.service.model.FindDevicesRequest;
import org.jumpmind.pos.util.clientcontext.ClientContext;
import org.jumpmind.pos.util.peripheral.CategoryDescriptor;
import org.jumpmind.pos.util.peripheral.IInvalidationHandle;
import org.jumpmind.pos.util.peripheral.IPeripheralDeviceSelector;
import org.jumpmind.pos.util.peripheral.PeripheralDeviceDescription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PairedCustomerDisplayPeripheralSelector implements IPeripheralDeviceSelector {
    private IInvalidationHandle invalidationHandle;
    private DeviceModel myDevice;
    // TODO: Remove - only for experimenting while developing
    private DeviceModel pairedDevice;

    // TODO: Remove - only for experimenting while developing
    @Autowired
    IDevicesService devicesService;

    @Autowired
    ClientContext clientContext;

    public void notifyChange() {
        if (invalidationHandle != null) {
            invalidationHandle.invalidate();
        }
    }

    @Override
    public void initialize(IInvalidationHandle invalidationHandle) {
        this.invalidationHandle = invalidationHandle;
        myDevice = devicesService.getMyDevice().getDeviceModel();
    }

    @Override
    public CategoryDescriptor getCategory() {
        return new CategoryDescriptor(
                "DEVICE.PAIRED_DEVICE",
                "device:peripheral.category",
                "Device",
                "device:no.peripheral.category");
    }

    @Override
    public PeripheralDeviceDescription getCurrentSelection() {
        // TODO: Remove - use service and actually store this
        if (pairedDevice == null) {
            return null;
        }

        return new PeripheralDeviceDescription(pairedDevice.getDeviceId(), pairedDevice.getDeviceId());
    }

    @Override
    public List<PeripheralDeviceDescription> getPossibleSelections() {
        return getPairableDevices()
                .stream()
                .map(device -> PeripheralDeviceDescription.builder()
                        .id(device.getDeviceId())
                        .displayName(device.getDeviceId())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public void setSelection(String id) {
        // TODO: Just temporary crap-ola
        pairedDevice = getPairableDevices().stream()
                .filter(device -> device.getDeviceId().equals(id))
                .findFirst()
                .orElse(null);

        notifyChange();
    }

    protected List<DeviceModel> getPairableDevices() {
        // TODO: Just temporary crap-ola
        return devicesService
                .findDevices(
                        FindDevicesRequest.builder()
                                .businessUnitId(clientContext.get("businessUnitId"))
                                .build()
                )
                .getDevices()
                .stream()
                .filter(device -> !device.getDeviceId().equals(myDevice.getDeviceId()))
                .collect(Collectors.toList());
    }
}
