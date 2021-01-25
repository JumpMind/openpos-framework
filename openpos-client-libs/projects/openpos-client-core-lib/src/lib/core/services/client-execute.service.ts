import {Injectable} from '@angular/core';
import {SessionService} from './session.service';
import {ClientExecuteConfig} from "../interfaces/client-execute-config.interface";
import {ElectronService} from "ngx-electron";
import * as _ from 'lodash';
import {MessageTypes} from "../messages/message-types";

@Injectable({
    providedIn: 'root',
})
export class ClientExecuteService {
    constructor(private sessionService: SessionService, private electronService: ElectronService) {
        sessionService.getMessages(MessageTypes.CLIENT_EXECUTE)
            .subscribe(m => this.execute(m));
    }

    private execute(message: any) {
        const config = message as ClientExecuteConfig;
        console.log("Executing command...")
        const args = _.concat([config.data], config.args);
        if (this.electronService.isElectronApp) {
            this.electronService.ipcRenderer.invoke(config.executableType, args);
        } else {
            console.warn('Cannot execute command ' + config.data);
        }
        this.sessionService.cancelLoading();
    }
}
