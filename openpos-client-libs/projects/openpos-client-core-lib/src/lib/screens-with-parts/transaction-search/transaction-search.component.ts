import { Component, Injector } from '@angular/core';
import { TransactionSearchInterface } from './transaction-search.interface';
import { ScreenComponent } from '../../shared/decorators/screen-component.decorator';
import { PosScreen } from '../pos-screen/pos-screen.component';
import { TransactionSearchMode } from './transaction-search-mode.enum';
import { IDynamicFormPartEventArg } from '../../shared/screen-parts/dynamic-form-part/dynamic-form-part-event-arg.interface';
import { IForm } from '../../core/interfaces/form.interface';
import { UIDataMessageService } from '../../core/ui-data-message/ui-data-message.service';
import { takeUntil } from 'rxjs/operators';
import { merge } from 'rxjs';

@ScreenComponent({
  name: 'TransactionSearch'
})
@Component({
  selector: 'app-transaction-search',
  templateUrl: './transaction-search.component.html',
  styleUrls: ['./transaction-search.component.scss']
})
export class TransactionSearchComponent extends PosScreen<TransactionSearchInterface> {
  searchAllParamsForm: IForm;
  resultsCount: number;
  TransactionSearchMode = TransactionSearchMode;
  changeSearchModeDisabled: boolean;

  constructor(injector: Injector, private dataMessageService: UIDataMessageService) {
    super(injector);
  }

  buildScreen() {
    this.dataMessageService.getData$(this.screen.providerKey)
        .pipe(
            takeUntil(merge(this.destroyed$, this.beforeBuildScreen$))
        )
        .subscribe(results => this.resultsCount = results.length);
    this.changeSearchModeDisabled = !this.screen.changeSearchModeButton;
  }

  onSearchAllFormChanges(event: IDynamicFormPartEventArg): void {
    this.searchAllParamsForm = event.form;
  }

  searchAll(): void {
    this.doAction(this.screen.searchAllButton.action, this.searchAllParamsForm);
  }
}
