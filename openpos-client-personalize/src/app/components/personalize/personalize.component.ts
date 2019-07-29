import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, AbstractControl } from '@angular/forms';
import { ClientUrlService } from 'src/app/services/client-url.service';
import { PersonalizeService } from 'src/app/services/personalize.service';
import { PersonalizationResponse } from 'src/app/interfaces/personalization-response.interface';
import { PersonalizationResult } from 'src/app/interfaces/personalization-result.interface';

@Component({
  selector: 'app-personalize-component',
  templateUrl: './personalize.component.html',
  styleUrls: ['./personalize.component.scss']
})
export class PersonalizeComponent implements OnInit {

  firstFormGroup: FormGroup;
  secondFormGroup: FormGroup;
  lastFormGroup: FormGroup;
  clientResponse: any;
  serverResponse: PersonalizationResponse;
  clientTimeout: any;
  serverTimeout: any;

  constructor(
    private formBuilder: FormBuilder, private clientUrlService: ClientUrlService,
    private personalizeService: PersonalizeService
  ) { }

  ngOnInit() {

    if (localStorage.getItem('clientUrl')) {
      this.clientUrlService.renavigate();
    } else {
      this.firstFormGroup = this.formBuilder.group({
        clientName: ['', [Validators.required]],
        clientPort: ['', [Validators.required, , Validators.pattern('^[0-9]+$')]],
        appName: ['', [Validators.required]],
        sslEnabled: ['']
      }, { asyncValidator: this.clientValidator });
      // });

      this.secondFormGroup = this.formBuilder.group({
        serverName: ['', [Validators.required]],
        serverPort: ['', [Validators.required, , Validators.pattern('^[0-9]+$')]],
        sslEnabled: ['']
      }, { asyncValidator: this.serverValidator });

      this.updateLastFormGroup();
    }
  }

  updateLastFormGroup() {
    let devicePattern = '[a-zA-Z0-9\-]+';
    if (this.serverResponse && this.serverResponse.devicePattern) {
      devicePattern = this.serverResponse.devicePattern;
    }
    const formGroup = {
      deviceId: ['', [Validators.required, , Validators.pattern(devicePattern)]]
    };

    if (this.serverResponse) {
      const validator = [Validators.required, , Validators.pattern('[a-zA-Z0-9]+')];
      for (const prop of this.serverResponse.parameters) {
        formGroup[prop.property] = [prop.defaultValue, validator];
      }
    }

    this.lastFormGroup = this.formBuilder.group(formGroup);
  }

  public personalize() {
    const clientName = this.firstFormGroup.get('clientName').value;
    const clientPort = this.firstFormGroup.get('clientPort').value;
    const appName = this.firstFormGroup.get('appName').value;
    const clientSslEnabled = this.firstFormGroup.get('sslEnabled').value;

    const serverName = this.secondFormGroup.get('serverName').value;
    const serverPort = this.secondFormGroup.get('serverPort').value;
    const serverSslEnabled = this.secondFormGroup.get('sslEnabled').value;

    const deviceId = this.lastFormGroup.get('deviceId').value;

    const personalizationResults: PersonalizationResult[] = [];
    if (this.serverResponse && this.serverResponse.parameters) {
      for (const parameter of this.serverResponse.parameters) {
        personalizationResults.push({
          property: parameter.property,
          value: this.lastFormGroup.get(parameter.property).value
        });
      }
    }

    this.clientUrlService.navigate(clientName, clientPort, appName, clientSslEnabled,
      serverName, serverPort, serverSslEnabled, deviceId, personalizationResults);
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
        this.serverResponse = await this.personalizeService.requestPersonalization(serverName, serverPort, sslEnabled);
        if (this.serverResponse.success) {
          this.updateLastFormGroup();
          resolve(null);
        } else {
          console.log(`Personalization request failed with error: ${this.serverResponse.message}`);
          resolve(this.serverResponse);
        }
      }, 1000);
    });
  }
}
