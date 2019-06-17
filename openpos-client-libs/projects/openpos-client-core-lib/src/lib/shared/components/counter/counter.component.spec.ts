import { TestBed, ComponentFixture } from '@angular/core/testing';
import { CounterComponent } from './counter.component';
import { NO_ERRORS_SCHEMA } from '@angular/core'
import { FormBuilder } from '@angular/forms';


describe('CounterComponent', () => {
    let counterComponent: CounterComponent;
    let fixture: ComponentFixture<CounterComponent>;
    const formBuilder: FormBuilder = new FormBuilder();

    beforeEach( () => {
        TestBed.configureTestingModule({
            declarations: [
                CounterComponent,
            ],
            schemas: [
                NO_ERRORS_SCHEMA,
            ]
        }).compileComponents();
        fixture = TestBed.createComponent(CounterComponent);
        counterComponent = fixture.componentInstance;
        counterComponent.minVal = 1;
        counterComponent.maxVal = 5;
        counterComponent.controlName = 'formValue';
        counterComponent.formGroup = formBuilder.group({
            formValue: ['0']
        });
    });

    describe('checkMinusDisable', () => {

        it('should be disabled when form value is less than min', () => {
            counterComponent.formGroup.value['formValue'] = '0';
            expect(counterComponent.checkMinusDisable()).toBe(true);
        });

        it('should be disabled when form value is not an number', () => {
            counterComponent.formGroup.value['formValue'] = '';
            expect(counterComponent.checkMinusDisable()).toBe(true);
        });

        it('should be disabled when form value is equal to min', () => {
            counterComponent.formGroup.value['formValue'] = '1';
            expect(counterComponent.checkMinusDisable()).toBe(true);
        });

        it('should be enabled when form value is greater than min', () => {
            counterComponent.formGroup.value['formValue'] = '4';
            expect(counterComponent.checkMinusDisable()).toBe(false);
        });
    });

    describe('checkPlusDisable', () => {

        it('should be disabled when form value is greater than max', () => {
            counterComponent.formGroup.value['formValue'] = '6';
            expect(counterComponent.checkPlusDisable()).toBe(true);
        });

        it('should be disabled when form value is not an number', () => {
            counterComponent.formGroup.value['formValue'] = '';
            expect(counterComponent.checkPlusDisable()).toBe(true);
        });

        it('should be disabled when form value is equal to max', () => {
            counterComponent.formGroup.value['formValue'] = '5';
            expect(counterComponent.checkPlusDisable()).toBe(true);
        });

        it('should be enabled when form value is less than max', () => {
            counterComponent.formGroup.value['formValue'] = '4';
            expect(counterComponent.checkPlusDisable()).toBe(false);
        });
    });

    describe('decrementQty', () => {

        beforeEach( () => {
            spyOn(counterComponent.valueChange, 'emit');
        });

        it('should decrement quanity when enabled', () => {
            counterComponent.minusDisabled = false;
            counterComponent.value = '3'
            counterComponent.formGroup.value['formValue'] = counterComponent.value;
            counterComponent.decrementQty();
            expect(counterComponent.value).toBe('2');
            expect(counterComponent.valueChange.emit).toHaveBeenCalledWith('2');
        });

        it('should not decrement quanity when disabled', () => {
            counterComponent.minusDisabled = true;
            counterComponent.value = '3'
            counterComponent.formGroup.value['formValue'] = counterComponent.value;
            counterComponent.decrementQty();
            expect(counterComponent.value).toBe('3');
            expect(counterComponent.valueChange.emit).not.toHaveBeenCalled();
        });
    });

    describe('incrementQty', () => {

        beforeEach( () => {
            spyOn(counterComponent.valueChange, 'emit');
        });

        it('should increment quanity when enabled', () => {
            counterComponent.plusDisabled = false;
            counterComponent.value = '3'
            counterComponent.formGroup.value['formValue'] = counterComponent.value;
            counterComponent.incrementQty();
            expect(counterComponent.value).toBe('4');
            expect(counterComponent.valueChange.emit).toHaveBeenCalledWith('4');
        });

        it('should not increment quanity when disabled', () => {
            counterComponent.plusDisabled = true;
            counterComponent.value = '3'
            counterComponent.formGroup.value['formValue'] = counterComponent.value;
            counterComponent.incrementQty();
            expect(counterComponent.value).toBe('3');
            expect(counterComponent.valueChange.emit).not.toHaveBeenCalled();
        });
    });

    describe('onValueChange', () => {

        beforeEach( () => {
            spyOn(counterComponent.valueChange, 'emit');
        });

        it('should emit new value when changed', () => {
            counterComponent.value = '3'
            counterComponent.formGroup.value['formValue'] = '4';
            counterComponent.onValueChange();
            expect(counterComponent.value).toBe('4');
            expect(counterComponent.valueChange.emit).toHaveBeenCalledWith('4');
        });
    });
});