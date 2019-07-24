import { SelfCheckoutMenuInterface } from './self-checkout-menu.interface';
import { OpenposMediaService } from '../../../core/services/openpos-media.service';
import { Observable } from 'rxjs';
import { Component, Injector } from '@angular/core';
import { ScreenPartComponent } from '../../../shared/screen-parts/screen-part';
import { ScreenPart } from '../../../shared/decorators/screen-part.decorator';
import { IActionItem } from '../../../core/actions/action-item.interface';
import { MatDialogConfig, MatDialog } from '@angular/material';
import { ScanSomethingComponent } from '../../../shared/components/scan-something/scan-something.component';


@ScreenPart({
    name: 'selfCheckoutMenu'
})
@Component({
    selector: 'app-self-checkout-menu',
    templateUrl: './self-checkout-menu.component.html',
    styleUrls: ['./self-checkout-menu.component.scss']
})
export class SelfCheckoutMenuComponent extends ScreenPartComponent<SelfCheckoutMenuInterface> {

    isMobile$: Observable<boolean>;
    operatorInfo: string;

    constructor(mediaService: OpenposMediaService, injector: Injector, protected dialogService: MatDialog) {
        super(injector);
        const mobileMap = new Map([
            ['xs', true],
            ['sm', false],
            ['md', false],
            ['lg', false],
            ['xl', false]
        ]);
        this.isMobile$ = mediaService.mediaObservableFromMap(mobileMap);
    }

    screenDataUpdated() {
        if (this.screenData.backButton) {
            this.screenData.backButton.keybind = 'Escape';
        }
        if (this.screenData.operatorText && this.screenData.deviceId) {
            this.operatorInfo = this.screenData.operatorText + ' on ' + this.screenData.deviceId;
        } else {
            this.operatorInfo = this.screenData.operatorText != null ? this.screenData.operatorText : this.screenData.deviceId;
        }
    }

    public doMenuItemAction(menuItem: IActionItem) {
        this.doAction(menuItem);
    }

    public onAdminLogin() {
        this.doAction('ShowLogin');
    }

    public showScan() {
        const dialogConfig: MatDialogConfig = { autoFocus: true };
        this.dialogService.open(ScanSomethingComponent, dialogConfig);
    }

}
