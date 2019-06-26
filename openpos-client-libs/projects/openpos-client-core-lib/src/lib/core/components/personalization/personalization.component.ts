import { Logger } from './../../services/logger.service';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, AbstractControl } from '@angular/forms';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { IScreen } from '../../../shared/components/dynamic-screen/screen.interface';
import { PersonalizationService } from '../../services/personalization.service';
import { PersonalizationResponse } from '../../interfaces/personalization-response.interface';
import { OverlayContainer } from '@angular/cdk/overlay';
import { SessionService } from '../../services/session.service';

@Component({
    selector: 'app-personalization',
    templateUrl: './personalization.component.html'
})
export class PersonalizationComponent implements IScreen, OnInit {

    firstFormGroup: FormGroup;
    secondFormGroup: FormGroup;
    response: PersonalizationResponse;
    checkTimeout: any;

    constructor(private log: Logger, private personalization: PersonalizationService, private session: SessionService, 
                private formBuilder: FormBuilder, private http: HttpClient, overlayContainer: OverlayContainer) {
        overlayContainer.getContainerElement().classList.add('openpos-default-theme');
    }

    ngOnInit() {
        this.firstFormGroup = this.formBuilder.group({
            serverName: ['', [Validators.required]],
            serverPort: ['', [Validators.required, , Validators.pattern('^[0-9]+$')]],
            sslEnabled: ['']
        }, { asyncValidator: this.serverValidator });
        this.updateSecondFormGroup();
    }

    updateSecondFormGroup() {
        let devicePattern = '[a-zA-Z0-9\-]+';
        if (this.response && this.response.devicePattern) {
            devicePattern = this.response.devicePattern;
        }
        const formGroup = {
            deviceId: ['', [Validators.required, , Validators.pattern(devicePattern)]]
        };

        if (this.response) {
            const validator = [Validators.required, , Validators.pattern('[a-zA-Z0-9]+')];
            for (const prop of this.response.parameters) {
                formGroup[prop.property] = [prop.defaultValue, validator];
            }
        }

        this.secondFormGroup = this.formBuilder.group(formGroup);
    }

    show(screen: any): void {
    }

    public personalize() {
        const personalizationProperties = new Map<string, string>();
        if (this.response) {
            for (const prop of this.response.parameters) {
                personalizationProperties.set(prop.property, this.secondFormGroup.get(prop.property).value);
            }
        }
        this.personalization.personalize(this.firstFormGroup.get('serverName').value, this.firstFormGroup.get('serverPort').value,
            this.secondFormGroup.get('deviceId').value, personalizationProperties, this.firstFormGroup.get('sslEnabled').value);
    }

    serverValidator = async (control: AbstractControl) => {
        clearTimeout(this.checkTimeout);
        const serverName = control.get('serverName').value;
        const serverPort = control.get('serverPort').value;
        const sslEnabled: boolean = control.get('sslEnabled').value;

        return new Promise((resolve, reject) => {
            this.checkTimeout = setTimeout(async () => {
                this.response = await this.session.requestPersonalization({ serverName: serverName, serverPort: serverPort, useSsl: sslEnabled });
                if (this.response.success) {
                    this.updateSecondFormGroup();
                    resolve(null);
                } else {
                    this.log.info(`Personalization request failed with error: ${this.response.message}`);
                    resolve(this.response);
                }
            }, 1000);
        });
    }

}

