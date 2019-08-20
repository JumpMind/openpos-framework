import { Component } from '@angular/core';
import { LocaleService } from '../../../core/services/locale.service';
import { SessionService } from '../../../core/services/session.service';
import { ActionService } from '../../../core/actions/action.service';


@Component({
    selector: 'app-language-selector',
    templateUrl: './language-selector.component.html',
    styleUrls: ['./language-selector.component.scss']
})
export class LanguageSelectorComponent {

    selectedLocale: string;
    selectedIcon: string;

    locales: string[];
    languages = [];
    showIcons = true;

    constructor(public sessionService: SessionService, public localeService: LocaleService, public actionService: ActionService) {
        this.selectedLocale = localeService.getDisplayLocale();
        this.selectedIcon = this.getIcon(this.selectedLocale);

        this.locales = localeService.getSupportedLocales();
        this.locales.forEach(loc => {
            this.languages.push({ locale: loc, icon: this.getIcon(loc), displayName: this.getDisplayName(loc) });
        });

        this.showIcons = localeService.isShowIcons();
    }

    screenDataUpdated() {
    }

    public onLanguageSelected() {
        this.selectedIcon = this.getIcon(this.selectedLocale);
        this.actionService.doAction({action: 'LocaleSelected'}, this.localeService.formatLocaleForJava(this.selectedLocale));
    }

    public getDisplayName(locale: string): string {
        return this.localeService.getConstant('displayName', locale);
    }

    public getIcon(locale: string): string {
        return this.localeService.getConstant('localeIcon', locale);
    }

    public getSelectedIcon(): string {
        return this.localeService.getConstant('localeIcon', this.selectedLocale);
    }

}
