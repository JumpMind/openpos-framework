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
        this.form = this.formBuilder.group(this.screen.form);
        if ( !!this.screen.tenderTypeActionNames ) {
            this.screen.tenderTypeActionNames.forEach( actionName => {
                this.actionService.registerActionPayload(actionName, () => {
                    if (this.form.valid) {
                        return this.formBuilder.buildFormPayload(this.form, this.screen.form);
                    } else {
                        // Show errors for each of the fields where necessary
                        Object.keys(this.form.controls).forEach(f => {
                            const control = this.form.get(f);
                            control.markAsTouched({ onlySelf: true });
                        });
                        throw Error('form is invalid');
                    }
                });
            });
        }
    }

}
