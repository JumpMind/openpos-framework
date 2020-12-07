import {Injectable} from '@angular/core';
import {SessionService} from './session.service';
import {IToastScreen, ToastType} from '../interfaces/toast-screen.interface';
import {ToastrService} from 'ngx-toastr';

@Injectable({
    providedIn: 'root',
  })
export class ToastService {

    constructor( private sessionService: SessionService, private toastrService: ToastrService ) {
        sessionService.getMessages('Toast').subscribe(m => this.showToast(m));
        sessionService.getMessages('Connected').subscribe(m => this.toastrService.clear());
        window['toastService'] = this.toastrService;
    }

    private showToast( message: any) {
        const toastMessage = message as IToastScreen;
        this.toastrService.show(toastMessage.message, null, {
            timeOut: toastMessage.duration,
            extendedTimeOut: toastMessage.duration,
            disableTimeOut: this.isStickyToast(toastMessage),
            tapToDismiss: this.isStickyToast(toastMessage),
            positionClass: this.getPosition(toastMessage.verticalPosition),
            toastClass: `ngx-toastr app-${this.getType(toastMessage.toastType)}`
        }, this.getType(toastMessage.toastType));
        this.sessionService.cancelLoading();
    }

    private isStickyToast(toastMessage: IToastScreen): boolean {
        return toastMessage.duration === 0;
    }

    private getPosition(verticalPosition: String): string {
        switch (verticalPosition) {
            case 'top':
                return 'toast-top-center';
            case 'bottom':
                return 'toast-bottom-center';
        }
        return null;
    }

    private getType(type: String): string {
        switch (type) {
            case ToastType.Success:
                return 'toast-success';
            case ToastType.Warn:
                return 'toast-warning';
        }
        return null;
    }
}
