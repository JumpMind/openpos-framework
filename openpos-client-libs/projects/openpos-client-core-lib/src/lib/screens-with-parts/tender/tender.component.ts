import { TenderScreenInterface } from './tender.interface';
import { Component, Injector } from '@angular/core';
import { ScreenComponent } from '../../shared/decorators/screen-component.decorator';
import { FormGroup } from '@angular/forms';
import { FormBuilder } from '../../core/services/form-builder.service';
import { PosScreen } from '../pos-screen/pos-screen.component';

@ScreenComponent({
    name: 'Tender'
})
@Component({
    selector: 'app-tender',
    templateUrl: './tender.component.html',
    styleUrls: ['./tender.component.scss']
})
export class TenderComponent extends PosScreen<TenderScreenInterface> {
    form: FormGroup;

    constructor( private formBuilder: FormBuilder, injector: Injector ) {
        super(injector);
    }

    buildScreen() {
    }

}
