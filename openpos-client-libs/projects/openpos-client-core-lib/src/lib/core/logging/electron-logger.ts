import { ILogger } from './logger.interface';
import { ElectronService } from 'ngx-electron';
import { Injectable } from '@angular/core';

@Injectable({
    providedIn: 'root'
})
export class ElectronLogger implements ILogger {
    electronLogger: any;

    constructor(private electron: ElectronService) {
        if (this.electron.isElectronApp) {
            console.log('configuring electron logging');
            this.electronLogger = this.electron.remote.require('electron-log');
            // this.electronLogger.transports.rendererConsole.level = 'silly';
            // electronLogger.transports.file.level = false;
            const fileTransport = this.electronLogger.transports.file;
            const app = this.electron.remote.app;
            const path = this.electron.remote.require('path');
            const logDir = path.dirname(app.getPath('exe')) + '/../logs';
            console.log('log directory set to ' + logDir);
            const fs = this.electron.remote.require('fs');
            if (!fs.existsSync(logDir)) {
                fs.mkdirSync(logDir);
            }
            fileTransport.file = logDir + '/nu-client.log';
            fileTransport.format = '{y}-{m}-{d} {h}:{i}:{s},{ms} {level}  {text}';
            this.electronLogger.transports.console.level = false;
            this.electron.ipcRenderer.on('errorInWindow', function(event, data) {
                this.electronLogger.error(data);
            });
        }
    }

    log(message: string) {
        if ( this.electron.isElectronApp ) {
            this.electronLogger.info(message);
        }
    }

    info(message: string) {
        if ( this.electron.isElectronApp ) {
            this.electronLogger.info(message);
        }
    }

    error(message: string) {
        if ( this.electron.isElectronApp ) {
            this.electronLogger.error(message);
        }
    }

    warn(message: string) {
        if ( this.electron.isElectronApp ) {
            this.electronLogger.warn(message);
        }
    }

    debug(message: string) {
        if ( this.electron.isElectronApp ) {
            this.electronLogger.debug(message);
        }
    }


}
