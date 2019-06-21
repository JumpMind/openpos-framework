import { IPlatformInterface } from './platform.interface';
import { Observable, fromEvent } from 'rxjs';
import { take, map, timeout } from 'rxjs/operators';
import { Injectable } from '@angular/core';

@Injectable({
    providedIn: 'root'
})
export class CordovaPlatform implements IPlatformInterface {

    getName(): string {
        return 'cordova';
    }

    platformPresent(): boolean {
        return !!window.hasOwnProperty('cordova');
    }

    platformReady(): Observable<string> {
        return fromEvent(document, 'deviceready').pipe(
                map( e => 'device ready'),
                take(1)
            );
    }

}
