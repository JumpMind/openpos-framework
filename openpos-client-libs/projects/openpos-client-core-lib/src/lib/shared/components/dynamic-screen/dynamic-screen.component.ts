import {Component, OnInit, ViewChild} from '@angular/core';
import {ActionService} from '../../../core/actions/action.service';
import { MessageProvider } from '../../providers/message.provider';
import {ToastContainerDirective, ToastrService} from "ngx-toastr";
import { SessionService } from '../../../core/services/session.service';
import { WatermarkMessage } from '../../../core/messages/watermark-message';
import { MessageTypes } from '../../../core/messages/message-types';
import { ConfigurationService } from '../../../core/services/configuration.service';
import { Observable } from 'rxjs';
import { PrinterService } from '../../../core/platform-plugins/printers/printer.service';

@Component({
    selector: 'app-dynamic-screen',
    templateUrl: './dynamic-screen.component.html',
    styleUrls: ['./dynamic-screen.component.scss'],
    providers: [MessageProvider, ActionService, PrinterService]
})
export class DynamicScreenComponent implements OnInit{
    @ViewChild(ToastContainerDirective)
    toastContainer: ToastContainerDirective;
    showWatermark = false;
    watermarkMessage: string;
    showStatusBar = true;
    constructor(
        private configuration: ConfigurationService,
        messageProvider: MessageProvider,
        private toastrService: ToastrService,
        private sessionService: SessionService,
        printService: PrinterService
    ) {
        messageProvider.setMessageType('Screen');
        this.sessionService.getMessages(MessageTypes.WATERMARK).subscribe((message: WatermarkMessage) => {
            this.showWatermark = true;
            this.watermarkMessage = message.screenMessage;
        });
        this.sessionService.getMessages(MessageTypes.HIDE_WATERMARK).subscribe(() => {
            this.showWatermark = false;
            this.watermarkMessage = '';
        });
        this.sessionService.getMessages(MessageTypes.STATUS_BAR).subscribe(() => this.showStatusBar = true);
        this.sessionService.getMessages(MessageTypes.HIDE_STATUS_BAR).subscribe(() => this.showStatusBar = false);
    }

    ngOnInit() {
        this.toastrService.overlayContainer = this.toastContainer;
    }

    public getLocalTheme(): Observable<string> {
        return this.configuration.theme$;
    }
}
