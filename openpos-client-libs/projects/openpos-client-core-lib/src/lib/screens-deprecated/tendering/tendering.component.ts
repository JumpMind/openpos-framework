import { Component, OnDestroy } from '@angular/core';
import { PosScreen } from '../pos-screen/pos-screen.component';
import { FormGroup, ValidatorFn, FormControl } from '@angular/forms';
import { ScreenComponent } from '../../shared/decorators/screen-component.decorator';
import { IItem } from '../../core/interfaces/item.interface';
import { IFormElement } from '../../core/interfaces/form-field.interface';
import { IActionItem } from '../../core/interfaces/action-item.interface';
import { ValidatorsService } from '../../core/services/validators.service';
import { ActionIntercepter, ActionIntercepterBehavior, ActionIntercepterBehaviorType } from '../../core/action-intercepter';

/**
 * @ignore
 */
@ScreenComponent({
    name: 'Tendering'
})
@Component({
    selector: 'app-tendering',
    templateUrl: './tendering.component.html'
})
export class TenderingComponent extends PosScreen<any> implements OnDestroy {

    text: string;
    tenderItems: IItem[];
    tenderAmount: IFormElement;
    balanceDueAmount: IFormElement;
    balanceDue: string;
    totalAmount: IFormElement;
    itemActions: IActionItem[] = [];
    actionButton: IActionItem;

    tenderFormGroup: FormGroup;

    constructor(private validatorsService: ValidatorsService) {
        super();
    }

    ngOnDestroy(): void {
        if (this.screen.template.localMenuItems) {
            this.screen.template.localMenuItems.forEach(element => {
                this.session.unregisterActionPayload(element.action);
                this.session.unregisterActionIntercepter(element.action);
            });
        }
    }

    buildScreen(): void {
        this.text = this.screen.text;
        this.tenderItems = this.screen.tenderItems;
        this.tenderAmount = this.screen.tenderAmount;

        const group: any = {};
        const validators: ValidatorFn[] = [];

        if (this.tenderAmount.validators) {
            this.tenderAmount.validators.forEach(v => {
                const fn = this.validatorsService.getValidator(v);
                if (fn) {
                    validators.push(fn);
                }
            });
        }

        let disabled = false;
        if (this.tenderAmount.disabled) {
            disabled = this.tenderAmount.disabled;
        }
        group['tenderAmtFld'] = new FormControl({value: this.tenderAmount.value, disabled: disabled}, validators);
        this.tenderFormGroup = new FormGroup(group);


        this.balanceDue = this.screen.balanceDue;
        this.balanceDueAmount = this.screen.balanceDueAmount;
        this.totalAmount = this.screen.totalAmount;
        this.itemActions = this.screen.itemActions;
        this.actionButton = this.screen.actionButton;

        if (this.screen.template.localMenuItems) {
            this.screen.template.localMenuItems.forEach(element => {
                this.session.registerActionPayload(element.action, () => this.tenderFormGroup.get('tenderAmtFld').value);
                this.session.registerActionIntercepter(element.action,
                    new ActionIntercepter(this.log, (payload) => {
                        const value = this.tenderFormGroup.get('tenderAmtFld').value;
                        this.log.info(`Returning value of ${value}.  Payload: ${JSON.stringify(payload)}`);
                        return value;
                    },
                        // Will only block if the formGroup is inValid
                        new ActionIntercepterBehavior(ActionIntercepterBehaviorType.block,
                            tenderAmtValue => this.isTenderValid()
                        )
                    )
                );
            });
            //                this.session.registerActionPayload( element.action, () => this.tenderFormGroup.get('tenderAmtFld').value );
            //            });
        }

    }

    onFormSubmit(): void {
        this.onAction();
    }

    isTenderValid(): boolean {
        return this.tenderFormGroup.valid || this.tenderFormGroup.get('tenderAmtFld').disabled === true;
    }

    onAction(): void {
        if (this.isTenderValid()) {
            this.tenderAmount.value = this.tenderFormGroup.get('tenderAmtFld').value;
            this.session.onAction(this.actionButton.action, this.tenderAmount.value);
        }
    }

}
