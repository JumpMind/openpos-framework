import { Component, OnInit, OnDestroy, Input, Output, EventEmitter, Injector, ViewChild } from '@angular/core';
import { SearchExpandInputInterface } from './search-expand-input.interface';
import { OnBecomingActive } from '../../../core/life-cycle-interfaces/becoming-active.interface';
import { OnLeavingActive } from '../../../core/life-cycle-interfaces/leaving-active.interface';
import { Observable, Subscription } from 'rxjs';
import { IActionItem } from '../../../core/actions/action-item.interface';
import { DeviceService } from '../../../core/services/device.service';
import { ScannerService } from '../../../core/platform-plugins/scanners/scanner.service';
import { MediaBreakpoints, OpenposMediaService } from '../../../core/media/openpos-media.service';
import { MatInput } from '@angular/material';
import { ActionService } from '../../../core/actions/action.service';


@Component({
    selector: 'app-search-expand-input',
    templateUrl: './search-expand-input.component.html',
    styleUrls: ['./search-expand-input.component.scss']
})
export class SearchExpandInputComponent implements
    OnInit, OnDestroy, OnBecomingActive, OnLeavingActive {

    public barcode: string;
    isMobile$: Observable<boolean>;

    @Input() defaultAction: IActionItem;
    @Input() searchData: SearchExpandInputInterface;
    @Output() expanded: EventEmitter<boolean> = new EventEmitter<boolean>();

    private scanServiceSubscription: Subscription;

    @ViewChild(MatInput) inputElement: MatInput;
    public open = false;

    constructor(
        public devices: DeviceService, mediaService: OpenposMediaService,
        private scannerService: ScannerService, private actionService: ActionService) {
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

    ngOnInit(): void {
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
        this.scannerService.stopScanning();
    }

    private registerScanner() {
        if (typeof this.scanServiceSubscription === 'undefined' || this.scanServiceSubscription === null) {
            this.scanServiceSubscription = this.scannerService.startScanning().subscribe(scanData => {
                this.actionService.doAction({ action: this.searchData.scanActionName }, scanData);
            });
        }
    }

    private unregisterScanner() {
        if (this.scanServiceSubscription !== null) {
            this.scanServiceSubscription.unsubscribe();
            this.scanServiceSubscription = null;
        }
    }

    public onEnter(): void {
        if (this.barcode && this.barcode.trim().length >= this.searchData.scanMinLength) {
            this.actionService.doAction({ action: this.searchData.keyedActionName }, this.barcode);
            this.barcode = '';
        } else if (this.defaultAction && this.defaultAction.enabled) {
            this.actionService.doAction(this.defaultAction);
        }
    }

    public onSelected(): void {
        this.open = true;
        this.inputElement.focus();
        this.expanded.emit(true);
    }

    public onFocusOut(): void {
        this.open = false;
        this.expanded.emit(false);
    }
}
