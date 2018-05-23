import { IScreen } from '../../common/iscreen';
import { Component, OnInit, DoCheck, OnDestroy } from '@angular/core';
import {SessionService} from '../../services/session.service';
import { ActionIntercepter, ActionIntercepterBehaviorType } from '../../common/action-intercepter';
import { IForm } from '../../screens/form.component';


@Component({
  selector: 'app-self-checkout-options',
  templateUrl: './self-checkout-options.component.html',
  styleUrls: ['./self-checkout-options.component.scss']
})
export class SelfCheckoutOptionsComponent implements IScreen, OnInit, OnDestroy {

  static readonly UNDO = 'Undo';
  screen: any;
  public currentView: string;
  public selectedOption: IOptionItem;
  public optionItems: IOptionItem[];
  public promptText: string;

  constructor(public session: SessionService) {
  }

  show(screen: any) {
    this.screen = screen;
    this.optionItems = this.screen.options;
    this.currentView = this.screen.displayStyle;
    this.promptText = this.screen.prompt;
  }

  ngOnInit(): void {
  }

  ngOnDestroy() {
    this.session.unregisterActionIntercepter(SelfCheckoutOptionsComponent.UNDO);
  }

  onMakeOptionSelection( option: IOptionItem): void {
    if ( option.form.formElements.length > 0 ) {
      this.selectedOption = option;
      this.currentView = 'OptionForm';
      this.session.registerActionIntercepter(SelfCheckoutOptionsComponent.UNDO,
        new ActionIntercepter((payload) => { this.onBackButtonPressed(); }, ActionIntercepterBehaviorType.block));
    } else {
      this.session.onAction( option.value );
    }
  }

  onBackButtonPressed(): void {
    this.currentView = this.screen.displayStyle;
    this.session.unregisterActionIntercepter(SelfCheckoutOptionsComponent.UNDO);
  }

}

export interface IOptionItem {
    displayValue: string;
    value: string;
    enabled: boolean;
    selected: boolean;
    form: IForm;
    icon: string;
}
