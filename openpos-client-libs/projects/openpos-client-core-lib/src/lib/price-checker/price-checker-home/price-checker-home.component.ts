import { Component, OnDestroy, OnInit } from '@angular/core';
import { PriceCheckerHomeInterface } from './price-checker-home.interface';
import { PosScreen } from '../../screens-deprecated/pos-screen/pos-screen.component';
import { ScreenComponent } from '../../shared/decorators/screen-component.decorator';
import { ImageService } from '../../core/services/image.service';
import { ScannerService } from '../../core/platform-plugins/scanners/scanner.service';
import { Subscription } from 'rxjs';

@ScreenComponent({
    name: 'PriceCheckerHome'
})
@Component({
  selector: 'app-price-checker-home',
  templateUrl: './price-checker-home.component.html',
  styleUrls: ['./price-checker-home.component.scss']
})
export class PriceCheckerHomeComponent extends PosScreen<PriceCheckerHomeInterface> implements OnDestroy, OnInit {

    backgroundStyle = {};

    scannerSubscription: Subscription;

    constructor( private imageService: ImageService, private scannerService: ScannerService) {
        super();        
    }

    ngOnInit(): void {
        this.scannerSubscription = this.scannerService.startScanning().subscribe( m => this.onMenuItemClick(this.screen.scanAction, m));
    }

    buildScreen() {
        const url = this.imageService.replaceImageUrl(this.screen.backgroundImageUrl);
        this.backgroundStyle = {
            'background-image': `url(${url})`,
            'background-repeat': 'no-repeat',
            'background-size': 'contain',
            'background-position': 'center'
        };
    }

    ngOnDestroy(): void {
        this.scannerSubscription.unsubscribe();
        this.scannerService.stopScanning();
    }
}
