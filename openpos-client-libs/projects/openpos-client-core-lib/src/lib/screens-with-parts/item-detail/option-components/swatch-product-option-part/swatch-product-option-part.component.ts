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
export class SwatchProductOptionPart extends ScreenPartComponent<SwatchProductOptionPartInterface> implements OnInit{
    
    selectedOptionName: String;
    
    constructor( @Optional() private injector: Injector, @Optional() @Inject(OPTION_NAME) private optionName: string ){
        super(injector);
    }

    
    ngOnInit(): void{
        this.screenPartName = 'swatchProductOption' + this.optionName;
        super.ngOnInit();
    }

    screenDataUpdated() {
        this.selectedOptionName = this.screenData.swatches.find(value => value.id === this.screenData.selectedOption).name;
        console.log(this.selectedOptionName)
    }
    
    selectOption(swatch: SwatchInterface){
        this.doAction(this.screenData.selectOptionAction, swatch.id)
    }

}