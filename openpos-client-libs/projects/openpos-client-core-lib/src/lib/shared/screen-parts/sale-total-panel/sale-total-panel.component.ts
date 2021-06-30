import { Component, Injector } from '@angular/core';
import { SaleTotalPanelInterface } from './sale-total-panel.interface';
import { ScreenPart } from '../../decorators/screen-part.decorator';
import { ScreenPartComponent } from '../screen-part';
import { IActionItem } from '../../../core/actions/action-item.interface';
import { Configuration } from '../../../configuration/configuration';
import { Observable } from "rxjs";
import { MediaBreakpoints, OpenposMediaService } from "../../../core/media/openpos-media.service";
import { MessageTypes } from '../../../core/messages/message-types';
import { takeUntil } from 'rxjs/operators';
import { LoyaltySignupMessage } from '../../../core/messages/loyalty-signup-message';

@ScreenPart({
    name: 'SaleTotalPanel'
})
@Component({
    selector: 'app-sale-total-panel',
    templateUrl: './sale-total-panel.component.html',
    styleUrls: ['./sale-total-panel.component.scss']
})
export class SaleTotalPanelComponent extends ScreenPartComponent<SaleTotalPanelInterface> {
    private loyaltyIconToken = '${icon}';
    public loyaltyBefore: string;
    public loyaltyAfter: string;
    public loyaltySignupInProgressOnCustomerDisplay: boolean;

    constructor(injector: Injector, media: OpenposMediaService) {
        super(injector);
        this.isMobile$ = media.observe(new Map([
            [MediaBreakpoints.MOBILE_PORTRAIT, true],
            [MediaBreakpoints.MOBILE_LANDSCAPE, true],
            [MediaBreakpoints.TABLET_PORTRAIT, true],
            [MediaBreakpoints.TABLET_LANDSCAPE, false],
            [MediaBreakpoints.DESKTOP_PORTRAIT, false],
            [MediaBreakpoints.DESKTOP_LANDSCAPE, false]
        ]));
        this.sessionService.getMessages(MessageTypes.LOYALTY_SIGNUP)
            .pipe(takeUntil(this.destroyed$))
            .subscribe((message: LoyaltySignupMessage) => {
                this.loyaltySignupInProgressOnCustomerDisplay = message.isActiveOnCustomerDisplay;
            });
    }

    screenDataUpdated() {
        this.loyaltySignupInProgressOnCustomerDisplay = this.screenData.loyaltySignupInProgressOnCustomerDisplay;
        if (this.screenData.loyaltyButton) {
            const title = this.screenData.loyaltyButton.title as string;
            const parts = title.split(this.loyaltyIconToken);
            if (parts && parts.length > 0) {
                this.loyaltyBefore = parts[0].trim();
                if (parts.length > 1) {
                    this.loyaltyAfter = parts[1].trim();
                }
            }
        }
    }

    public keybindsEnabled(menuItem: IActionItem): boolean {
        return Configuration.enableKeybinds && !!menuItem.keybind && menuItem.keybind !== 'Enter';
    }

    public doMenuItemAction(menuItem: IActionItem) {
        this.doAction(menuItem);
    }

    public isMissingCustomerInfo() {
        return this.screenData.customerMissingInfoEnabled && this.screenData.customerMissingInfo
    }
}
