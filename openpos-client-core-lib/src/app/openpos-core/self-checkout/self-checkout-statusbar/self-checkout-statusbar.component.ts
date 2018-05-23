import { MatDialog, MatDialogConfig, MatDialogRef, MatSnackBar, MatSnackBarVerticalPosition } from '@angular/material';
import { FileUploadService } from '../../services/file-upload.service';
import { IMenuItem } from '../../common/imenuitem';
import { Component, Input } from '@angular/core';
import { SessionService } from '../../services/session.service';
import { ScanSomethingComponent } from '../../common/controls/scan-something/scan-something.component';
import { SelfCheckoutStatusBarData } from '../../common/screen-interfaces/selfCheckoutStatusBarData';

@Component({
  selector: 'app-self-checkout-statusbar',
  templateUrl: './self-checkout-statusbar.component.html',
  styleUrls: ['./self-checkout-statusbar.component.scss']

})
export class SelfCheckoutStatusBarComponent {

  @Input()
  data: SelfCheckoutStatusBarData;

  constructor(private session: SessionService, public snackBar: MatSnackBar, public dialogService: MatDialog) {
  }

  public doMenuItemAction(menuItem: IMenuItem) {
    this.session.onAction(menuItem.action, null, menuItem.confirmationMessage);
  }

  public isMenuItemEnabled(m: IMenuItem): boolean {
    let enabled = m.enabled;
    if (m.action.startsWith('<') && this.session.isRunningInBrowser()) {
      enabled = false;
    }
    return enabled;
  }

  public onAdminLogin() {
    this.session.onAction('ShowLogin');
  }

  public showScan() {
    const dialogConfig: MatDialogConfig = { autoFocus: true, data: this.data.scanSomethingData };
    this.dialogService.open(ScanSomethingComponent, dialogConfig);
  }

}
