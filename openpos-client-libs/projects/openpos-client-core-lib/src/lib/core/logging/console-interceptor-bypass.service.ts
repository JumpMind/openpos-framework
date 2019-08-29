import { Injectable } from '@angular/core';
import { Subject, Observable } from 'rxjs';

@Injectable({
    providedIn: 'root'
})
export class ConsoleInterceptorBypassService {

    private messages$ = new Subject<{method: string, message: string}>();

    public log( message: string) {
        this.messages$.next({method: 'log', message});
    }
    public info( message: string) {
        this.messages$.next({method: 'info', message});
    }
    public error( message: string) {
        this.messages$.next({method: 'error', message});
    }
    public warn( message: string) {
        this.messages$.next({method: 'warn', message});
    }
    public debug( message: string) {
        this.messages$.next({method: 'debug', message});
    }

    public getMessages$(): Observable<{method: string, message: string}> {
        return this.messages$;
    }
}
