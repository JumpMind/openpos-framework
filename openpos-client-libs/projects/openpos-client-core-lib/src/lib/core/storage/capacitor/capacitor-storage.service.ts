import { Injectable } from '@angular/core';

import { Capacitor, Plugins as CapacitorPlugins } from '@capacitor/core';

import { from, Observable, of } from 'rxjs';
import { map } from 'rxjs/operators';
import { StorageContainer } from '../storage-container';

const { Storage } = CapacitorPlugins;

@Injectable({
    providedIn: 'root'
})
export class CapacitorStorageService implements StorageContainer {
    name(): string {
        return 'cap-storage';
    }
    
    isSupported(): boolean {
        return Capacitor.isNative && Capacitor.isPluginAvailable('Storage');
    }

    initialize(): Observable<string> {
        return of('initialized Capacitor storage plugin');
    }

    getValue(key: string): Observable<string> {
        return from(Storage.get({key})).pipe(
            map(result => result.value)
        );
    }

    setValue(key: string, value: string): Observable<void> {
        return from(Storage.set({key, value}));
    }
    
    remove(key: string): Observable<void> {
        return from(Storage.remove({key}));
    }
}
