import {FlexibleConnectedPositionStrategy, Overlay, OverlayRef, VerticalConnectionPos} from '@angular/cdk/overlay';
import {ComponentPortal, PortalInjector} from '@angular/cdk/portal';
import {MatDialog} from '@angular/material/dialog';
import {MatSidenav} from '@angular/material/sidenav';
import {over} from '@stomp/stompjs';
import {SideNavComponent} from '../../../../../../../dist/openpos-client-core-lib';
import {ActionService} from '../../../core/actions/action.service';
import {MessageProvider} from '../../providers/message.provider';
import {BaconDrawerComponent} from './bacon-drawer/bacon-drawer.component';
import { BaconStripInterface } from './bacon-strip.interface';
import { ScreenPartComponent } from '../screen-part';
import { OpenposMediaService } from '../../../core/services/openpos-media.service';
import { Observable } from 'rxjs';
import {Component, ElementRef, Injector, ViewChild, ViewContainerRef} from '@angular/core';
import { ScreenPart } from '../../decorators/screen-part.decorator';

@ScreenPart({
    name: 'baconStrip'})
@Component({
    selector: 'app-bacon-strip',
    templateUrl: './bacon-strip.component.html',
    styleUrls: ['./bacon-strip.component.scss']})
export class BaconStripComponent extends ScreenPartComponent<BaconStripInterface> {

    operatorInfo: string;
    iconButtonName: string;

    @ViewChild(MatSidenav)
    baconDrawer: MatSidenav;


    screenDataUpdated() {
        if(this.screenData.actions && this.screenData.actions.length == 1){
            this.iconButtonName = this.screenData.actions[0].icon;
        } else if( this.screenData.actions ){
            this.iconButtonName = 'menu';
        }

        if (this.screenData.operatorText && this.screenData.deviceId ) {
            this.operatorInfo = this.screenData.operatorText + ' on ' + this.screenData.deviceId;
        } else {
            this.operatorInfo = this.screenData.operatorText ? this.screenData.operatorText : this.screenData.deviceId;
        }
    }

    buttonClick(){
        if(this.screenData.actions.length == 1){
            this.doAction(this.screenData.actions[0]);
        } else {
            this.baconDrawer.toggle();
        }
    }
}
