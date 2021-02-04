import { ModuleWithProviders, NgModule } from '@angular/core';
import { SCANNERS } from '../scanner.service';

import { ScanditNativeScanner } from './scandit-scanner';
import { ScanditNativeComponent } from './scandit-native.component';

@NgModule({
    imports: [],
    exports: [ScanditNativeComponent],
    declarations: [ScanditNativeComponent],
    providers: [],
})
export class ScanditNativeModule {
    static forRoot(): ModuleWithProviders {
        return {
            ngModule: ScanditNativeModule,
            providers: [
                { provide: SCANNERS, useExisting: ScanditNativeScanner, multi: true },
            ]
        };
    }
}
