import { Component, HostListener } from '@angular/core';
import { ObservableMedia } from '@angular/flex-layout';
import { IScreen } from '../../shared/components/dynamic-screen/screen.interface';
import { IActionItem } from '../../core/interfaces/action-item.interface';
import { SessionService } from '../../core/services/session.service';

@Component({
  selector: 'app-self-checkout-home',
  templateUrl: './self-checkout-home.component.html',
  styleUrls: ['./self-checkout-home.component.scss']

})
export class SelfCheckoutHomeComponent implements IScreen {

  screen: any;
  public menuItems: IActionItem[];
  private actionSent = false;

  constructor(public session: SessionService, public media: ObservableMedia) {
  }

  @HostListener('document:click', [])
  @HostListener('document:touchstart', [])
  begin() {
    if (this.menuItems && this.menuItems.length > 0) {
      this.onMenuItemClick(this.menuItems[0]);
    }
  }

  show(screen: any) {
    this.actionSent = false;
    this.screen = screen;
    this.menuItems = screen.menuItems;
  }

  onEnter(value: string) {
    this.session.onAction('Save');
  }

  getClass(): String {
    // return 'main-menu-grid-list';
    return 'foo';
  }

  onMenuItemClick(menuItem: IActionItem) {
    if (!this.actionSent) {
      this.session.onAction(menuItem);
      this.actionSent = true;
    }
  }
}
