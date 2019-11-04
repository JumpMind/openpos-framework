import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, AbstractControl } from '@angular/forms';
import { IScreen } from '../../shared/components/dynamic-screen/screen.interface';
import { PersonalizationService } from './personalization.service';
import { PersonalizationResponse } from './personalization-response.interface';
import { ClientUrlService } from './client-url.service';
import { MatDialog } from '@angular/material';
import { DiscoveryService } from '../discovery/discovery.service';
import { DiscoveryStatus } from '../discovery/discovery-status.enum';
import { DiscoveryResponse } from '../discovery/discovery-response.interface';

@Component({
    selector: 'app-personalization',
    templateUrl: './personalization.component.html'
})
export class PersonalizationComponent implements IScreen, OnInit {

    navigateExternal = false;
    openposMgmtServerPresent = false;
    discoveryStatus: DiscoveryStatus;
    discoveryResponse: DiscoveryResponse;

    firstFormGroup: FormGroup;
    secondFormGroup: FormGroup;
    thirdFormGroup: FormGroup;
    lastFormGroup: FormGroup;
    clientResponse: any;
    serverResponse: PersonalizationResponse;
    clientTimeout: any;
    serverTimeout: any;

    constructor(
        private formBuilder: FormBuilder, private clientUrlService: ClientUrlService,
        private personalizationService: PersonalizationService,
        private discoveryService: DiscoveryService,
        private matDialog: MatDialog
    ) { }

    show(screen: any): void {
    }

    ngOnInit() {

        this.navigateExternal = this.clientUrlService.navigateExternal;

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
                serverName: ['', [Validators.required]],
                serverPort: ['', [Validators.required, , Validators.pattern('^[0-9]+$')]],
                sslEnabled: ['']
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
            this.openposMgmtServerPresent = !!this.serverResponse.openposManagementServer;
        }

        const formGroup = {
            deviceId: ['', [Validators.required, , Validators.pattern(devicePattern)]]
        };

        this.thirdFormGroup = this.formBuilder.group(formGroup);
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

        const personalizationProperties = new Map<string, string>();
        if (this.serverResponse && this.serverResponse.parameters) {
            for (const parameter of this.serverResponse.parameters) {
                personalizationProperties.set(parameter.property, this.lastFormGroup.get(parameter.property).value);
            }
        }

        this.clientUrlService.navigate(clientName, clientPort, appName, clientSslEnabled,
            serverName, serverPort, serverSslEnabled, deviceId, personalizationProperties);
    }

    public personalizeLocal() {
        const personalizationProperties = new Map<string, string>();
        if (this.serverResponse) {
            for (const parameter of this.serverResponse.parameters) {
                personalizationProperties.set(parameter.property, this.lastFormGroup.get(parameter.property).value);
            }
        }

        let server = this.secondFormGroup.get('serverName').value;
        let port = this.secondFormGroup.get('serverPort').value;
        if (this.openposMgmtServerPresent && this.discoveryResponse && this.discoveryResponse.success) {
            personalizationProperties.set(PersonalizationService.OPENPOS_MANAGED_SERVER_PROPERTY, 'true');
        }

        this.personalizationService.personalize(
            server, 
            port,
            this.thirdFormGroup.get('deviceId').value, 
            personalizationProperties, 
            this.secondFormGroup.get('sslEnabled').value
        );
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
                this.serverResponse = await this.personalizationService.requestPersonalization(
                    this.discoveryResponse.host, 
                    this.discoveryResponse.port,
                    this.secondFormGroup.get('sslEnabled').value
                );
                if (this.serverResponse.success) {
                    this.updateLastFormGroup();
                    this.discoveryStatus = DiscoveryStatus.Completed;
                } else {
                    this.discoveryStatus = DiscoveryStatus.Failed;
                    this.discoveryResponse.success = false;
                    this.discoveryResponse.message = `Personalization request failed with error: ${this.serverResponse.message}`;
                    console.warn(this.discoveryResponse.message);
                }
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
            this.personalizeLocal();
            this.matDialog.closeAll();
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


    serverValidator = async (control: AbstractControl) => {
        clearTimeout(this.serverTimeout);
        const serverName = control.get('serverName').value;
        const serverPort = control.get('serverPort').value;
        const sslEnabled: boolean = control.get('sslEnabled').value;

        return new Promise((resolve, reject) => {
            this.serverTimeout = setTimeout(async () => {
                this.serverResponse = await this.personalizationService.requestPersonalization(serverName, serverPort, sslEnabled);
                if (this.serverResponse.success) {
                    this.updateThirdFormGroup();
                    this.updateLastFormGroup();
                    resolve(null);
                } else {
                    console.warn(`Personalization request failed with error: ${this.serverResponse.message}`);
                    resolve(this.serverResponse);
                }
            }, 1000);
        });
    }

}

