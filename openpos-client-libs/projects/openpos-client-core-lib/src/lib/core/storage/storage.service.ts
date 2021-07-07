import { Inject, Injectable, Optional } from '@angular/core';
import { Observable, of } from 'rxjs';
import { switchMap, take } from 'rxjs/operators';
import { LocalStorageService } from './local-storage/local-storage.service';
import { STORAGE_CONTAINERS, StorageContainer } from './storage-container';

@Injectable({
    providedIn: 'root'
})
export class Storage {
    private readonly container$: Observable<StorageContainer>

    constructor(
        @Inject(STORAGE_CONTAINERS) @Optional() containers: StorageContainer[],

        // local storage should always be available as a fallback...
        localStorage: LocalStorageService
    ) {
        let container: StorageContainer = localStorage;

        if (containers) {
            const supportedContainers = containers.filter(c => c.isSupported());
            container = supportedContainers.length > 0 ? containers[0] : localStorage;
        }

        console.log(`using storage container: ${container.name()}`);
        this.container$ = of(container);
    }

    getValue(key: string): Observable<string> {
        return this.container$.pipe(
            take(1),
            switchMap(container => container.getValue(key))
        );
    }

    setValue(key: string, value: string): Observable<void> {
        return this.container$.pipe(
            take(1),
            switchMap(container => container.setValue(key, value))
        );
    }

    remove(key: string): Observable<void> {
        return this.container$.pipe(
            take(1),
            switchMap(container => container.remove(key))
        );
    }
}