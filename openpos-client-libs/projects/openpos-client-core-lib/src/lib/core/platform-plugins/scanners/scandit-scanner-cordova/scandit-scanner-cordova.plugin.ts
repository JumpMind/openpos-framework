import { Injectable } from '@angular/core';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { Observable, Subject } from 'rxjs';
import { filter } from 'rxjs/operators';
import { SessionService } from '../../../services/session.service';
import { IPlatformPlugin } from '../../platform-plugin.interface';
import { IScanData } from '../scan.interface';
import { IScanner } from '../scanner.interface';
import { ScanditCameraViewComponent } from './scandit-camera-view/scandit-camera-view.component';
import { ScanditBarcodeUtils } from './scandit-to-openpos-barcode-type';

declare var Scandit: any;

@Injectable()
export class ScanditScannerCordovaPlugin implements IScanner, IPlatformPlugin {

    private licenseKey = "";
    private scanData$ = new Subject<IScanData>();
    private settings;
    private barcodeCapture: any = {};
    private cameraSettings;
    private context: any;
    private dialogRef: MatDialogRef<ScanditCameraViewComponent>;
    private listener: any;
    private viewFinderType: string;
    private enableVibrate: boolean;
    private enableBeep: boolean;
    private config: any;

    constructor(sessionService: SessionService, private matDialog: MatDialog) {

        sessionService.getMessages('ConfigChanged').pipe(
            filter(m => m.configType === 'ScanditCordova')
        ).subscribe(m => {
            console.log(m);
            this.config = m;

        });

        sessionService.getMessages('Screen', 'Dialog').subscribe(m => {
            if (this.dialogRef) {
                this.dialogRef.close();
            }
        });
    }

    initialize(): Observable<string> {
        return Observable.create((initialized: Subject<string>) => {

            this.settings = new Scandit.BarcodeCaptureSettings();

            if(!this.settings){
                initialized.error('Failed to create Scandit BarcodeCaptureSettings object');
            }

            if (this.config.licenseKey) {
                this.licenseKey = this.config.licenseKey;
            }

            Object.getOwnPropertyNames(this.config).forEach(propName => {
                if (this.settings.hasOwnProperty(propName)) {
                    this.settings[propName] = this.config[propName];
                }
            });

            if (this.config.enabledCodes) {
                const codes = this.config.enabledCodes.split(',');
                codes.forEach(code => this.settings.enableSymbology(ScanditBarcodeUtils.convertFromOpenposType(code.trim()), true));
            }
            if (this.config.viewFinderType) {
                this.viewFinderType = this.config.viewFinderType;
            }
            if (this.config.enableVibrate) {
                this.enableVibrate = this.config.enableVibrate;
            }
            if (this.config.enableBeep) {
                this.enableBeep = this.config.enableBeep;
            }

            this.context = Scandit.DataCaptureContext.forLicenseKey(this.licenseKey);
            console.log('Initializing Scandit with settings', this.settings);
            this.barcodeCapture = Scandit.BarcodeCapture.forContext(this.context, this.settings);
            this.barcodeCapture.feedback = this.getFeedback();
            this.barcodeCapture.isEnabled = false;

            let camera;
            if(this.config.camera === 'front'){
                camera = Scandit.Camera.atPosition(Scandit.CameraPosition.UserFacing);
            } else if( this.config.camera === 'rear'){
                camera = Scandit.Camera.atPosition(Scandit.CameraPosition.WorldFacing);

            } else {
                camera = Scandit.Camera.default;
            }

            if (!this.cameraSettings) {
                this.cameraSettings = Scandit.BarcodeCapture.recommendedCameraSettings;
            }

            if (camera) {
                camera.applySettings(this.cameraSettings);
            }
            this.context.setFrameSource(camera);
            camera.switchToDesiredState(Scandit.FrameSourceState.On);
            this.listener = {
                didScan: (barcodeCapture, session) => {
                    barcodeCapture.isEnabled = false;
                    if (this.dialogRef) {
                        this.dialogRef.close();
                    }
                    setTimeout(() => this.processScan(session));
                }
            };
            this.barcodeCapture.addListener(this.listener);
            initialized.complete();
        });
    }

    name(): string {
        return 'ScanditCordova';
    }

    pluginPresent(): boolean {
        return window['Scandit'];
    }

    startScanning(): Observable<IScanData> {
        return this.scanData$;
    }

    stopScanning() {
    }

    triggerScan() {
        this.barcodeCapture.isEnabled = true;
        this.dialogRef = this.matDialog.open(ScanditCameraViewComponent);
        this.dialogRef.afterOpened().subscribe(() => {
            const view = Scandit.DataCaptureView.forContext(this.context);
            view.connectToElement(document.getElementById('scandit-capture-view'));
            const overlay = Scandit.BarcodeCaptureOverlay.withBarcodeCaptureForView(this.barcodeCapture, view);
            overlay.viewfinder = this.getViewfinderType();
        }
        )
    }

    private processScan(session: any) {
        console.log('Scanned ', session);
        const scanData = (
            {
                type: ScanditBarcodeUtils.convertToOpenposType(session.newlyRecognizedBarcodes[0].symbology),
                data: session.newlyRecognizedBarcodes[0].data
            }
        ) as IScanData;
        this.scanData$.next(scanData);
    }

    private getViewfinderType() {
        if (this.viewFinderType) {
            switch (this.viewFinderType) {
                case 'Laser':
                    console.log('Scandit Setting Laserline ViewFinder');
                    return new Scandit.LaserlineViewfinder();
                case 'Rectangular':
                    console.log('Scandit Setting Rectangular ViewFinder');
                    return new Scandit.RectangularViewfinder();
                case 'Spotlight':
                    console.log('Scandit Setting Spotlight ViewFinder');
                    return new Scandit.SpotlightViewfinder();
            }
        }
        return null;
    }

    private getFeedback() {
        const feedback = Scandit.BarcodeCaptureFeedback.default;
        feedback.success = new Scandit.Feedback(
            this.enableVibrate ? Scandit.Vibration.defaultVibration : null,
            this.enableBeep ? Scandit.Sound.defaultSound : null);

        console.log('Scandit Feedback', feedback);

        return feedback;
    }
}