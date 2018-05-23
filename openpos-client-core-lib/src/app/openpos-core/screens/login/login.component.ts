import { IForm } from '../form.component';
import { SessionService } from '../../services/session.service';
import { IScreen } from '../../common/iscreen';
import { DoCheck, OnInit, Component } from '@angular/core';
import { IMenuItem } from '../../common/imenuitem';
import { IFormElement } from '../../common/iformfield';
@Component({
    selector: 'app-login',
    templateUrl: './login.component.html',
    styleUrls: ['./login.component.scss']
  })
  export class LoginComponent implements IScreen {

    public form: IForm;
    public screen: any;
    loginIdField: IFormElement;
    passwordField: IFormElement;
    submitAction: string;
    changePasswordAction: IMenuItem;
    forgotPasswordAction: IMenuItem;
    okButton: IFormElement;
    cancelButton: IFormElement;
    title: string;

    constructor(public session: SessionService) {
    }

    show(screen: any): void {
        this.screen = screen;
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
        this.session.response = this.form;
        this.session.onAction(this.submitAction);
    }

    onOkButton(): void {
        this.session.response = this.form;
        this.session.onAction(this.okButton.buttonAction);
    }

    onChangePasswordAction(): void {
        this.session.response = this.form;
        this.session.onAction(this.changePasswordAction.action);
    }

    onForgotPasswordAction(): void {
        this.session.response = this.form;
        this.session.onAction(this.forgotPasswordAction);
    }
}

