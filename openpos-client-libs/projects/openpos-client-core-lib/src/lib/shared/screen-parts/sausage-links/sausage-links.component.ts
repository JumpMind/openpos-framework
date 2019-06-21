import { ScreenPartComponent } from '../screen-part';
import { IActionItem } from '../../../core/interfaces/action-item.interface';
import { Component } from '@angular/core';
import { MessageProvider } from '../../providers/message.provider';
import { Configuration } from '../../../configuration/configuration';
import { ScreenPart } from '../../decorators/screen-part.decorator';
import { ISausageLinksInterface } from './sausage-links.interface';
import { INotificationItem } from '../../../core/interfaces/notification-item.interface';

@ScreenPart({
    name: 'sausageLinks'
})
@Component({
    selector: 'app-sausage-links',
    templateUrl: './sausage-links.component.html',
    styleUrls: ['./sausage-links.component.scss']
})
export class SausageLinksComponent extends ScreenPartComponent<IActionItem[] | ISausageLinksInterface> {
    sausageLinks: ISausageLinksInterface;

    constructor( messageProvider: MessageProvider ) {
        super(messageProvider);
    }

    screenDataUpdated() {
        if ( Array.isArray(this.screenData)) {
            this.sausageLinks = {links: this.screenData};
        } else {
            this.sausageLinks = this.screenData;
        }
    }

    keybindsEnabled() {
        return Configuration.enableKeybinds;
    }

    public getNotificationForLink(item: IActionItem): INotificationItem {
        if (typeof this.sausageLinks.notificationItems !== 'undefined' && this.sausageLinks.notificationItems) {
          return this.sausageLinks.notificationItems.find(i => i.id === item.action);
        }
        return null;
    }

}
