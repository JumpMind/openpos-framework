import { Component } from '@angular/core';
import { ScreenPart } from '../../decorators/screen-part.decorator';
import { ScreenPartComponent } from '../screen-part';
import { NotificationsInterface } from './notifications.interface';

@ScreenPart({
    name: 'notifications'
})
@Component({
    selector: 'app-notifications',
    templateUrl: './notifications.component.html',
    styleUrls: ['./notifications.component.scss']
})
export class NotificationsComponent extends ScreenPartComponent<NotificationsInterface> {

    screenDataUpdated() {
    }

}