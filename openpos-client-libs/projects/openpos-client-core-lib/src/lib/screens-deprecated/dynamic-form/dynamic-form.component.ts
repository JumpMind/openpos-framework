import { ScannerService } from '../../core/platform-plugins/scanners/scanner.service';
import { Component, OnDestroy, Injector } from '@angular/core';
import { PosScreen } from '../pos-screen/pos-screen.component';
import { ScreenComponent } from '../../shared/decorators/screen-component.decorator';
import { SessionService } from '../../core/services/session.service';
import { Subscription } from 'rxjs';

/**
 * @ignore
 */
@ScreenComponent({
  name: 'DynamicForm'
})
@Component({
  selector: 'app-dynamic-form',
  templateUrl: './dynamic-form.component.html'
})
export class DynamicFormComponent extends PosScreen<any> implements OnDestroy {

    private scanServiceSubscription: Subscription;

    constructor(private scannerService: ScannerService, injector: Injector) {
        super(injector);
    }

    buildScreen() {
        // this.scanServiceSubscription = this.scannerService.startScanning().subscribe( m => this.session.onAction('Scan', m));
    }

    ngOnDestroy(): void {
        if (this.scanServiceSubscription != null) {
            this.scanServiceSubscription.unsubscribe();
        }
        this.scannerService.stopScanning();
    }


}
