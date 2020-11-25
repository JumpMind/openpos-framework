import { Component, Input } from '@angular/core';
import { AudioLicense, AudioLicenseLabels } from './audio-license.interface';

@Component({
    selector: 'app-audio-license',
    templateUrl: './audio-license.component.html',
    styleUrls: ['./audio-license.component.scss']
})
export class AudioLicenseComponent {
    @Input() license: AudioLicense;
    @Input() labels: AudioLicenseLabels;
}
