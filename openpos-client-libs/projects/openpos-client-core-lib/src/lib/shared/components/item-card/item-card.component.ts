import { Component, Input, HostListener, ViewChild, ElementRef } from '@angular/core';
import { ISellItem } from '../../../core/interfaces/sell-item.interface';
import { SessionService } from '../../../core/services/session.service';
import { IActionItem } from '../../../core/actions/action-item.interface';
import { ActionService } from '../../../core/actions/action.service';
import { Subscription } from 'rxjs';
import { Configuration } from '../../../configuration/configuration';
import { KeyPressProvider } from '../../providers/keypress.provider';
import { KeyboardClassKey } from '../../../keyboard/enums/keyboard-class-key.enum';
import { KebabLabelButtonComponent } from '../kebab-label-button/kebab-label-button.component';


@Component({
  selector: 'app-item-card',
  templateUrl: './item-card.component.html',
  styleUrls: ['./item-card.component.scss']
})
export class ItemCardComponent {

  @Input() item: ISellItem;
  @Input() isReadOnly = false;

  _expanded = true;
  @Input('expanded')
  set expanded(expanded: boolean) {
    this._expanded = expanded;
    if (!this._expanded && this.buttonSubscription) {
      this.buttonSubscription.unsubscribe();
    } else if (this._expanded) {
      this.createSubscription();
    }
  }
  get expanded() {
    return this._expanded;
  }

  @Input() enableHover = true;

  public hover = false;

  @ViewChild('kebab') kebab: KebabLabelButtonComponent;
  buttonSubscription: Subscription;

  constructor(public actionService: ActionService, public session: SessionService,  protected keyPresses: KeyPressProvider) {
    this.createSubscription();
  }

  public createSubscription() {
    this.buttonSubscription = this.keyPresses.subscribe( KeyboardClassKey.Space, 1, (event: KeyboardEvent) => {
      // ignore repeats and check configuration
      if ( event.repeat || event.type !== 'keydown' || !Configuration.enableKeybinds) {
        return;
      }
      if ( event.type === 'keydown' && this.expanded) {
        if (this.item.menuItems.length > 1) {
          this.kebab.openKebabMenu();
        } else {
          this.doItemAction(this.item.menuItems[0], this.item.index);
        }
      }
    });
  }

  public doItemAction(action: IActionItem, payload: number) {
    this.actionService.doAction(action, [payload]);
  }

  public isMenuItemEnabled(m: IActionItem): boolean {
    let enabled = m.enabled;
    if (m.action.startsWith('<') && this.session.isRunningInBrowser()) {
      enabled = false;
    }
    return enabled;
  }

  @HostListener('mouseenter')
  onMouseEnter() {
    if (this.enableHover) {
      this.hover = true;
    }
  }

  @HostListener('mouseleave')
  onMouseLeave() {
    this.hover = false;
  }

}
