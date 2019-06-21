import { Component } from '@angular/core';
import { PosScreen } from '../pos-screen/pos-screen.component';
import { IForm } from '../../core/interfaces/form.interface';
import { IFormElement } from '../../core/interfaces/form-field.interface';
import { IActionItem } from '../../core/interfaces/action-item.interface';
@Component({
    selector: 'app-login',
    templateUrl: './login.component.html',
    styleUrls: ['./login.component.scss']
  })
  export class LoginComponent extends PosScreen<any> {

    public form: IForm;
    public screen: any;
    loginIdField: IFormElement;
    passwordField: IFormElement;
    submitAction: string;
    changePasswordAction: IActionItem;
    forgotPasswordAction: IActionItem;
    okButton: IFormElement;
    cancelButton: IFormElement;
    title: string;

    constructor() {
        super();
    }

    buildScreen(): void {
        this.form = this.screen.form;
        this.loginIdField = this.form.formElements.find((e) => e.id === 'userId');
        if (!this.loginIdField.pattern) {
            this.loginIdField.pattern = '[0-9]*';
        }
        this.passwordField = this.form.formElements.find((e) => e.id === 'password');
        this.okButton = this.form.formElements.find((e) => e.id === 'okButton');
        this.cancelButton = this.form.formElements.find((e) => e.id === 'cancelButton');
        this.submitAction = this.screen.submitAction;
        this.forgotPasswordAction = this.screen.forgotPasswordAction;
        this.changePasswordAction = this.screen.changePasswordAction;
        this.title = this.form.name;
    }

    hasSubmitAction(): boolean {
        return this.submitAction !== null;
    }

    onEnterPressed(): void {
        if (this.hasSubmitAction()) {
            this.onSubmitAction();
        } else {
            this.onOkButton();
        }
    }

    onSubmitAction(): void {
        this.session.onAction(this.submitAction, this.form);
    }

    onOkButton(): void {
        this.session.onAction(this.okButton.buttonAction, this.form);
    }

    onChangePasswordAction(): void {
        this.session.onAction(this.changePasswordAction.action, this.form);
    }

    onForgotPasswordAction(): void {
        this.session.onAction(this.forgotPasswordAction, this.form);
    }
}

