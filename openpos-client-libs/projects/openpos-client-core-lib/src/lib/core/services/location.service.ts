import { SessionService } from './session.service';
import { filter, map } from 'rxjs/operators';
import { Optional, Inject, InjectionToken, OnDestroy, Injectable } from '@angular/core';
import { ILocationProvider } from '../location-providers/location-provider.interface';
import { Subscription } from 'rxjs/internal/Subscription';
import { ILocationData } from '../location-providers/location-data.interface';
import { BehaviorSubject, Observable } from 'rxjs';

export const PROVIDERS = new InjectionToken<ILocationProvider[]>('LocationProviders');
@Injectable({
    providedIn: 'root',
})
export class LocationService implements OnDestroy {

    subscription: Subscription;
    $data = new BehaviorSubject<ILocationData>(null);
    previousLocationData: ILocationData;
    manualOverride = false;
    availableCountries: string[];

    constructor(public sessionService: SessionService,
                @Optional() @Inject(PROVIDERS) private locationProviders: Array<ILocationProvider>) {

        sessionService.getMessages('ConfigChanged').pipe(
            filter( m => m.configType === 'LocationService')
        ).subscribe( message => {
            if (message.enabled === 'true') {
                if (message.countries) {
                    this.availableCountries = message.countries.split(',');
                }
                let provider = locationProviders.find( l => l.getProviderName() === message.provider );
                if (provider === undefined || provider === null) {
                    provider = locationProviders.find(l => l.getProviderName() === 'default');
                }
                if (this.subscription) {
                    this.subscription.unsubscribe();
                }
                this.subscription = provider.getCurrentLocation(message.coordinateBuffer ? message.coordinateBuffer : 0)
                .subscribe((locationData: ILocationData) => {
                    if (!this.manualOverride && this.isPreviousLocationDataDifferent(locationData)) {
                        this.$data.next(locationData);
                        this.previousLocationData = locationData;
                        if (locationData && locationData.postalCode && locationData.country) {
                            sessionService.onValueChange('LocationChanged', locationData);
                        }
                    }
                });
            }
        });
    }

    isPreviousLocationDataDifferent(locationData: ILocationData): boolean {
        return !this.previousLocationData
            || (this.previousLocationData && locationData && (this.previousLocationData.postalCode !== locationData.postalCode
                || this.previousLocationData.country !== locationData.country));
    }

    hasManualOverride(): boolean {
        return this.manualOverride;
    }

    getAvailableCountries(): string[] {
        return this.availableCountries;
    }

    getPostalCode(): Observable<string> {
        return this.$data.pipe(map(l => l === null ? null : l.postalCode));
    }

    setLocationData(locationData: ILocationData): void {
        if (locationData && locationData.postalCode && locationData.country) {
            this.manualOverride = true;
            this.$data.next(locationData);
            this.previousLocationData = locationData;
            this.sessionService.onAction('LocationChanged', locationData);
        } else {
            this.manualOverride = false;
        }
    }

    ngOnDestroy(): void {
        if (this.subscription) {
            this.subscription.unsubscribe();
        }
    }

}
