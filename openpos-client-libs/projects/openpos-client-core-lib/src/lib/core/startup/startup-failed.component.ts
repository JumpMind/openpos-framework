import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA } from '@angular/material';

@Component({
    templateUrl: './startup-failed.component.html',
    styleUrls: ['startup-failed.component.scss']
})
export class StartupFailedComponent {

    title = 'Startup Failed ...';
    error: string;
    messages: string[];
    protected _appReloadOnCloseEnabled = true;

    constructor( @Inject(MAT_DIALOG_DATA) public data: any) {
        this.error = data.error;
        this.messages = data.messages;
    }

    get appReloadOnCloseEnabled(): boolean { return this._appReloadOnCloseEnabled; }
    set appReloadOnCloseEnabled(enabled: boolean) { this._appReloadOnCloseEnabled = enabled; }
}
