import {Component, OnInit, ViewChild} from '@angular/core';
import {ActionService} from '../../../core/actions/action.service';
import { MessageProvider } from '../../providers/message.provider';
import {ToastContainerDirective, ToastrService} from "ngx-toastr";

@Component({
    selector: 'app-dynamic-screen',
    templateUrl: './dynamic-screen.component.html',
    styleUrls: ['./dynamic-screen.component.scss'],
    providers: [MessageProvider, ActionService]
})
export class DynamicScreenComponent implements OnInit{
    @ViewChild(ToastContainerDirective)
    toastContainer: ToastContainerDirective;
    constructor(
                messageProvider: MessageProvider,
                private toastrService: ToastrService) {
        messageProvider.setMessageType('Screen');
    }

    ngOnInit() {
        this.toastrService.overlayContainer = this.toastContainer;
    }
}
