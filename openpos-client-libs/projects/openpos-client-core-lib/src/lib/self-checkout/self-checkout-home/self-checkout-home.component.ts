import { Component, Injector, HostListener } from '@angular/core';

import { IActionItem } from '../../core/actions/action-item.interface';
import { PosScreen } from '../../screens-with-parts/pos-screen/pos-screen.component';
import { ScreenComponent } from '../../shared/decorators/screen-component.decorator';
import { MatDialog } from '@angular/material';
import { DEFAULT_LOCALE, LocaleService } from '../../core/services/locale.service';

@ScreenComponent({
    name: 'SelfCheckoutHome'
})
@Component({
    selector: 'app-self-checkout-home',
    templateUrl: './self-checkout-home.component.html',
    styleUrls: ['./self-checkout-home.component.scss']

})
export class SelfCheckoutHomeComponent extends PosScreen<any> {

    public menuItems: IActionItem[];
    
    private actionSent = false;

    private currentLocale = DEFAULT_LOCALE;

    private locales: string[];
    public languages: any[] = [];

    constructor(injector: Injector, private localeService: LocaleService ) {
        super(injector);
        this.locales = localeService.getSupportedLocales();
        this.currentLocale = localeService.getLocale();
        this.locales.forEach(loc => {
            if (loc !== DEFAULT_LOCALE) {
                this.languages.push({ locale: loc, icon: this.getIcon(loc), displayName: this.getDisplayName(loc) });
            }
        });
    }

    @HostListener('document:click', [])
    begin() {
        this.doAction(this.screen.action, this.currentLocale);
    }

    buildScreen() {
        this.actionSent = false;
        this.currentLocale = DEFAULT_LOCALE;
        this.menuItems = this.screen.menuItems;
    }

    onEnter(value: string) {
        this.doAction('Save');
    }

    onLocaleSelected(value: string) {
        this.currentLocale = value;
    }

    getClass(): string {
        // return 'main-menu-grid-list';
        return 'foo';
    }

    onMenuItemClick(menuItem: IActionItem) {
        if (!this.actionSent) {
            this.doAction(menuItem);
            this.actionSent = true;
        }
    }

    public getDisplayName(locale: string): string {
        return this.localeService.getConstant('displayName', locale);
    }

    public getIcon(locale: string): string {
        return this.localeService.getConstant('localeIcon', locale);
    }

    public getSelectedIcon(): string {
        return this.localeService.getConstant('localeIcon', this.currentLocale);
    }
}
