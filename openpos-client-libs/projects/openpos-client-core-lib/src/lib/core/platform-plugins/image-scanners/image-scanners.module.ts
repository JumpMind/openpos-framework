import { NgModule, ModuleWithProviders, Provider } from '@angular/core';

import { Plugins as CapacitorPlugins } from '@capacitor/core';

import { IMAGE_SCANNERS } from './image-scanner';

import { ImageScanners } from './image-scanners.service';
import { ScanditCapacitorImageScanner } from '../image-scanners/scandit-capacitor/scandit-capacitor.service';
import { ImageScannerComponent } from './image-scanner.component';

@NgModule({
    imports: [],
    exports: [
        ImageScannerComponent
    ],
    declarations: [
        ImageScannerComponent
    ],
    providers: [],
})
export class ImageScannersModule {
    static forRoot(): ModuleWithProviders {
        return {
            ngModule: ImageScannersModule,
            providers: [
                ImageScanners,
                { provide: IMAGE_SCANNERS, useClass: ScanditCapacitorImageScanner, multi: true }
            ]
        };
    }
}
