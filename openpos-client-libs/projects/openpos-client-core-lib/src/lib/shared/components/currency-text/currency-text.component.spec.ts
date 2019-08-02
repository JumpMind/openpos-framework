import { TestBed } from '@angular/core/testing';
import { CurrencyTextComponent } from './currency-text.component';
import { LocaleService } from '../../../core/services/locale.service';

describe('Currency Text Component', () => {
    let sut: CurrencyTextComponent;
    let localeService: jasmine.SpyObj<LocaleService>;

    beforeEach(() => {
        const localeServiceSpy = jasmine.createSpyObj('LocaleService', ['getLocale', 'getConstant']);
        TestBed.configureTestingModule({
          // provide the component-under-test and dependent service
          providers: [
            CurrencyTextComponent,
            { provide: LocaleService, useValue: localeServiceSpy }
          ]
        });
        // inject both the component and the dependent service.
        sut = TestBed.get(CurrencyTextComponent);
        localeService = TestBed.get(LocaleService);
        localeService.getLocale.and.returnValue('en-US');
        localeService.getConstant.and.returnValue('$');
      });

    it('Should use the input amount test if the currency pipe fails', () => {
        sut.amountText = 'N/A';
        sut.ngOnChanges();
        expect(sut.textAfterSymbol).toBe('N/A');
      });
});
