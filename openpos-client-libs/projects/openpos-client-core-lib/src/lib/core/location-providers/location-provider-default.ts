import { ILocationProvider } from './location-provider.interface';
import { ILocationData } from './location-data.interface';
import { Observable } from 'rxjs/internal/Observable';
import { Http } from '@angular/http';
import { Configuration } from '../../configuration/configuration';
import { Injectable, OnDestroy } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({
    providedIn: 'root'
})
export class LocationProviderDefault implements ILocationProvider, OnDestroy {

    prevLat: number;
    prevLong: number;
    watchId: number;

    private $locationData = new BehaviorSubject<ILocationData>(null);

    constructor(private http: Http) {
    }

    ngOnDestroy(): void {
        if (this.watchId) {
            navigator.geolocation.clearWatch(this.watchId);
        }
    }

    getProviderName(): string {
        return 'default';
    }

    getCurrentLocation(): Observable<ILocationData> {
        if (navigator.geolocation && Configuration.googleApiKey) {
            let zipCode = '';
            let  countryName = '';
            this.watchId = navigator.geolocation.watchPosition((position) => {
                const lat = position.coords.latitude;
                const long = position.coords.longitude;
                if (lat !== this.prevLat || long !== this.prevLong) {
                    this.prevLat = lat;
                    this.prevLong = long;
                    const latlong = lat + ',' + long;
                    this.reverseGeocode(Configuration.googleApiKey, latlong)
                        .then((response) => {
                            const address = response.results[0].address_components;
                            zipCode = address[address.length - 1].long_name;
                            countryName = address[address.length - 2].long_name;
                            this.$locationData.next({
                                type: 'default',
                                postalCode: zipCode,
                                country: countryName
                            } as ILocationData);
                        })
                        .catch((error) => console.log(error));
                }
            });
        }

        return this.$locationData;
    }

    async reverseGeocode(key: string, param: string): Promise<any> {
        try {
            const response = await this.http
                .get('https://maps.google.com/maps/api/geocode/json?key=' + key + '&latlng=' + param + '&sensor=false')
                .toPromise();
            return await Promise.resolve(response.json());
        } catch (error) {
            return await Promise.resolve(error.json());
        }
    }
}
