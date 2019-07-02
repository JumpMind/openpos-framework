import { LocaleConstantKey, LocaleConstants } from './locale.constants';
import { SessionService } from './session.service';
import { Injectable } from '@angular/core';

export const DEFAULT_LOCALE = 'en-US';

@Injectable({
    providedIn: 'root',
})
export class LocaleService {
    private locale = DEFAULT_LOCALE;
    private displayLocale = DEFAULT_LOCALE;
    private supportedLocales = ['en-US'];

    constructor(public sessionService: SessionService) {
        this.sessionService.getMessages('LocaleChanged').subscribe(message => this.handleLocaleChanged(message));
    }

    handleLocaleChanged(message: any) {
        if (message.locale) {
            this.locale = this.formatLocaleForBrowser(message.locale);
        }
        if (message.displayLocale) {
            this.displayLocale = this.formatLocaleForBrowser(message.displayLocale);
        }
        if (message.supportedLocales) {
            this.supportedLocales = message.supportedLocales.map(locale => this.formatLocaleForBrowser(locale));
        }
    }

    getLocale(): string {
        return this.locale;
    }

    getDisplayLocale(): string {
        return this.displayLocale;
    }

    getSupportedLocales(): string[] {
        return this.supportedLocales;
    }

    getConstant(key: LocaleConstantKey, locale?: string): any {
        let llocale = locale || this.locale;
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


