import {Component, Injector, OnInit} from '@angular/core';
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
export class MembershipDetailsDialogComponent extends PosScreen<MembershipDetailsDialogInterface> implements OnInit {

  isMobile: Observable<boolean>;
  constructor(public actionService: ActionService, injector: Injector, private media: OpenposMediaService, protected keyPresses: KeyPressProvider) {
    super(injector);
    this.initIsMobile();
  }

  ngOnInit() {
    this.screen.subscriptionAccounts.forEach((subscription) => {
      subscription.plans = [
        {
          iconImageUrl: "heart_plus_outline",
          iconText: "",
          title: "Vital Care 1.0 Dog",
          planCopy: "this is where the copy happens",
          signupActionItem: undefined
        },
        {
          iconImageUrl: "heart_plus_outline",
          iconText: "",
          title: "Vital Care 1.0 Cat",
          planCopy: "this is where the copy happens",
          signupActionItem: undefined
        },
        {
          iconImageUrl: "heart_plus_outline",
          iconText: "",
          title: "Vital Care 1.0 Snek",
          planCopy: "this is where the copy happens",
          signupActionItem: undefined
        }
      ];
    })
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
