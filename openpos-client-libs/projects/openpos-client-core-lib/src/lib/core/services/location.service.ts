import { SessionService } from './session.service';
import { filter } from 'rxjs/operators';
import { Optional, Inject, InjectionToken, OnDestroy, Injectable } from '@angular/core';
import { ILocationProvider } from '../location-providers/location-provider.interface';
import { Subscription } from 'rxjs/internal/Subscription';
import { ILocationData } from '../location-providers/location-data.interface';

export const PROVIDERS = new InjectionToken<ILocationProvider[]>('LocationProviders');
@Injectable({
    providedIn: 'root',
})
export class LocationService implements OnDestroy {

    subscription: Subscription;
    previousLocationData: ILocationData;

    constructor(public sessionService: SessionService,
                @Optional() @Inject(PROVIDERS) private locationProviders: Array<ILocationProvider>) {
        sessionService.getMessages('ConfigChanged').pipe(
            filter( m => m.configType === 'LocationService')
        ).subscribe( message => {
            if (message.enabled === 'true') {
                let provider = locationProviders.find( l => l.getProviderName() === message.provider );
                if (provider === undefined || provider === null) {
                    provider = locationProviders.find(l => l.getProviderName() === 'default');
                }
                if (this.subscription) {
                    this.subscription.unsubscribe();
                }
                this.subscription = provider.getCurrentLocation(message.coordinateBuffer ? message.coordinateBuffer : 0)
                .subscribe((locationData: ILocationData) => {
                    if (!this.previousLocationData ||
                        (this.previousLocationData && locationData && this.previousLocationData.postalCode !== locationData.postalCode)) {
                        this.previousLocationData = locationData;
                        if (locationData && locationData.postalCode && locationData.country) {
                            sessionService.onAction('LocationChanged', locationData);
                        }
                    }
                });
            }
        });
    }

    ngOnDestroy(): void {
        if (this.subscription) {
            this.subscription.unsubscribe();
        }
    }

}
