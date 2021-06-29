import { Component, OnInit, OnDestroy, Input, Output, EventEmitter, Injector, ViewChild } from '@angular/core';
import { OnBecomingActive } from '../../../core/life-cycle-interfaces/becoming-active.interface';
import { OnLeavingActive } from '../../../core/life-cycle-interfaces/leaving-active.interface';
import { Observable, Subscription } from 'rxjs';
import { IActionItem } from '../../../core/actions/action-item.interface';
import { DeviceService } from '../../../core/services/device.service';
import { ScannerService } from '../../../core/platform-plugins/scanners/scanner.service';
import { MediaBreakpoints, OpenposMediaService } from '../../../core/media/openpos-media.service';
import { MatInput } from '@angular/material';
import { ScreenPart } from '../../decorators/screen-part.decorator';
import { ScreenPartComponent } from '../screen-part';
import { ScanOrSearchInterface } from '../scan-or-search/scan-or-search.interface';


@ScreenPart({
    name: 'scanOrSearch'
})
@Component({
    selector: 'app-search-expand-input',
    templateUrl: './search-expand-input.component.html',
    styleUrls: ['./search-expand-input.component.scss']
})
export class SearchExpandInputComponent extends ScreenPartComponent<ScanOrSearchInterface> implements
    OnInit, OnDestroy, OnBecomingActive, OnLeavingActive {

    public barcode: string;
    isMobile$: Observable<boolean>;

    @Input() defaultAction: IActionItem;
    @Output() expanded: EventEmitter<boolean> = new EventEmitter<boolean>();

    private scanServiceSubscription: Subscription;

    @ViewChild(MatInput) inputElement: MatInput;
    public open = false;

    constructor(
        private injector: Injector, public devices: DeviceService, mediaService: OpenposMediaService,
        private scannerService: ScannerService) {
        super(injector);
        const mobileMap = new Map([
            [MediaBreakpoints.MOBILE_PORTRAIT, true],
            [MediaBreakpoints.MOBILE_LANDSCAPE, true],
            [MediaBreakpoints.TABLET_PORTRAIT, false],
            [MediaBreakpoints.TABLET_LANDSCAPE, false],
            [MediaBreakpoints.DESKTOP_PORTRAIT, false],
            [MediaBreakpoints.DESKTOP_LANDSCAPE, false]
        ]);
        this.isMobile$ = mediaService.observe(mobileMap);
    }

    screenDataUpdated() {
    }

    ngOnInit(): void {
        super.ngOnInit();
        this.registerScanner();
    }

    onBecomingActive() {
        this.registerScanner();
    }

    onLeavingActive() {
        this.unregisterScanner();
    }

    ngOnDestroy(): void {
        this.unregisterScanner();
        super.ngOnDestroy();
    }

    private registerScanner() {
        if (typeof this.scanServiceSubscription === 'undefined' || this.scanServiceSubscription === null) {
            this.scanServiceSubscription = this.scannerService.startScanning().subscribe(scanData => {
                this.actionService.doAction(this.screenData.scanAction, scanData);
            });
        }
    }

    private unregisterScanner() {
        if (this.scanServiceSubscription) {
            this.scanServiceSubscription.unsubscribe();
            this.scanServiceSubscription = null;
        }
    }

    public onEnter(): void {
        if (this.barcode && this.barcode.trim().length >= this.screenData.scanMinLength) {
            this.actionService.doAction(this.screenData.keyedAction, this.barcode);
            this.barcode = '';
        } else if (this.defaultAction && this.defaultAction.enabled) {
            this.actionService.doAction(this.defaultAction);
        }
    }

    public onSelected(): void {
        if( open ) {
            this.scannerService.triggerScan();
        }
        this.open = true;
        this.inputElement.focus();
        this.expanded.emit(true);
    }

    public onFocusOut(): void {
        this.open = false;
        this.expanded.emit(false);
    }
}
