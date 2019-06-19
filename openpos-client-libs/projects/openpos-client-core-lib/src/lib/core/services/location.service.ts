import { SessionService } from './session.service';
import { filter } from 'rxjs/operators';
import { Optional, Inject, InjectionToken, OnDestroy, Injectable } from '@angular/core';
import { ILocationProvider } from '../location-providers/location-provider.interface';
import { Subscription } from 'rxjs/internal/Subscription';
import { ILocationData } from '../location-providers/location-data.interface';

export const SCANNERS = new InjectionToken<ILocationProvider[]>('LocationProviders');

@Injectable()
export class LocationService implements OnDestroy {

    subscription: Subscription;
    previousLocationData: ILocationData;

    constructor(public sessionService: SessionService,  @Optional() @Inject(SCANNERS) private locationProviders: Array<ILocationProvider>) {
        sessionService.getMessages('ConfigChanged').pipe(
            filter( m => m.configType === 'LocationService')
        ).subscribe( message => {
            if (message.enabled) {
                let provider = locationProviders.find( l => l.getProviderName() === message.provider );
                if (provider === undefined || provider === null) {
                    provider = locationProviders.find(l => l.getProviderName() === 'default');
                }
                if (this.subscription) {
                    this.subscription.unsubscribe();
                }
                this.subscription = provider.getCurrentLocation().subscribe((locationData: ILocationData) => {
                    if (this.previousLocationData === undefined || this.previousLocationData.postalCode !== locationData.postalCode) {
                        this.previousLocationData = locationData;
                        sessionService.onAction('LocationChanged', locationData);
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
