import { InjectionToken } from '@angular/core';
import { Observable } from "rxjs";

export const STORAGE_CONTAINERS = new InjectionToken<StorageContainer[]>('Sto≥rageContainers');

/**
 * An abstraction of a storage container.
 */
export interface StorageContainer {
    /**
     * Gets the name of the container.
     */
    name(): string;

    /**
     * Weather or not the container is supported.
     */
    isSupported(): boolean;

    /**
     * Gets an observable that provides the current value of the stored key,
     * and then completes. If there is no associated value for the specified
     * key, then `undefined` is provided through the stream.
     * 
     * @param key The unique key used to access the value.
     */
    getValue(key: string): Observable<string | undefined>;

    /**
     * Assigns the value of the specified key to the specified value. Returns
     * an Observable that completes when successfully finished.
     * 
     * @param key The unique key used to access the value.
     * @param value The new value.
     */
    setValue(key: string, value: string): Observable<void>;

    /**
     * Removes the specified key and its associated value from storage. Returns
     * an Observable that completes when successfully finished. If the key is
     * not present, it still completes successfully as if it were present.
     * 
     * @param key The unique key used to access the value.
     */
    remove(key: string): Observable<void>;
}
