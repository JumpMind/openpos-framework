import { ILocationProvider } from './location-provider.interface';
import { ILocationData } from './location-data.interface';
import { Observable } from 'rxjs/internal/Observable';
import { Http } from '@angular/http';
import { Configuration } from '../../configuration/configuration';
import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({
    providedIn: 'root'
})
export class LocationProviderDefault implements ILocationProvider {

    prevLat: number;
    prevLong: number;

    private $locationData = new BehaviorSubject<ILocationData>(null);

    constructor(private http: Http) {
    }

    getProviderName(): string {
        return 'default';
    }

    getCurrentLocation(coordinateBuffer: number): Observable<ILocationData> {
        if (navigator.geolocation && Configuration.googleApiKey) {
            let zipCode = '';
            let  countryName = '';
            const previous = {latitude: 0, longitude: 0};
            const buffer = coordinateBuffer;
            navigator.geolocation.watchPosition((position) => {
                const lat = position.coords.latitude;
                const long = position.coords.longitude;
                if (lat > previous.latitude + buffer || lat < previous.latitude - buffer
                    || long > previous.longitude + buffer || long < previous.longitude - buffer) {
                    previous.latitude = lat;
                    previous.longitude = long;
                    const latlong = lat + ',' + long;
                    console.log('calling google maps geocode api');
                    this.reverseGeocode(Configuration.googleApiKey, latlong)
                        .then((response) => {
                            console.log(response.results[0].address_components);
                            for (const addressComponent of response.results[0].address_components) {
                                for (const type of addressComponent.types) {
                                    if (type === 'postal_code') {
                                        zipCode = addressComponent.long_name;
                                    }
                                    if (type === 'country') {
                                        countryName = addressComponent.long_name;
                                    }
                                }
                            }

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
