import { Pipe, PipeTransform, OnDestroy, ChangeDetectorRef } from '@angular/core';

import { LocaleService } from '../../core/services/locale.service';
import { Subscription } from 'rxjs';

@Pipe({
    name: 'localize',
    pure: false
})
export class LocalizePipe implements PipeTransform, OnDestroy {
    private _hasProcessed = false;
    private _subscription?: Subscription;
    private _latestValue = '';

    constructor(
        private _locale: LocaleService,
        private _ref: ChangeDetectorRef
    ){}

    ngOnDestroy() {
        if (this._subscription) {
            this._subscription.unsubscribe();
            this._subscription = undefined;
            this._hasProcessed = false;
        }
    }

    transform(value?: string, ...args: any[]): any {
        if (this._hasProcessed) {
            return this._latestValue;
        }

        if (!value || value.length <= 0) {
            console.error('cannot localize string, no key provided');
            return this._latestValue = '';
        }

        const split = value.split(':');
        
        if (split.length != 2) {
            console.error('invalid localization key specified, cannot localize (expecting `base:key` format)');
            return this._latestValue = value;
        }

        this._hasProcessed = true;
        this._locale.getString(split[0], split[1], args).subscribe(
            s => this._latestValue = s
        );

        return this._latestValue;
    }
}
