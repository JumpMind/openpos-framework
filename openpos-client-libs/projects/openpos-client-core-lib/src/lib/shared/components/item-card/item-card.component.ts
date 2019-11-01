import { Component, Input, HostListener } from '@angular/core';
import { ISellItem } from '../../../core/interfaces/sell-item.interface';
import { SessionService } from '../../../core/services/session.service';
import { IActionItem } from '../../../core/actions/action-item.interface';
import { ActionService } from '../../../core/actions/action.service';


@Component({
  selector: 'app-item-card',
  templateUrl: './item-card.component.html',
  styleUrls: ['./item-card.component.scss']
})
export class ItemCardComponent {

  @Input() item: ISellItem;
  @Input() isReadOnly = false;
  @Input() expanded = true;
  @Input() enableHover = true;

  public hover = false;

  constructor(public actionService: ActionService, public session: SessionService) { }

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
