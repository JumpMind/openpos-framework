import { ILocationData } from './location-data.interface';
import { Observable } from 'rxjs/internal/Observable';

export interface ILocationProvider {
    getProviderName(): string;
    getCurrentLocation(): Observable<ILocationData>;
}
