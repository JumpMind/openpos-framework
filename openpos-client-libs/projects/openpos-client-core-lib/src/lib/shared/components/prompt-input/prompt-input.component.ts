import { Logger } from './../../../core/services/logger.service';
import { Subscription } from 'rxjs';
import { FormGroup, FormControl, FormGroupDirective, NgForm } from '@angular/forms';
import { Component, Input, OnInit, OnDestroy } from '@angular/core';
import { DatePipe } from '@angular/common';
import { TextMask, IMaskSpec, ITextMask } from '../../textmask';
import { ErrorStateMatcher } from '@angular/material';
import { OldPluginService } from '../../../core/services/old-plugin.service';
import { BarcodeScannerPlugin } from '../../../core/oldplugins/barcode-scanner.plugin';
import { Scan } from '../../../core/oldplugins/scan';

@Component({
    selector: 'app-prompt-input',
    templateUrl: './prompt-input.component.html'
})

export class PromptInputComponent implements OnInit, OnDestroy {

    @Input() placeholderText: string;
    @Input() responseType: string;
    @Input() responseText: string;
    @Input() promptIcon: string;
    @Input() hintText: string;
    @Input() maskSpec: IMaskSpec;
    @Input() minLength: number;
    @Input() maxLength: number;
    @Input() promptFormGroup: FormGroup;
    @Input() readOnly = false;
    @Input() keyboardPreference: string;
    @Input() scanEnabled = false;

    inputType: string;
    checked = true;
    errorMatcher = new MyErrorStateMatcher();
    keyboardLayout = 'en-US';

    _textMask: ITextMask; // Mask object built for text-mask
    private barcodeEventSubscription: Subscription;

    constructor(private log: Logger, private datePipe: DatePipe, private pluginService: OldPluginService) {
    }

    isNumericField(): boolean {
        if (this.responseType) {
            return ['numerictext', 'money', 'phone', 'postalCode', 'percent', 'percentint', 'income', 'decimal']
                .indexOf(this.responseType.toLowerCase()) >= 0 || this.keyboardPreference === 'Numeric';
        } else {
            return false;
        }
    }

    isDateField(): boolean {
        if (this.responseType) {
            return this.responseType.toLowerCase().indexOf('date') >= 0;
        } else {
            return false;
        }
    }

    isPassword(): boolean {
        return this.responseType && this.responseType.toLowerCase() === 'alphanumericpassword';
    }

    onCheck() {
        if (this.responseText === 'ON') {
            this.responseText = 'OFF';
        } else {
            this.responseText = 'ON';
        }
    }

    ngOnInit(): void {

        if (this.maskSpec) {
            const newMask = TextMask.instance(this.maskSpec);
            this._textMask = newMask;
        } else {
            this._textMask = TextMask.NO_MASK;
        }

        if (this.scanEnabled) {
            this.pluginService.getPluginWithOptions('barcodeScannerPlugin', true, { waitForCordovaInit: true }).then(plugin => {
                // the onBarcodeScanned will only emit an event when client code passes a scan
                // event to the plugin.  This won't be called for cordova barcodescanner plugin
                // camera-based scan events.  It should only be used for third party scan events
                // which come from other sources such as a scan device
                this.barcodeEventSubscription = (<BarcodeScannerPlugin>plugin).onBarcodeScanned.subscribe({
                    next: (scan: Scan) => {
                        this.log.info(`app-prompt-input got scan event: ${scan.value}`);
                        this.setFieldValue(scan.value);
                    }
                });
                this.log.info(`app-prompt-input is subscribed for barcode scan events`);

            }).catch(error => this.log.info(`Failed to get barcodeScannerPlugin.  Reason: ${error}`));
        }

        this.setKeyboardLayout();

    }

    ngOnDestroy(): void {
        if (this.barcodeEventSubscription) {
            this.barcodeEventSubscription.unsubscribe();
        }
    }

    isScanAllowed(): boolean {
        return this.scanEnabled &&
            ['numerictext', 'alphanumerictext'].indexOf(this.responseType.toLowerCase()) >= 0;
    }


    // This method is invoked when the user presses the Scan button on the field.
    // For device-based scan events, see the ngOnInit method.

    onScan(): void {
        this.pluginService.getDevicePlugin('barcodeScannerPlugin').then(plugin =>
            plugin.processRequest(
                { requestId: 'scan', deviceId: 'barcode-scanner', type: null, subType: null, payload: null },
                (scan) => {
                    if (scan instanceof Scan && !scan.cancelled) {
                        this.setFieldValue(scan.value);
                    }
                },
                (error) => {
                    console.error('Scanning failed: ' + error);
                }
            )
        ).catch(error => this.log.info(`Scanning failed: ${error}`)
        );
    }

    private setFieldValue(value: any) {
        const patchGroup = {};
        patchGroup['promptInputControl'] = value;
        this.promptFormGroup.patchValue(patchGroup);
    }

    private setKeyboardLayout() {
        if (this.responseType)  {
            if (['numerictext', 'money', 'phone', 'postalCode', 'percent', 'percentint', 'income', 'decimal']
            .indexOf(this.responseType.toLowerCase()) >= 0) {
                this.keyboardLayout = 'Numeric';
            } else if (this.responseType.toLowerCase() === 'email') {
                this.keyboardLayout = 'Email';
            }
        }
    }
}

export class MyErrorStateMatcher implements ErrorStateMatcher {
    isErrorState(control: FormControl | null, form: FormGroupDirective | NgForm | null): boolean {
      const isSubmitted = form && form.submitted;
      return (control && (control.dirty && control.invalid));  // show error only when dirty and invalid
    }
}