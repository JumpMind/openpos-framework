import { Component, OnDestroy } from '@angular/core';
import { DialogComponent } from '../../shared/decorators/dialog-component.decorator';
import { PosScreen } from '../../screens-deprecated/pos-screen/pos-screen.component';
import { ChooseOptionsScreenDialogInterface } from './choose-options-screen-dialog.interface';
import { ScannerService } from '../../core/platform-plugins/scanners/scanner.service';
import { Subscription } from 'rxjs';

@DialogComponent({
    name: 'ChooseOptions'
})
@Component({
  selector: 'app-choose-options-screen-dialog',
  templateUrl: './choose-options-screen-dialog.component.html',
  styleUrls: ['./choose-options-screen-dialog.component.scss']
})
export class ChooseOptionsScreenDialogComponent extends PosScreen<ChooseOptionsScreenDialogInterface> implements OnDestroy {

  private scanServiceSubscription: Subscription;

  constructor(private scannerService: ScannerService) {
      super();
  }

  buildScreen() {
  }

  ngOnDestroy(): void {
      if (this.scanServiceSubscription != null) {
          this.scanServiceSubscription.unsubscribe();
      }
      this.scannerService.stopScanning();
  }

}
