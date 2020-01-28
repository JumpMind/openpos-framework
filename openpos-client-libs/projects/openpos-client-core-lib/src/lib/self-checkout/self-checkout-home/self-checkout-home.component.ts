import { Component, Injector, HostListener } from '@angular/core';

import { IActionItem } from '../../core/actions/action-item.interface';
import { PosScreen } from '../../screens-with-parts/pos-screen/pos-screen.component';
import { ScreenComponent } from '../../shared/decorators/screen-component.decorator';

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

    constructor(injector: Injector ) {
        super(injector);
    }

    @HostListener('document:click', [])
    @HostListener('document:touchstart', [])
    begin() {
        this.doAction(this.screen.action);
    }

    buildScreen() {
        this.actionSent = false;
        this.menuItems = this.screen.menuItems;
    }

    onEnter(value: string) {
        this.doAction('Save');
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
}
