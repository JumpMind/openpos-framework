import { Inject, Injectable, Optional } from '@angular/core';
import { Observable } from 'rxjs';
import { LocalStorageService } from './local-storage/local-storage.service';
import { STORAGE_CONTAINERS, StorageContainer } from './storage-container';

@Injectable({
    providedIn: 'root'
})
export class Storage {
    private readonly container: StorageContainer;

    constructor(
        @Inject(STORAGE_CONTAINERS) @Optional() containers: StorageContainer[],
        // local storage should always be available as a fallback...
        localStorage: LocalStorageService
    ) {
        this.container = localStorage;

        if (containers) {
            const supportedContainers = containers.filter(c => c.isSupported());
            this.container = supportedContainers.length > 0 ? containers[0] : localStorage;
        }

        console.log(`using storage container: ${this.container.name()}`);
    }

    getValue(key: string): Observable<string> {
        return this.container.getValue(key);
    }

    setValue(key: string, value: string): Observable<void> {
        return this.container.setValue(key, value);
    }

    remove(key: string): Observable<void> {
        return this.container.remove(key);
    }
}