import {Component, Inject, Injector, OnInit, Optional} from '@angular/core';
import {MatSelectChange} from '@angular/material/select';
import {ScreenPartComponent} from '../../../../shared/screen-parts/screen-part';
import {OPTION_NAME} from '../../item-detail.component';
import {BasicProductOptionPartInterface} from './basic-product-option-part.interface';

@Component({
    selector: 'app-basic-product-options-part',
    templateUrl: './basic-product-option-part.component.html',
    styleUrls: ['./basic-product-option-part.scss']
})
export class BasicProductOptionPart extends ScreenPartComponent<BasicProductOptionPartInterface> implements OnInit{
    
    constructor( @Optional() private injector: Injector, @Optional() @Inject(OPTION_NAME) private optionName: string ){
        super(injector);

    }

    ngOnInit(): void{
        this.screenPartName = 'basicProductOption' + this.optionName;
        super.ngOnInit();
    }


    screenDataUpdated() {
    }
    
    optionSelected(selectChange: MatSelectChange){
        this.doAction(this.screenData.selectOptionAction, selectChange.value);
    }
    
}