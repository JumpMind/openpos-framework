import { Observable } from 'rxjs';

export interface IPlatformInterface {
    getName(): string;
    platformPresent(): boolean;
    platformReady(): Observable<string>;
}
