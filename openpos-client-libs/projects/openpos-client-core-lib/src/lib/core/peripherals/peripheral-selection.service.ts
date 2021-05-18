import { Injectable } from '@angular/core';
import { Observable, ConnectableObservable } from 'rxjs';
import { filter, map, publishBehavior, tap } from 'rxjs/operators';

import { MessageTypes } from '../messages/message-types';
import { SessionService } from '../services/session.service';
import { PeripheralDeviceSelectionMessage } from '../messages/peripheral-device-selection';
import { ActionMessage } from '../messages/action-message';

@Injectable({
    providedIn: 'root'
})
export class PeripheralSelectionService {
    readonly peripheralCategories$: Observable<PeripheralCategory[]>;

    private _categoryNameToData = new Map<string, PeripheralCategory>();

    constructor(
        private session: SessionService
    ) {
        const connectable = session.getMessages(MessageTypes.PERIPHERAL_DEVICE_SELECTION).pipe(
            map(m => m as PeripheralDeviceSelectionMessage),
            filter(m => !!m),
            map(m => {
                const devices = m.available;

                return <PeripheralCategory> {
                    id: m.category.id,
                    localizationKey: m.category.localizationDisplayKey,
                    icon: m.category.icon,
                    localizationNoCategorySelectedKey: m.category.localizationNoCategorySelectedKey,
                    knownDevices: devices,
                    selectedDevice: m.selectedDevice
                };
            }),
            tap(n => this._categoryNameToData.set(n.id, n)),
            map(() => Array.from(this._categoryNameToData.values())),
            publishBehavior([])
        ) as ConnectableObservable<PeripheralCategory[]>;

        // make it hot...
        connectable.connect();

        this.peripheralCategories$ = connectable;
    }

    selectDevice(category: PeripheralCategoryRef, device: PeripheralDeviceRef) {
        let categoryName: string;
        let deviceId: string;

        if (typeof(category) == 'string') {
            categoryName = category as string;
        } else {
            categoryName = (category as PeripheralCategory).id;
        }

        if (typeof(device) == 'string') {
            deviceId = device as string;
        } else {
            deviceId = (device as PeripheralDevice).id;
        }

        this.session.sendMessage(new ActionMessage('SelectPeripheral', true, { category: categoryName, id: deviceId }));
    }
}

export type PeripheralCategoryRef = PeripheralCategory | string;
export interface PeripheralCategory {
    id: string;
    localizationKey: string;
    icon: string;
    localizationNoCategorySelectedKey: string;
    knownDevices: PeripheralDevice[];
    selectedDevice: PeripheralDevice;
}

export type PeripheralDeviceRef = PeripheralDevice | string;
export interface PeripheralDevice {
    id: string;
    displayName: string;
}