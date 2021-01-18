import { Component, OnInit, ViewChild } from '@angular/core';
import { ActionService } from '../../../core/actions/action.service';
import { MessageProvider } from '../../providers/message.provider';
import { ToastContainerDirective, ToastrService } from 'ngx-toastr';
import { SessionService } from '../../../core/services/session.service';
import { OpenposMessage } from '../../../core/messages/message';

@Component({
    selector: 'app-dynamic-screen',
    templateUrl: './dynamic-screen.component.html',
    styleUrls: ['./dynamic-screen.component.scss'],
    providers: [MessageProvider, ActionService]
})
export class DynamicScreenComponent implements OnInit {
    @ViewChild(ToastContainerDirective)
    toastContainer: ToastContainerDirective;
    showWatermark = false;
    watermarkMessage: string;
    constructor(
        private messageProvider: MessageProvider,
        private toastrService: ToastrService,
        private sessionService: SessionService
    ) {
        this.messageProvider.setMessageType('Screen');
        this.sessionService.getMessages('Watermark').subscribe((message: OpenposMessage) => {
            this.showWatermark = message?.showWatermark;
            this.watermarkMessage = message?.screenMessage;
        });
    }

    ngOnInit() {
        this.toastrService.overlayContainer = this.toastContainer;
    }
}
