import { Component, OnDestroy } from '@angular/core';
import { PriceCheckerHomeInterface } from './price-checker-home.interface';
import { PosScreen } from '../../screens-deprecated/pos-screen/pos-screen.component';
import { ScreenComponent } from '../../shared/decorators/screen-component.decorator';
import { ImageService } from '../../core/services/image.service';
import { ScannerService } from '../../core/services/scanner.service';

@ScreenComponent({
    name: 'PriceCheckerHome'
})
@Component({
  selector: 'app-price-checker-home',
  templateUrl: './price-checker-home.component.html',
  styleUrls: ['./price-checker-home.component.scss']
})
export class PriceCheckerHomeComponent extends PosScreen<PriceCheckerHomeInterface> implements OnDestroy {

    backgroundStyle = {};

    constructor( private imageService: ImageService, private scannerService: ScannerService) {
        super();
    }

    buildScreen() {
        const url = this.imageService.replaceImageUrl(this.screen.backgroundImageUrl);
        this.backgroundStyle = {
            'background-image': `url(${url})`,
            'background-repeat': 'no-repeat',
            'background-size': 'contain',
            'background-position': 'center'
        };

        this.scannerService.startScanning().subscribe( m => this.onMenuItemClick(this.screen.scanAction, m));
    }

    ngOnDestroy(): void {
        this.scannerService.stopScanning();
    }
}
