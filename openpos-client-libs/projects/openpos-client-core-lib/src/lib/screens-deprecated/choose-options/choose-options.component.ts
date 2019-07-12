
import { Component, OnDestroy, HostListener } from '@angular/core';
import { IOptionItem } from './option-item.interface';
import { IChooseOptionsScreen } from './choose-options-screen.interface';
import { PosScreen } from '../../screens-with-parts/pos-screen.component';
import { Configuration } from '../../configuration/configuration';
import { ActionIntercepter, ActionIntercepterBehaviorType } from '../../core/action-intercepter';
import { ScreenComponent } from '../../shared/decorators/screen-component.decorator';

/**
 * @ignore
 */
@ScreenComponent({
  name: 'ChooseOptions'
})
@Component({
  selector: 'app-choose-options',
  templateUrl: './choose-options.component.html'
})
export class ChooseOptionsComponent extends PosScreen<IChooseOptionsScreen> implements  OnDestroy {

  static readonly UNDO = 'Undo';
  public currentView: string;
  public selectedOption: IOptionItem;
  public optionItems: IOptionItem[];
  public promptText: string;

  buildScreen() {
    this.optionItems = this.screen.options;
    this.currentView = this.screen.displayStyle;
    this.promptText = this.screen.promptText;
  }

  ngOnDestroy() {
    this.session.unregisterActionIntercepter(ChooseOptionsComponent.UNDO);
  }

  onMakeOptionSelection( option: IOptionItem): void {
    if ( option.form.formElements.length > 0 ) {
      this.selectedOption = option;
      this.currentView = 'OptionForm';
      this.session.registerActionIntercepter(ChooseOptionsComponent.UNDO,
        new ActionIntercepter(this.log, (payload) => { this.onBackButtonPressed(); }, ActionIntercepterBehaviorType.block));
    } else {
      this.session.onAction( option.value );
    }
  }

  onBackButtonPressed(): void {
    this.currentView = this.screen.displayStyle;
    this.session.unregisterActionIntercepter(ChooseOptionsComponent.UNDO);
  }

  @HostListener('document:keydown', ['$event'])
  public onKeydown(event: KeyboardEvent) {
    // Map F1 -> F12 to local menu buttons
    if (Configuration.enableKeybinds) {
      const regex = /^F(\d+)$/;
      let bound = false;
      if (regex.test(event.key)) {
        for (const option of this.optionItems) {
          if (option.keybind === event.key) {
            bound = true;
            this.onMakeOptionSelection(option);
          }
        }
      }
      if (bound) {
        event.preventDefault();
      }
    }
  }

  public keybindsEnabled() {
    return Configuration.enableKeybinds;
  }

}
