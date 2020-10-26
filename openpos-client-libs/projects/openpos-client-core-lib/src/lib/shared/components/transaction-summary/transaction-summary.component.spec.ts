import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { TransactionSummaryComponent } from './transaction-summary.component';
import { Component, Input } from '@angular/core';
import { ActionService } from '../../../core/actions/action.service';
import { MatChipsModule } from '@angular/material';

describe('TransactionSummaryComponent', () => {
  let component: TransactionSummaryComponent;
  let fixture: ComponentFixture<TransactionSummaryComponent>;

  beforeEach(async(() => {

    TestBed.configureTestingModule({
      imports: [MatChipsModule],
      declarations: [
        TransactionSummaryComponent,
        MockIconComponent,
        MockCurrencyTextComponent,
      ],
      providers: [
        { provide: ActionService }
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TransactionSummaryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

@Component({
  selector: 'app-icon',
  template: '',
})
class MockIconComponent {
  @Input() iconName: string;
  @Input() iconClass: string;
}

@Component({
  selector: 'app-currency-text',
  template: '',
})
class MockCurrencyTextComponent {
  @Input() amountText: string;
}
