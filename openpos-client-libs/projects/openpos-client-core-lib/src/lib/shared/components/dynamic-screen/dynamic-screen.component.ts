import { Component } from '@angular/core';
import { ActionService } from '../../../core/actions/action.service';
import { MessageProvider } from '../../providers/message.provider';

@Component({
    selector: 'app-dynamic-screen',
    templateUrl: './dynamic-screen.component.html',
    styleUrls: ['./dynamic-screen.component.scss'],
    providers: [MessageProvider, ActionService]
})
export class DynamicScreenComponent {

    constructor(
                messageProvider: MessageProvider) {
        messageProvider.setMessageType('Screen');
    }
}
