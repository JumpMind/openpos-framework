import {Component, OnInit, ViewChild} from '@angular/core';
import {ActionService} from '../../../core/actions/action.service';
import { MessageProvider } from '../../providers/message.provider';
import {ToastContainerDirective, ToastrService} from "ngx-toastr";
import { SessionService } from '../../../core/services/session.service';
import { WatermarkMessage } from '../../../core/messages/watermark-message';
import { MessageTypes } from '../../../core/messages/message-types';

@Component({
    selector: 'app-dynamic-screen',
    templateUrl: './dynamic-screen.component.html',
    styleUrls: ['./dynamic-screen.component.scss'],
    providers: [MessageProvider, ActionService]
})
export class DynamicScreenComponent implements OnInit{
    @ViewChild(ToastContainerDirective)
    toastContainer: ToastContainerDirective;
    showWatermark = false;
    watermarkMessage: string;
    constructor(
                messageProvider: MessageProvider,
                private toastrService: ToastrService,
                private sessionService: SessionService) {
        messageProvider.setMessageType('Screen');
        this.sessionService.getMessages(MessageTypes.WATERMARK).subscribe((message: WatermarkMessage) => {
            this.showWatermark = true;
            this.watermarkMessage = message.screenMessage;
        });
        this.sessionService.getMessages(MessageTypes.HIDE_WATERMARK).subscribe(() => {
            this.showWatermark = false;
            this.watermarkMessage = '';
        });
    }

    ngOnInit() {
        this.toastrService.overlayContainer = this.toastContainer;
    }
}
