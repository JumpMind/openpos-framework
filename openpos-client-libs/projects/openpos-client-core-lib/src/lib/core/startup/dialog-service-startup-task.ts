import { Observable, Subject } from 'rxjs';
import { StartupTaskNames } from './startup-task-names';
import { Injectable } from '@angular/core';
import { IStartupTask } from './startup-task.interface';
import { DialogService } from '../services/dialog.service';

@Injectable({
    providedIn: 'root',
})
export class DialogServiceStartupTask implements IStartupTask {

    name = StartupTaskNames.DIALOG_SERVICE_STARTUP;

    order = 900;

    constructor(protected dialogService: DialogService) {

    }

    execute(): Observable<string> {
        return Observable.create( (message: Subject<string>) => {
            message.next('Starting DialogService...');
            this.dialogService.start();
            message.next('DialogService started');
            message.complete();
        });
    }


}
