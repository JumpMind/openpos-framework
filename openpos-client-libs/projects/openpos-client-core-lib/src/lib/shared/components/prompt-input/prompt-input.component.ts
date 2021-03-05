import { Subscription } from 'rxjs';
import { FormGroup, FormControl, FormGroupDirective, NgForm } from '@angular/forms';
import { Component, Input, OnInit, OnDestroy } from '@angular/core';
import { ErrorStateMatcher } from '@angular/material';
import { OldPluginService } from '../../../core/services/old-plugin.service';
import { BarcodeScannerPlugin } from '../../../core/oldplugins/barcode-scanner.plugin';
import { Scan } from '../../../core/oldplugins/scan';

@Component({
    selector: 'app-prompt-input',
    styleUrls: ['./prompt-input.component.scss'],
    templateUrl: './prompt-input.component.html'
})

export class PromptInputComponent implements OnInit, OnDestroy {

    @Input() placeholderText: string;
    @Input() responseType: string;
    @Input() responseText: string;
    @Input() promptIcon: string;
    @Input() hintText: string;
    @Input() minLength: number;
    @Input() maxLength: number;
    @Input() promptFormGroup: FormGroup;
    @Input() readOnly = false;
    @Input() keyboardPreference: string;
    @Input() scanEnabled = false;
    @Input() validationMessages: Map<string, string>;

    inputType: string;
    checked = true;
    errorMatcher = new MyErrorStateMatcher();
    keyboardLayout = 'en-US';

    private barcodeEventSubscription: Subscription;

    constructor( private pluginService: OldPluginService) {
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

        if (this.scanEnabled) {
            this.pluginService.getPluginWithOptions('barcodeScannerPlugin', true, { waitForCordovaInit: true }).then(plugin => {
                // the onBarcodeScanned will only emit an event when client code passes a scan
                // event to the plugin.  This won't be called for cordova barcodescanner plugin
                // camera-based scan events.  It should only be used for third party scan events
                // which come from other sources such as a scan device
                this.barcodeEventSubscription = (<BarcodeScannerPlugin>plugin).onBarcodeScanned.subscribe({
                    next: (scan: Scan) => {
                        console.info(`app-prompt-input got scan event: ${scan.value}`);
                        this.setFieldValue(scan.value);
                    }
                });
                console.info(`app-prompt-input is subscribed for barcode scan events`);

            }).catch(error => console.info(`Failed to get barcodeScannerPlugin.  Reason: ${error}`));
        }

        this.setKeyboardLayout();

    }

    ngOnDestroy(): void {
        if (this.barcodeEventSubscription) {
            this.barcodeEventSubscription.unsubscribe();
        }
    }

    isScanAllowed(): boolean {
        return this.scanEnabled && (this.responseType && ['numerictext', 'alphanumerictext'].indexOf(this.responseType.toLowerCase()) >= 0);
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
        ).catch(error => console.info(`Scanning failed: ${error}`)
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