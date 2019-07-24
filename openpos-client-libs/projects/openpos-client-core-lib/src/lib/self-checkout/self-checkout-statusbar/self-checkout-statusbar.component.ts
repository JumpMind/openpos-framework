import { MatDialog, MatDialogConfig, MatSnackBar } from '@angular/material';
import { Component, Input } from '@angular/core';
import { SelfCheckoutStatusBarData } from './self-checkout-status-bar-data';
import { SessionService } from '../../core/services/session.service';
import { IActionItem } from '../../core/actions/action-item.interface';
import { ScanSomethingComponent } from '../../shared/components/scan-something/scan-something.component';
import { ActionService } from '../../core/actions/action.service';

@Component({
  selector: 'app-self-checkout-statusbar',
  templateUrl: './self-checkout-statusbar.component.html',
  styleUrls: ['./self-checkout-statusbar.component.scss']

})
export class SelfCheckoutStatusBarComponent {

  @Input()
  data: SelfCheckoutStatusBarData;

  constructor(
      public session: SessionService,
      public snackBar: MatSnackBar,
      public dialogService: MatDialog,
      public actionService: ActionService) {
  }

  public doMenuItemAction(menuItem: IActionItem) {
    this.actionService.doAction(menuItem);
  }

  public isMenuItemEnabled(m: IActionItem): boolean {
    let enabled = m.enabled;
    if (m.action.startsWith('<') && this.session.isRunningInBrowser()) {
      enabled = false;
    }
    return enabled;
  }

  public onAdminLogin() {
    this.actionService.doAction({action: 'ShowLogin'});
  }

  public showScan() {
    const dialogConfig: MatDialogConfig = { autoFocus: true, data: this.data.scanSomethingData };
    this.dialogService.open(ScanSomethingComponent, dialogConfig);
  }

}
