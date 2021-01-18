import { Injectable } from '@angular/core';
import { Observable, ConnectableObservable } from 'rxjs';
import { filter, map, publishBehavior, tap } from 'rxjs/operators';

import { MessageTypes } from '../messages/message-types';
import { SessionService } from '../services/session.service';
import { PeripheralDeviceSelectionMessage } from '../messages/peripheral-device-selection';
import { ActionMessage } from '../messages/action-message';

@Injectable({providedIn: 'root'})
export class PeriphealSelectionService {
    readonly periphealCategories$: Observable<PeriphealCategory[]>;

    private _categoryNameToData = new Map<string, PeriphealCategory>();

    constructor(
        private session: SessionService
    ) {
        const connectable = session.getMessages(MessageTypes.PERIPHERAL_DEVICE_SELECTION).pipe(
            map(m => m as PeripheralDeviceSelectionMessage),
            filter(m => !!m),
            map(m => {
                const devices = m.available;

                return <PeriphealCategory> {
                    name: m.categoryDisplayName,
                    knownDevices: devices,
                    selectedDevice: devices.find(d => d.id === m.selectedId)
                };
            }),
            tap(n => this._categoryNameToData.set(n.name, n)),
            map(() => Array.from(this._categoryNameToData.values())),
            publishBehavior([])
        ) as ConnectableObservable<PeriphealCategory[]>;

        // make it hot...
        connectable.connect();

        this.periphealCategories$ = connectable;
    }

    selectDevice(category: PeriphealCategoryRef, device: PeriphealDeviceRef) {
        let categoryName: string;
        let deviceId: string;

        if (typeof(category) == 'string') {
            categoryName = category as string;
        } else {
            categoryName = (category as PeriphealCategory).name;
        }

        if (typeof(device) == 'string') {
            deviceId = device as string;
        } else {
            deviceId = (device as PeriphealDevice).id;
        }

        this.session.sendMessage(new ActionMessage('SelectPeripheral', true, { category: categoryName, id: deviceId }));
    }
}

export type PeriphealCategoryRef = PeriphealCategory | string;
export interface PeriphealCategory {
    name: string;
    knownDevices: PeriphealDevice[];
    selectedDevice: PeriphealDevice;
}

export type PeriphealDeviceRef = PeriphealDevice | string;
export interface PeriphealDevice {
    id: string;
    displayName: string;
}