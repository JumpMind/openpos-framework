import { Component, Inject, OnInit, Optional } from '@angular/core';
import {FormBuilder, FormGroup, Validators, AbstractControl, AsyncValidatorFn, ValidationErrors} from '@angular/forms';
import {Router} from '@angular/router';
import {Observable, of} from 'rxjs';
import {catchError, map} from 'rxjs/operators';
import { IScreen } from '../../shared/components/dynamic-screen/screen.interface';
import { PersonalizationService } from './personalization.service';
import { PersonalizationConfigResponse } from './personalization-config-response.interface';
import { ClientUrlService } from './client-url.service';
import { MatDialog, MAT_DIALOG_DATA } from '@angular/material';
import { DiscoveryService } from '../discovery/discovery.service';
import { DiscoveryStatus } from '../discovery/discovery-status.enum';
import { DiscoveryResponse } from '../discovery/discovery-response.interface';

@Component({
    selector: 'app-personalization',
    templateUrl: './personalization.component.html'
})
export class PersonalizationComponent implements IScreen, OnInit {

    navigateExternal = false;
    manualPersonalization = false;
    openposMgmtServerPresent = false;
    discoveryStatus: DiscoveryStatus;
    discoveryResponse: DiscoveryResponse;

    firstFormGroup: FormGroup;
    secondFormGroup: FormGroup;
    thirdFormGroup: FormGroup;
    lastFormGroup: FormGroup;
    clientResponse: any;
    serverResponse: PersonalizationConfigResponse;
    availableDevices: {key:string, value:string}[];
    clientTimeout: any;
    serverTimeout: any;
    errorMessage: string;
    appIds: string[];
    appServerAddress: string;
    appServerPort: string;
    serverIsSSL: boolean;

    constructor(
        private formBuilder: FormBuilder, private clientUrlService: ClientUrlService,
        private personalizationService: PersonalizationService,
        private discoveryService: DiscoveryService,
        private matDialog: MatDialog,
        @Inject(MAT_DIALOG_DATA) @Optional() private data?: { serverAddress?: string, serverPort?: string, appId?: string }
    ) {}

    show(screen: any): void {
    }

    ngOnInit() {

        this.navigateExternal = this.clientUrlService.navigateExternal;
        this.serverIsSSL = window.location.protocol.includes('https');
        this.appServerAddress = this.data && this.data.serverAddress ? this.data.serverAddress : window.location.hostname;

        if (this.data && this.data.serverPort) {
            this.appServerPort = this.data.serverPort;
        } else {
            this.appServerPort = window.location.port 
                ? window.location.port 
                : this.serverIsSSL ? '443': '';
        }

        if (this.navigateExternal && localStorage.getItem('clientUrl')) {
            this.clientUrlService.renavigate();
        } else {
            this.firstFormGroup = this.formBuilder.group({
                clientName: ['', [Validators.required]],
                clientPort: ['', [Validators.required, , Validators.pattern('^[0-9]+$')]],
                appName: ['', [Validators.required]],
                sslEnabled: ['']
            }, { asyncValidator: this.clientValidator });

            this.secondFormGroup = this.formBuilder.group({
                serverName: [this.appServerAddress, [Validators.required]],
                serverPort: [this.appServerPort, [Validators.required, , Validators.pattern('^[0-9]+$')]],
                sslEnabled: [this.serverIsSSL]
            }, { asyncValidator: this.serverValidator });

            this.updateThirdFormGroup();
            this.updateLastFormGroup();
        }
    }

    updateThirdFormGroup() {
        let devicePattern = '[a-zA-Z0-9\-]+';
        this.openposMgmtServerPresent = false;
        if (this.serverResponse) {
            if (this.serverResponse.devicePattern) {
                devicePattern = this.serverResponse.devicePattern;
            }

            if( this.serverResponse.loadedAppIds) {
                this.appIds = this.serverResponse.loadedAppIds;
            }

            if(this.serverResponse.availableDevices){
                this.availableDevices = [];
                const availableDeviceMap = this.serverResponse.availableDevices;
                Object.entries(availableDeviceMap).forEach(entry => {
                    let key = entry[0];
                    let value = entry[1];
                    this.availableDevices.push({key, value});
                });
                this.availableDevices.sort((deviceOne, deviceTwo) =>
                    (deviceOne.value > deviceTwo.value) ? 1 : (deviceOne.value === deviceTwo.value) ? ((deviceOne.key > deviceTwo.key) ? 1 : -1) : -1 );
            } else {
                this.manualPersonalization = true;
            }
            this.openposMgmtServerPresent = !!this.serverResponse.openposManagementServer;
        }

        if (this.manualPersonalization) {
            let defaultDeviceId = '';

            if (this.data && this.data.appId) {
                defaultDeviceId = this.data.appId;
            }

            const formGroup = {
                deviceId: ['', [Validators.required, Validators.pattern(devicePattern)]],
                appId: [defaultDeviceId, [Validators.required]]
            };

            this.thirdFormGroup = this.formBuilder.group(formGroup);
        } else {
            const formGroup = {
                device: ['', [Validators.required]]
            }

            this.thirdFormGroup = this.formBuilder.group(formGroup);
        }
    }

    updateLastFormGroup() {
        const formGroup = {
        };
        if (this.serverResponse) {
            const validator = [Validators.required, , Validators.pattern('[a-zA-Z0-9]+')];
            if (this.serverResponse.parameters) {
                for (const prop of this.serverResponse.parameters) {
                    formGroup[prop.property] = [prop.defaultValue, validator];
                }
            }
        }

        this.lastFormGroup = this.formBuilder.group(formGroup);
    }

    public navigate() {
        const clientName = this.firstFormGroup.get('clientName').value;
        const clientPort = this.firstFormGroup.get('clientPort').value;
        const appName = this.firstFormGroup.get('appName').value;
        const clientSslEnabled = this.firstFormGroup.get('sslEnabled').value;

        const serverName = this.secondFormGroup.get('serverName').value;
        const serverPort = this.secondFormGroup.get('serverPort').value;
        const serverSslEnabled = this.secondFormGroup.get('sslEnabled').value;


        const deviceId = this.thirdFormGroup.get('deviceId').value;
        const appId = this.thirdFormGroup.get('appId').value;

        const personalizationProperties = new Map<string, string>();
        if (this.serverResponse && this.serverResponse.parameters) {
            for (const parameter of this.serverResponse.parameters) {
                personalizationProperties.set(parameter.property, this.lastFormGroup.get(parameter.property).value);
            }
        }

        this.clientUrlService.navigate(clientName, clientPort, appName, clientSslEnabled,
            serverName, serverPort, serverSslEnabled, deviceId, appId, personalizationProperties);
    }

    public personalizeLocal(): Observable<string> {
        const personalizationProperties = new Map<string, string>();
        if (this.serverResponse && this.serverResponse.parameters) {
            for (const parameter of this.serverResponse.parameters) {
                personalizationProperties.set(parameter.property, this.lastFormGroup.get(parameter.property).value);
            }
        }

        let server = this.secondFormGroup.get('serverName').value;
        let port = this.secondFormGroup.get('serverPort').value;
        if (this.openposMgmtServerPresent && this.discoveryResponse && this.discoveryResponse.success) {
            personalizationProperties.set(PersonalizationService.OPENPOS_MANAGED_SERVER_PROPERTY, 'true');
        }

        if( this.manualPersonalization ){
            return  this.personalizationService.personalize(
                server,
                port,
                this.thirdFormGroup.get('deviceId').value,
                this.thirdFormGroup.get('appId').value,
                personalizationProperties,
                this.secondFormGroup.get('sslEnabled').value
            );
        } else {
            return this.personalizationService.personalizeWithToken(
                server,
                port,
                this.thirdFormGroup.get('device').value,
                this.secondFormGroup.get('sslEnabled').value
            );
        }



    }

    public discoveryBack() {
        this.discoveryResponse = null;
        this.discoveryStatus = null;
    }

    isDiscoveryCompleted(): boolean {
        return this.discoveryStatus === DiscoveryStatus.Completed;
    }
    isDiscoveryInProgress(): boolean {
        return this.discoveryStatus === DiscoveryStatus.InProgress
    }
    public discoveryCompleted() {
        this.discoveryStatus = DiscoveryStatus.Completed;
    }

    public getDiscoveryResponseErrorMessage(): string {
        return (typeof this.discoveryResponse.message === 'string') ? 
            this.discoveryResponse.message : 
            JSON.stringify(this.discoveryResponse.message);
    }

    public async discover() {
        if (this.openposMgmtServerPresent) {
            this.discoveryStatus = DiscoveryStatus.InProgress;

            this.discoveryResponse = await this.discoveryService.discoverDeviceProcess({
                server: this.secondFormGroup.get('serverName').value,
                port: this.secondFormGroup.get('serverPort').value, 
                deviceId: this.thirdFormGroup.get('deviceId').value,
                sslEnabled: this.secondFormGroup.get('sslEnabled').value,
                maxWaitMillis: 90000
            });
            if (this.discoveryResponse && this.discoveryResponse.success) {
                this.personalizationService.requestPersonalizationConfig(
                    this.discoveryResponse.host, 
                    this.discoveryResponse.port,
                    this.secondFormGroup.get('sslEnabled').value
                ).subscribe( {
                    next: result => {
                        this.updateLastFormGroup();
                        this.discoveryStatus = DiscoveryStatus.Completed;
                    },
                    error: error  => {
                        this.discoveryStatus = DiscoveryStatus.Failed;
                        this.discoveryResponse.success = false;
                        this.discoveryResponse.message = `Personalization request failed with error: ${error}`;
                        console.warn(this.discoveryResponse.message);
                    }
                });
            } else {
                this.discoveryStatus = DiscoveryStatus.Failed;
            }
        } else {
            // If we haven't connected to an openpos mgmt server, we'll
            // already have the personalization properties config and can
            // now display them.
            this.updateLastFormGroup();
        }
    }

    public personalize() {
        if (this.navigateExternal) {
            this.navigate();
        } else {
            this.personalizeLocal().subscribe({
                next: response => this.matDialog.closeAll(),
                error: error => {
                    console.warn(error);
                    this.errorMessage = `Personalization request failed`;
                }
                });

        }
    }

    private updateDefaultServerValues(clientName: string) {
        this.secondFormGroup.get('serverName').setValue(clientName);
        this.secondFormGroup.get('serverPort').setValue('6140');
    }

    clientValidator = async (control: AbstractControl) => {
        clearTimeout(this.clientTimeout);
        const clientName = control.get('clientName').value;
        const clientPort = control.get('clientPort').value;
        const appName = control.get('appName').value;
        const clientSslEnabled: boolean = control.get('sslEnabled').value;

        return new Promise((resolve, reject) => {
            this.clientTimeout = setTimeout(async () => {
                this.clientResponse = await this.clientUrlService.checkClientStatus(clientName, clientPort, appName, clientSslEnabled);
                if (this.clientResponse.success) {
                    this.updateDefaultServerValues(clientName);
                    resolve(null);
                } else {
                    resolve(this.clientResponse);
                }
            }, 1000);
        });
    }


    serverValidator = (control: AbstractControl): Observable<ValidationErrors|null> => {
        clearTimeout(this.serverTimeout);
        const serverName = control.get('serverName').value;
        const serverPort = control.get('serverPort').value;
        const sslEnabled: boolean = control.get('sslEnabled').value;

        return this.personalizationService.requestPersonalizationConfig(serverName, serverPort, sslEnabled)
            .pipe(
                map( response => {
                    this.serverResponse = response;
                    this.updateThirdFormGroup();
                    this.updateLastFormGroup();
                    return null;
                }),
                catchError(error => {
                    console.warn(`Personalization request failed with error: ${error}`);
                    return of({'message': `${serverName}:${serverPort}`});
                })
            );

        };
}

