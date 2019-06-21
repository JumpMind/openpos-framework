import { Injectable } from '@angular/core';
import { MatSnackBar, MatSnackBarVerticalPosition } from '@angular/material';
import { SessionService } from './session.service';
import { IToastScreen, ToastType } from '../interfaces/toast-screen.interface';

@Injectable({
    providedIn: 'root',
  })
export class ToastService {

    constructor( private snackBar: MatSnackBar, private sessionService: SessionService ) {
        sessionService.getMessages('Toast').subscribe(m => this.showToast(m));
        sessionService.getMessages('Connected').subscribe(m => this.snackBar.dismiss());
    }

    private showToast( message: any) {
        const toast = message as IToastScreen;
        this.snackBar.open(toast.message, toast.duration === 0 ? 'X' : null, {
            duration: toast.duration,
            panelClass: this.getToastClass(toast.toastType),
            verticalPosition: toast.verticalPosition === 'top' ? 'top' : 'bottom'
        });
        this.sessionService.cancelLoading();
    }

    private getToastClass(type: ToastType): string {
        switch (type) {
            case ToastType.Success:
                return 'toast-success';
            case ToastType.Warn:
                return 'toast-warn';
        }

        return null;
    }
}
