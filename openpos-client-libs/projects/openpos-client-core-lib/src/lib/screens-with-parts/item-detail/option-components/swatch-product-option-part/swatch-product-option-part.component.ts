import {Component, Inject, Injector, OnInit, Optional} from '@angular/core';
import {ScreenPart} from '../../../../shared/decorators/screen-part.decorator';
import {ScreenPartComponent} from '../../../../shared/screen-parts/screen-part';
import {OPTION_NAME} from '../../item-detail.component';
import {SwatchProductOptionPartInterface} from './swatch-product-option-part.interface';
import {SwatchInterface} from './swatch.interface';

@ScreenPart( {
    name: 'swatchProductOption'
})
@Component({
    selector: 'app-swatch-product-options-part',
    templateUrl: './swatch-product-option-part.component.html',
    styleUrls: ['./swatch-product-option-part.scss']
})
export class SwatchProductOptionPart extends ScreenPartComponent<SwatchProductOptionPartInterface> implements OnInit {
    selectedOptionName: string;
    useImageSwatch = true;
    
    constructor( @Optional() injector: Injector, @Optional() @Inject(OPTION_NAME) private optionName: string ) {
        super(injector);
    }

    
    ngOnInit(): void{
        this.screenPartName = 'swatchProductOption' + this.optionName;
        super.ngOnInit();
    }

    screenDataUpdated() {
        if (this.screenData && this.screenData.swatches) {
            let swatch = this.screenData.swatches.find(value => value.id === this.screenData.selectedOption);
            
            if (swatch) {
                this.selectedOptionName = swatch.name;
            } else {
                this.selectedOptionName = '';
            }
        } else {
            this.selectedOptionName = '';
        }

        console.log(this.selectedOptionName)
    }
    
    selectOption(swatchId: string){
        console.log("selected", swatchId);
        this.doAction(this.screenData.selectOptionAction, swatchId);
    }

    onImageLoadFailed() {
        this.useImageSwatch = false;
    }
}