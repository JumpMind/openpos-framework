import { MatSnackBar, MatSnackBarVerticalPosition } from '@angular/material';
import { FileUploadService } from './../services/file-upload.service';
import { IPlugin } from './../common/iplugin';
import { IMenuItem } from '../common/imenuitem';
import { Component, Input } from '@angular/core';
import { SessionService } from '../services/session.service';
import { PluginService } from './../services/plugin.service';
import { StatusBarData } from '../common/screen-interfaces/statusBarData';

@Component({
  selector: 'app-statusbar',
  templateUrl: './statusbar.component.html'
})
export class StatusBarComponent {
  @Input()
  data: StatusBarData;

  constructor(private session: SessionService,
    private pluginService: PluginService, private fileUploadService: FileUploadService,
    public snackBar: MatSnackBar) {
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


}
