import { IScreen } from '../../common/iscreen';
import { Component, OnInit, OnDestroy } from '@angular/core';
import {SessionService} from '../../services/session.service';
import { IForm } from './../form.component';
import { ActionIntercepter, ActionIntercepterBehaviorType } from '../../common/action-intercepter';
import { IChooseOptionsScreen, DisplayStyle } from './ichooseOptionsScreen';


@Component({
  selector: 'app-choose-options',
  templateUrl: './choose-options.component.html'
})
export class ChooseOptionsComponent implements IScreen, OnInit,  OnDestroy {

  static readonly UNDO = 'Undo';
  public currentView: string;
  public selectedOption: IOptionItem;
  public optionItems: IOptionItem[];
  public promptText: string;

  screen: any;

  constructor(public session: SessionService) {
  }

  show(screen: any) {
    this.screen = screen;
    this.optionItems = this.screen.options;
    this.currentView = this.screen.displayStyle;
    this.promptText = this.screen.promptText;
  }

  ngOnInit(): void {
  }

  ngOnDestroy() {
    this.session.unregisterActionIntercepter(ChooseOptionsComponent.UNDO);
  }

  onMakeOptionSelection( option: IOptionItem): void {
    if ( option.form.formElements.length > 0 ) {
      this.selectedOption = option;
      this.currentView = 'OptionForm';
      this.session.registerActionIntercepter(ChooseOptionsComponent.UNDO,
        new ActionIntercepter((payload) => { this.onBackButtonPressed(); }, ActionIntercepterBehaviorType.block));
    } else {
      this.session.onAction( option.value );
    }
  }

  onBackButtonPressed(): void {
    this.currentView = this.screen.displayStyle;
    this.session.unregisterActionIntercepter(ChooseOptionsComponent.UNDO);
  }

}

export interface IOptionItem {
    displayValue: string;
    value: string;
    enabled: boolean;
    selected: boolean;
    form: IForm;
}
