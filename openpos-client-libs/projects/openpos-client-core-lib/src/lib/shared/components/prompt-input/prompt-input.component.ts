import { Subscription } from 'rxjs';
import { FormGroup, FormControl, FormGroupDirective, NgForm } from '@angular/forms';
import { Component, Input, OnInit, OnDestroy } from '@angular/core';
import { ErrorStateMatcher } from '@angular/material';

import { BarcodeScanner } from '../../../core/platform-plugins/barcode-scanners/barcode-scanner.service';

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

    showScanner = false;

    private _activeScan: Subscription;

    constructor(private _barcodeScanner: BarcodeScanner) {}

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
        this.setKeyboardLayout();
    }

    ngOnDestroy(): void {
        if (this._activeScan) {
            this._activeScan.unsubscribe();
        }
    }

    isScanAllowed(): boolean {
        return this.scanEnabled 
            && (this.responseType && ['numerictext', 'alphanumerictext'].indexOf(this.responseType.toLowerCase()) >= 0);
            //&& this._barcodeScanner.hasImageScanner;
    }

    // This method is invoked when the user presses the Scan button on the field.
    // For device-based scan events, see the ngOnInit method.

    onScan(): void {
        this.showScanner = !this.showScanner && this.isScanAllowed();

        console.log('scanner show changed', this.showScanner);
        
        if (this.showScanner) {
            //this._activeScan = this._barcodeScanner.beginImageScanning()
        } else {
            this._activeScan.unsubscribe();
        }
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