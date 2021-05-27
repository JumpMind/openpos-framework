import { Injectable } from '@angular/core';
import {from, of, Observable} from "rxjs";
import { Subject, BehaviorSubject } from 'rxjs';
import {Capacitor, DeviceInfo, Plugins} from "@capacitor/core";
import {map} from "rxjs/operators";


declare var cordova: any;

@Injectable({
    providedIn: 'root',
  })
export class CordovaService {
    public onDeviceReady: Subject<string> = new BehaviorSubject<string>(null);
    private _isRunningInCordova: boolean = null;
    public plugins: any;

    constructor() {
        document.addEventListener('deviceready', () => {
                console.info('Cordova devices are ready');
                this._isRunningInCordova = true;
                this.plugins = cordova.plugins;
                this.onDeviceReady.next(`deviceready`);
            },
            false
        );
    }

    public isRunningInCordova(): boolean {
        if (this._isRunningInCordova == null) {
            this._isRunningInCordova = typeof cordova !== 'undefined' && !this.isRunningInBrowser();
        }

        return this._isRunningInCordova;
    }

    public get cordova(): any {
        return this.isRunningInCordova() ? cordova : null;
    }

    public isPluginsAvailable() {
        return this.isRunningInCordova() && this.plugins;
    }

    public isRunningInBrowser(): boolean {
        const app = document.URL.indexOf('http://') === -1 && document.URL.indexOf('https://') === -1;
        return !app;
    }

    public getDeviceName(): Observable<string> {
        console.log(window['device']['serial']);
        return of(window['device']['serial']);
    }
}


