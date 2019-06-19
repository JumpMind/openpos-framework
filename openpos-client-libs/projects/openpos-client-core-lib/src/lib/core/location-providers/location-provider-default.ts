import { ILocationProvider } from './location-provider.interface';
import { ILocationData } from './location-data.interface';
import { Observable } from 'rxjs/internal/Observable';

export class LocationProviderDefault implements ILocationProvider {

    getProviderName(): string {
        return 'default';
    }

    getCurrentLocation(): Observable<ILocationData> {
        if (navigator.geolocation) {
            let zipCode = '';
            navigator.geolocation.watchPosition((position) => {
                const geocoder = require('geocoder');
                geocoder.reverseGeocode(position.coords.latitude, position.coords.longitude, (err, data) => {
                    const address = data[0].address_components;
                    zipCode = address[address.length - 1].long_name;
                });
            });
            return new Observable<ILocationData>((observer) => {
                observer.next({
                    type: 'default',
                    postalCode: zipCode
                } as ILocationData);
            });
        }
        return null;
    }

}
