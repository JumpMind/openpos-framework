import { Component, Injector } from '@angular/core';
import { TransactionSearchInterface } from './transaction-search.interface';
import { ScreenComponent } from '../../shared/decorators/screen-component.decorator';
import { PosScreen } from '../pos-screen/pos-screen.component';

@ScreenComponent({
  name: 'TransactionSearch'
})
@Component({
  selector: 'app-transaction-search',
  templateUrl: './transaction-search.component.html',
  styleUrls: ['./transaction-search.component.scss']
})
export class TransactionSearchComponent extends PosScreen<TransactionSearchInterface> {


  constructor(injector: Injector) {
    super(injector);
  }

  buildScreen() {
  }

}
