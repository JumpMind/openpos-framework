import { DynamicScreenComponent } from './dynamic-screen/dynamic-screen.component';
import { IMenuItem } from './../common/imenuitem';
import { IScreen } from '../common/iscreen';
import { Component, ViewChild, AfterViewInit, DoCheck, OnInit, Output } from '@angular/core';
import { SessionService } from '../services/session.service';
import { MatSelectChange } from '@angular/material';
import { FormBuilder, FormGroup, Validators, AbstractControl } from '@angular/forms';
import { ValidatorFn } from '@angular/forms/src/directives/validators';
import { HttpClient, HttpHeaders, HttpErrorResponse } from '@angular/common/http';

@Component({
    selector: 'app-personalization',
    templateUrl: './personalization.component.html'
})
export class PersonalizationComponent implements IScreen, OnInit {

    firstFormGroup: FormGroup;
    secondFormGroup: FormGroup;
    checkTimeout: any;

    constructor(public session: SessionService, private formBuilder: FormBuilder, private http: HttpClient) {
    }

    ngOnInit() {
        this.firstFormGroup = this.formBuilder.group({
            serverName: ['', [Validators.required]],
            serverPort: ['', [Validators.required, , Validators.pattern('^[0-9]+$')]],
            sslEnabled: ['']
        }, { asyncValidator: this.serverValidator });
        this.secondFormGroup = this.formBuilder.group({
            storeNumber: ['', [Validators.required, , Validators.pattern('\\d{5}')]],
            deviceNumber: ['', [Validators.required, , Validators.pattern('\\d{3}')]]
        });
    }

    show(screen: any): void {
    }

    public personalize() {
        this.session.personalize(this.firstFormGroup.get('serverName').value, this.firstFormGroup.get('serverPort').value,
            this.secondFormGroup.get('storeNumber').value, this.secondFormGroup.get('deviceNumber').value, 
            this.firstFormGroup.get('sslEnabled').value);
    }

    serverValidator = (control: AbstractControl) => {
        clearTimeout(this.checkTimeout);
        return new Promise((resolve, reject) => {
            this.checkTimeout = setTimeout(() => {
                const serverName = control.get('serverName').value;
                const serverPort = control.get('serverPort').value;
                const sslEnabled: boolean = control.get('sslEnabled').value;
                if (serverName) {
                    let protocol = 'http://';
                    if (sslEnabled) {
                        protocol = 'https://';
                    }
                    let url: string = protocol + serverName;
                    if (serverPort) {
                        url = url + ':' + serverPort;
                    }
                    url = url + '/ping';
                    console.log('testing url: ' + url);

                    this.http.get(url, {}).subscribe(
                        (data) => {
                            console.log('successful validation of ' + url);
                            resolve(null);
                        },
                        (err: HttpErrorResponse) => {
                            console.log('bad validation of ' + url + ' with an error message of :' + err.message);
                            resolve({ noping: err.message });
                        });
                }

            }, 1000);
        });
    }

}

