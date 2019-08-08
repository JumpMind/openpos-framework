import { IActionItem } from '../../../core/actions/action-item.interface';
import { Component, Input, Output, EventEmitter, OnInit, OnDestroy, Injector } from '@angular/core';
import { ScreenPartComponent } from '../screen-part';
import { ScanOrSearchInterface } from './scan-or-search.interface';
import { DeviceService } from '../../../core/services/device.service';
import { ScreenPart } from '../../decorators/screen-part.decorator';
import { OpenposMediaService } from '../../../core/services/openpos-media.service';
import { Observable, Subscription } from 'rxjs';
import { ScannerService } from '../../../core/platform-plugins/scanners/scanner.service';
import { OnBecomingActive } from '../../../core/life-cycle-interfaces/becoming-active.interface';
import { OnLeavingActive } from '../../../core/life-cycle-interfaces/leaving-active.interface';

@ScreenPart({
    name: 'scan'
})
@Component({
    selector: 'app-scan-or-search',
    templateUrl: './scan-or-search.component.html',
    styleUrls: ['./scan-or-search.component.scss']
})
export class ScanOrSearchComponent extends ScreenPartComponent<ScanOrSearchInterface> implements
                                    OnInit, OnDestroy, OnBecomingActive, OnLeavingActive {

    public barcode: string;
    isMobile$: Observable<boolean>;

    @Input() defaultAction: IActionItem;
    @Output() change: EventEmitter<string> = new EventEmitter<string>();

    private scanServiceSubscription: Subscription;

    constructor(public devices: DeviceService, injector: Injector,
                mediaService: OpenposMediaService, private scannerService: ScannerService ) {
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
        this.scannerService.stopScanning();
        super.ngOnDestroy();
    }

    private registerScanner() {
        if (typeof this.scanServiceSubscription === 'undefined' || this.scanServiceSubscription === null ) {
            this.scanServiceSubscription = this.scannerService.startScanning().subscribe(scanData => {
                this.doAction( { action: this.screenData.scanActionName }, scanData );
            });
        }
    }

    private unregisterScanner() {
        if (this.scanServiceSubscription !== null) {
            this.scanServiceSubscription.unsubscribe();
            this.scanServiceSubscription = null;
        }
    }

    screenDataUpdated() {
    }

    public onEnter(): void {
        if (this.barcode && this.barcode.trim().length >= this.screenData.scanMinLength) {
            this.doAction({ action: this.screenData.scanActionName}, this.barcode);
            this.barcode = '';
        } else if (this.defaultAction && this.defaultAction.enabled) {
            this.doAction(this.defaultAction);
        }
    }

    public onValueChange($event: any): void {
        this.change.emit(this.barcode);
    }

    private filterBarcodeValue(val: string): string {
        if (!val) {
            return val;
        }
        // Filter out extra characters permitted by HTML5 input type=number (for exponentials)
        const pattern = /[e|E|\+|\-|\.]/g;

        return val.toString().replace(pattern, '');
    }

    onBarcodePaste(event: ClipboardEvent) {
        const content = event.clipboardData.getData('text/plain');
        const filteredContent = this.filterBarcodeValue(content);
        if (filteredContent !== content) {
            this.log.info(`Clipboard data contains invalid characters for barcode, suppressing pasted content '${content}'`);
        }
        return filteredContent === content;
    }
}
