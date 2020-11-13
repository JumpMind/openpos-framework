import { Component, Input } from '@angular/core';
import { ContentLicense, ContentLicenseLabels } from './content-license.interface';

@Component({
    selector: 'app-content-license',
    templateUrl: './content-license.component.html',
    styleUrls: ['./content-license.component.scss']
})
export class ContentLicenseComponent {
    @Input() license: ContentLicense;
    @Input() labels: ContentLicenseLabels;
}
