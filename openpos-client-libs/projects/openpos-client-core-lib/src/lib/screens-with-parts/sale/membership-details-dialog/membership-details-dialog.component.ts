import {Component, Injector} from '@angular/core';
import {MembershipDetailsDialogInterface} from './membership-details-dialog.interface';
import {DialogComponent} from '../../../shared/decorators/dialog-component.decorator';
import {PosScreen} from '../../pos-screen/pos-screen.component';
import {Observable} from 'rxjs';
import {MediaBreakpoints, OpenposMediaService} from '../../../core/media/openpos-media.service';
import {IActionItem} from "../../../core/actions/action-item.interface";
import {Configuration} from "../../../configuration/configuration";
import {KeyPressProvider} from "../../../shared/providers/keypress.provider";
import {ActionService} from "../../../core/actions/action.service";

@DialogComponent({
  name: 'MembershipDetailsDialog'
})
@Component({
  selector: 'app-membership-details-dialog',
  templateUrl: './membership-details-dialog.component.html',
  styleUrls: ['./membership-details-dialog.component.scss']
})
export class MembershipDetailsDialogComponent extends PosScreen<MembershipDetailsDialogInterface>{

  isMobile: Observable<boolean>;
  constructor(public actionService: ActionService, injector: Injector, private media: OpenposMediaService, protected keyPresses: KeyPressProvider) {
    super(injector);
    this.initIsMobile();
  }


  initIsMobile(): void {
    this.isMobile = this.media.observe(new Map([
      [MediaBreakpoints.MOBILE_PORTRAIT, true],
      [MediaBreakpoints.MOBILE_LANDSCAPE, true],
      [MediaBreakpoints.TABLET_PORTRAIT, true],
      [MediaBreakpoints.TABLET_LANDSCAPE, true],
      [MediaBreakpoints.DESKTOP_PORTRAIT, false],
      [MediaBreakpoints.DESKTOP_LANDSCAPE, false]
    ]));
  }

  buildScreen() {

  }

  public keybindsEnabled(menuItem: IActionItem): boolean {
    return Configuration.enableKeybinds && !!menuItem.keybind && menuItem.keybind !== 'Enter';
  }
}
