import { Injectable } from '@angular/core';
import { ElectronService } from 'ngx-electron';

export class LogLevel {
    public static DEBUG = 4;
    public static INFO = 3;
    public static WARN = 2;
    public static ERROR = 1;
}

@Injectable({
    providedIn: 'root',
})
export class Logger {

    electronLogger: any;

    logLevel = LogLevel.INFO;

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

    public debug(message: any, ...args: any[]): void {
        if (this.electron.isElectronApp) {
            this.electronLogger.debug(message);
        } else {
            this.log(LogLevel.DEBUG, message, args);
        }
    }

    public info(message: any, ...args: any[]): void {
        if (this.electron.isElectronApp) {
            this.electronLogger.info(message);
        } else {
            this.log(LogLevel.INFO, message, args);
        }
    }

    public warn(message: any, ...args: any[]): void {
        if (this.electron.isElectronApp) {
            this.electronLogger.warn(message);
        } else {
            this.log(LogLevel.WARN, message, args);
        }
    }

    public error(message: any, ...args: any[]): void {
        if (this.electron.isElectronApp) {
            this.electronLogger.error(message);
        } else {
            this.log(LogLevel.ERROR, message, args);
        }
    }

    protected log(logLevel: number, message: any, args: any[]): void {
        if (this.logLevel >= logLevel) {
            let messageType: string;
            switch (logLevel) {
                case LogLevel.DEBUG:
                    messageType = 'debug';
                    break;
                case LogLevel.WARN:
                    messageType = 'warn';
                    break;
                case LogLevel.ERROR:
                    messageType = 'error';
                    break;
                case LogLevel.INFO:
                default:
                    messageType = 'info';
                    break;
            }
            if (args.length > 0) {
                console[messageType](message, args);
            } else {
                console[messageType](message);
            }
        }
    }
}


