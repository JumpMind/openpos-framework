import { Component, Injector } from '@angular/core';
import { SaleTotalPanelInterface } from './sale-total-panel.interface';
import { ScreenPart } from '../../decorators/screen-part.decorator';
import { ScreenPartComponent } from '../screen-part';
import { IActionItem } from '../../../core/actions/action-item.interface';
import { Configuration } from '../../../configuration/configuration';
import { MediaBreakpoints, OpenposMediaService } from '../../../core/media/openpos-media.service';
import { LoyaltySignupService } from '../../../core/services/loyalty-signup.service';
import { Observable } from 'rxjs/internal/Observable';
import { glowContractTrigger, glowPulseTrigger } from '../../animations/glow.animation';
import { shakeTrigger } from '../../animations/shake.animation';
import { throbTrigger } from '../../animations/throb.animation';
import { swingTrigger } from '../../animations/swing.animation';
import { gradientInnerGlowTrigger } from '../../animations/gradient-inner-glow.animation';

@ScreenPart({
    name: 'SaleTotalPanel'
})
@Component({
    selector: 'app-sale-total-panel',
    templateUrl: './sale-total-panel.component.html',
    styleUrls: ['./sale-total-panel.component.scss'],
    animations: [
        glowContractTrigger,
        glowPulseTrigger,
        shakeTrigger,
        throbTrigger,
        swingTrigger,
        gradientInnerGlowTrigger
    ]
})
export class SaleTotalPanelComponent extends ScreenPartComponent<SaleTotalPanelInterface> {
    private loyaltyIconToken = '${icon}';
    public loyaltyBefore: string;
    public loyaltyAfter: string;
    public isLoyaltySignupInProgressOnCustomerDisplay$: Observable<boolean>;
    public loyaltySignupInProgressDetailsMessage$: Observable<string>;
    public glowPulseRepeatTrigger = true;
    public gradientInnerGlowRepeatTrigger = true;

    constructor(injector: Injector, media: OpenposMediaService,
                private loyaltySignupService: LoyaltySignupService) {
        super(injector);
        this.isMobile$ = media.observe(new Map([
            [MediaBreakpoints.MOBILE_PORTRAIT, true],
            [MediaBreakpoints.MOBILE_LANDSCAPE, true],
            [MediaBreakpoints.TABLET_PORTRAIT, true],
            [MediaBreakpoints.TABLET_LANDSCAPE, false],
            [MediaBreakpoints.DESKTOP_PORTRAIT, false],
            [MediaBreakpoints.DESKTOP_LANDSCAPE, false]
        ]));

        this.isLoyaltySignupInProgressOnCustomerDisplay$ = this.loyaltySignupService.isActiveOnCustomerDisplay();
        this.loyaltySignupInProgressDetailsMessage$ = this.loyaltySignupService.getCustomerDisplayDetailsMessage();
        this.loyaltySignupService.checkCustomerDisplayStatus();
    }

    screenDataUpdated() {
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

    public doMenuItemAction(menuItem: IActionItem): void {
        this.doAction(menuItem);
    }

    public isMissingCustomerInfo(): boolean {
        return this.screenData.customerMissingInfoEnabled && this.screenData.customerMissingInfo
    }

    public repeatGlowPulse(): void {
        this.glowPulseRepeatTrigger = !this.glowPulseRepeatTrigger
    }

    public repeatGradientInnerGlow(): void {
        this.gradientInnerGlowRepeatTrigger = !this.gradientInnerGlowRepeatTrigger;
    }
}
