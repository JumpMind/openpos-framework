import { Component, HostListener } from '@angular/core';
import { ObservableMedia } from '@angular/flex-layout';
import { IScreen } from '../../shared/components/dynamic-screen/screen.interface';
import { IActionItem } from '../../core/actions/action-item.interface';
import { SessionService } from '../../core/services/session.service';
import { PosScreen } from '../../screens-with-parts/pos-screen/pos-screen.component';

@Component({
  selector: 'app-self-checkout-home',
  templateUrl: './self-checkout-home.component.html',
  styleUrls: ['./self-checkout-home.component.scss']

})
export class SelfCheckoutHomeComponent extends PosScreen<any> {

    public menuItems: IActionItem[];
    private actionSent = false;

    @HostListener('document:click', [])
    @HostListener('document:touchstart', [])
    begin() {
        if (this.menuItems && this.menuItems.length > 0) {
        this.onMenuItemClick(this.menuItems[0]);
        }
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
