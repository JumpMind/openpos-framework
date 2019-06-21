import { ILocationData } from './location-data.interface';
import { Observable } from 'rxjs/internal/Observable';

export interface ILocationProvider {
    getProviderName(): string;
    getCurrentLocation(coordinateBuffer: number): Observable<ILocationData>;
}
