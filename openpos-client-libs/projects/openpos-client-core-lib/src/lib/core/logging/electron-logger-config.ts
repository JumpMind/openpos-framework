import { InjectionToken } from '@angular/core';

export interface IElectronLoggerConfig {
    logDir?: string;
    clientLogFilename: string;
    format: string;
}
export const DEFAULT_ELECTRON_LOGGER_CONFIG: IElectronLoggerConfig = {
    clientLogFilename: 'openpos-client.log',
    format: '{y}-{m}-{d} {h}:{i}:{s},{ms} {level}  {text}'
};

export const ELECTRON_LOGGER_CONFIG = new InjectionToken<IElectronLoggerConfig>('electron-logger.config');

