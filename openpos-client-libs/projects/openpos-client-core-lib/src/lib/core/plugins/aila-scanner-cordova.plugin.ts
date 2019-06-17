import { IPlatformPlugin } from './platform-plugin.interface';
import { IScanner } from './scanner.interface';
import { Observable, Subject } from 'rxjs';
import { Injectable } from '@angular/core';
import { IScanData } from './scan.interface';

@Injectable()
export class AilaScannerCordovaPlugin implements IPlatformPlugin, IScanner {

    private scanData$ = new Subject<IScanData>();

    private AilaCordovaPlugin;

    name(): string {
        return 'AilaCordovaPlugin';
    }

    pluginPresent(): boolean {
        return !!window.hasOwnProperty('AilaCordovaPlugin');
    }

    initialize(): Observable<string> {
        return Observable.create( (initialized: Subject<string>) => {
            // tslint:disable-next-line:no-string-literal
            this.AilaCordovaPlugin = window['AilaCordovaPlugin'];
            if ( !this.AilaCordovaPlugin ) {
                initialized.error(`Tried to initialize plugin ${this.name()} which is not present`);
            }

            this.AilaCordovaPlugin.configure(
                'PadlocScanScanningModeContinuous',
                'PadlocScanMotionDetectionModeOn',
                'PadlocScanBeepModeOn',
                'PadlocScanCardDetectionModeOn',
                255,
                'PadlocScanDebugLevelWarn',
                (configResult) => {
                    this.AilaCordovaPlugin.init(
                        (initResult) => {
                        },
                        (initError) => {
                            initialized.error(initError);
                        });
                },
                (configError) => {
                    initialized.error(configError);
                });
            initialized.complete();
        });

    }

    startScanning(): Observable<IScanData> {
        if ( !this.AilaCordovaPlugin ) {
            throw Error(`Tried to start scanning with ${this.name()} which is not loaded`);
        }

        this.AilaCordovaPlugin.scanStart((result) => {
            this.seqScanStep();
        });

        return this.scanData$;
    }

    stopScanning() {
        if ( !this.AilaCordovaPlugin ) {
            throw Error(`Tried to stop scanning with ${this.name()} which is not loaded`);
        }

        this.AilaCordovaPlugin.scanStop((result) => {
        }, (error) => {
            alert('Error: ' + error);
        });
    }

    private seqScanStep() {
        this.AilaCordovaPlugin.getNextScan((result) => {
            this.scanData$.next(result);
            this.seqScanStep();
        }, (error) => {
            alert('Error: ' + error);
        });
    }

}
