import { Component } from '@angular/core';
import { MessageProvider } from '../../../shared/providers/message.provider';

@Component({
    selector: 'app-dynamic-screen',
    templateUrl: './dynamic-screen.component.html',
    styleUrls: ['./dynamic-screen.component.scss'],
    providers: [MessageProvider]
})
export class DynamicScreenComponent {

    constructor(
                messageProvider: MessageProvider) {
        messageProvider.setMessageType('Screen');
    }
}
