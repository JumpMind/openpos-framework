import {
    AfterViewInit, ChangeDetectorRef,
    Component,
    EventEmitter,
    Injector,
    Input, OnInit,
    Output,
    QueryList,
    ViewChild,
    ViewChildren
} from '@angular/core';
import {FormGroup} from '@angular/forms';
import {ScreenPartComponent} from '../screen-part';
import {FormBuilder} from '../../../core/services/form-builder.service';
import {DynamicFormFieldComponent} from '../../components/dynamic-form-field/dynamic-form-field.component';
import {ShowErrorsComponent} from '../../components/show-errors/show-errors.component';
import {IForm} from '../../../core/interfaces/form.interface';
import {IFormElement} from '../../../core/interfaces/form-field.interface';
import {IActionItem} from '../../../core/actions/action-item.interface';
import {IDynamicFormPartEventArg} from './dynamic-form-part-event-arg.interface';
import { takeUntil, tap } from 'rxjs/operators';

@Component({
    selector: 'app-dynamic-form-part',
    templateUrl: './dynamic-form-part.component.html',
    styleUrls: ['./dynamic-form-part.component.scss']
})
export class DynamicFormPartComponent extends ScreenPartComponent<IForm> implements OnInit, AfterViewInit {
    @Output() formInit = new EventEmitter<IDynamicFormPartEventArg>();
    @Output() formChanges = new EventEmitter<IDynamicFormPartEventArg>();
    @ViewChildren(DynamicFormFieldComponent) children: QueryList<DynamicFormFieldComponent>;
    @ViewChild('formErrors') formErrors: ShowErrorsComponent;
    form: FormGroup;

    buttons: IFormElement[];
    disableSubmitButton: boolean;

    private _alternateSubmitActions: string[];
    private  lastFocusedId;


    @Input() set formName(name: string) {
        this.screenPartName = name;
    }

    @Input() submitButton: IActionItem;

    constructor(private changeDetectorRef: ChangeDetectorRef, private formBuilder: FormBuilder, injector: Injector) {
        super(injector);
    }

    screenDataUpdated() {
        this.buttons = new Array<IFormElement>();

        // Before we rebuild what element has focus so we can restore
        if(this.children){
            let focusedField = this.children.find(item => item.field && item.field.focused);
            if(focusedField){
                this.lastFocusedId = focusedField.formField.id;
            }
        }

        this.form = this.formBuilder.group(this.screenData);

        this.form.statusChanges
            .pipe(
                tap(() => this.disableSubmitButton = this.isFormInvalid()),
                takeUntil(this.beforeScreenDataUpdated$)
            )
            // Let Angular know that changes in a child view (app-dynamic-form-field) will cause the UI
            // in this control to update
            .subscribe(() => this.changeDetectorRef.detectChanges());

        this.formInit.emit({
            form: this.screenData,
            formGroup: this.form
        });

        this.form.valueChanges.subscribe(value => {
            this.formBuilder.buildFormPayload(this.form, this.screenData);
            this.formChanges.emit({
                form: this.screenData,
                formGroup: this.form
            });
        });


        if (this.screenData && this.screenData.formElements) {
            this.screenData.formElements.forEach(element => {
                if (element.elementType === 'Button') {
                    this.buttons.push(element);
                }
            });
        }
    }

    isFormInvalid(): boolean {
        return this.form.invalid || this.form.pending;
    }

    ngOnInit() {
        super.ngOnInit();
        this.disableSubmitButton = this.isFormInvalid();
    }

    ngAfterViewInit(): void {
        this.children.changes.subscribe( (changes) => {
            if( this.lastFocusedId ){
                let elementToFocus = this.children.find(item => item.formField.id === this.lastFocusedId);
                setTimeout(() => elementToFocus.focus());
            }
        });

    }

    @Input()
    get alternateSubmitActions(): string[] {
        return this._alternateSubmitActions;
    }

    set alternateSubmitActions(actions: string[]) {
        this._alternateSubmitActions = actions;
        if (actions) {
            actions.forEach(action => {

                this.actionService.registerActionPayload(action, () => {
                    if (this.form.valid) {
                        this.formBuilder.buildFormPayload(this.form, this.screenData);
                        return this.screenData;
                    } else {
                        // Show errors for each of the fields where necessary
                        Object.keys(this.form.controls).forEach(f => {
                            const control = this.form.get(f);
                            control.markAsTouched({onlySelf: true});
                        });
                        throw Error('form is invalid');
                    }
                });
            });
        }
    }

    submitForm() {
        if (this.form.valid && !this.form.pending) {
            this.formBuilder.buildFormPayload(this.form, this.screenData);
            this.doAction(this.submitButton, this.screenData);
        }
    }

    onFieldChanged(formElement: IFormElement) {
        if (formElement.valueChangedAction) {
            let form = this.formBuilder.buildFormPayload(this.form, this.screenData);
            this.doAction( formElement.valueChangedAction, form);
        }
    }
}

