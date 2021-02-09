import {IActionItem} from '../../../core/actions/action-item.interface';
import {
    Component,
    ElementRef,
    EventEmitter,
    HostBinding,
    Injector,
    Input,
    OnDestroy,
    OnInit,
    Output
} from '@angular/core';
import {ScreenPartComponent} from '../screen-part';
import {ScanOrSearchInterface} from './scan-or-search.interface';
import {ScreenPart} from '../../decorators/screen-part.decorator';
import {MediaBreakpoints, OpenposMediaService} from '../../../core/media/openpos-media.service';
import {Observable, Subscription} from 'rxjs';
import {ScannerService} from '../../../core/platform-plugins/scanners/scanner.service';
import {OnBecomingActive} from '../../../core/life-cycle-interfaces/becoming-active.interface';
import {OnLeavingActive} from '../../../core/life-cycle-interfaces/leaving-active.interface';
import { IScanData } from '../../../core/platform-plugins/scanners/scan.interface';

@ScreenPart({
    name: 'scanOrSearch'
})
@Component({
    selector: 'app-scan-or-search',
    templateUrl: './scan-or-search.component.html',
    styleUrls: ['./scan-or-search.component.scss']
})
export class ScanOrSearchComponent extends ScreenPartComponent<ScanOrSearchInterface> implements OnInit, OnDestroy, OnBecomingActive, OnLeavingActive {

    public barcode: string;
    isMobile$: Observable<boolean>;

    @Input() defaultAction: IActionItem;

    @HostBinding('class.focusInitial')
    @Input() focusInitial = true;

    @Output() change: EventEmitter<string> = new EventEmitter<string>();

    keyboardLayout = 'US Standard';

    showScannerVisual = false;

    private scanServiceSubscription: Subscription;

    constructor(
                injector: Injector,
                private el: ElementRef,
                mediaService: OpenposMediaService,
                public scannerService: ScannerService) {
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

    ngOnInit(): void {
        super.ngOnInit();
        this.registerScanner();

        if (this.screenData.keyboardLayout) {
            this.keyboardLayout = this.screenData.keyboardLayout;
        }
    }

    onBecomingActive() {
        this.registerScanner();
    }

    onLeavingActive() {
        this.unregisterScanner();
    }

    ngOnDestroy(): void {
        this.unregisterScanner();
        // this.scannerService.stopScanning();
        super.ngOnDestroy();
    }

    scan(data: IScanData) {
        this.doAction(this.screenData.scanAction, data);
    }

    private registerScanner() {
        if ((typeof this.scanServiceSubscription === 'undefined' || this.scanServiceSubscription === null) && this.screenData.willUnblock) {
            this.scanServiceSubscription = this.scannerService.startScanning().subscribe(scanData => {
                this.doAction(this.screenData.scanAction, scanData);
            });
        }
    }

    private unregisterScanner() {
        if (this.scanServiceSubscription) {
            this.scanServiceSubscription.unsubscribe();
            this.scanServiceSubscription = null;
        }
    }

    screenDataUpdated() {
        //Since we are checking if the screen is disabled (willUnblock set to false) we need to try and register the scanner
        //on every update just in case we go from disabled to enabled.
        this.registerScanner();
    }

    public onEnter($event: any): void {
        if (this.barcode && this.barcode.trim().length >= this.screenData.scanMinLength) {
            $event.stopImmediatePropagation();
            this.doAction(this.screenData.keyedAction, this.barcode);
            this.barcode = '';
        } else if (this.defaultAction && this.defaultAction.enabled) {
            $event.stopImmediatePropagation();
            this.doAction(this.defaultAction);
        }
    }

    public onValueChange(): void {
        this.change.emit(this.barcode);
    }
}
