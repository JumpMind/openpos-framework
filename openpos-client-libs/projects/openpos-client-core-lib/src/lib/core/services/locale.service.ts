import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

import { LocaleConstantKey, LocaleConstants } from './locale.constants';
import { SessionService } from './session.service';
import { PersonalizationService } from '../personalization/personalization.service';
import { BehaviorSubject, Observable } from 'rxjs';
import { switchMap } from 'rxjs/operators';

export const DEFAULT_LOCALE = 'en-US';

@Injectable({
    providedIn: 'root',
})
export class LocaleService {
    private supportedLocales = ['en-US'];
    private showIcons = true;

    private locale$ = new BehaviorSubject<string>(DEFAULT_LOCALE);
    private displayLocale$ = new BehaviorSubject<string>(DEFAULT_LOCALE);

    constructor(
        public sessionService: SessionService,
        private http: HttpClient,
        private personalization: PersonalizationService
    ) {
        this.sessionService.getMessages('LocaleChanged').subscribe(message => this.handleLocaleChanged(message));
    }

    handleLocaleChanged(message: any) {
        if (message.locale) {
            this.locale$.next(this.formatLocaleForBrowser(message.locale));
        }
        if (message.displayLocale) {
            this.displayLocale$.next(this.formatLocaleForBrowser(message.displayLocale));
        }
        if (message.supportedLocales) {
            this.supportedLocales = message.supportedLocales.map(locale => this.formatLocaleForBrowser(locale));
        }
        if (message.hasOwnProperty('showIcons')) {
            this.showIcons = message.showIcons;
        }
    }

    getString(base: string, key: string, args?: any[]): Observable<string> {
        const url = `http${this.personalization.getSslEnabled$().getValue() ? 's' : ''}` +
                `://${this.personalization.getServerName$().getValue()}:${this.personalization.getServerPort$().getValue()}/i18n/value`;

        return this.displayLocale$.pipe(
            switchMap(l => this.http.post(url, {
                base,
                key,
                locale: this.formatLocaleForJava(l),
                args
            }, {
                responseType: 'text',
            }))
        );
    }

    getLocale(): string {
        return this.locale$.getValue();
    }

    getDisplayLocale(): string {
        return this.displayLocale$.getValue();
    }

    getSupportedLocales(): string[] {
        return this.supportedLocales;
    }

    isShowIcons() {
        return this.showIcons;
    }

    getConstant(key: LocaleConstantKey, locale?: string): any {
        let llocale = locale || this.getLocale();
        llocale = llocale ? llocale.toLowerCase() : null;
        if (LocaleConstants[key] && llocale) {
            return LocaleConstants[key][llocale];
        } else {
            return null;
        }
    }

    formatLocaleForBrowser(locale: string): string {
        if (locale) {
            return locale.replace('_', '-');
        } else {
            return null;
        }
    }

    formatLocaleForJava(locale: string): string {
        if (locale) {
            return locale.replace('-', '_');
        } else {
            return null;
        }
    }

}


