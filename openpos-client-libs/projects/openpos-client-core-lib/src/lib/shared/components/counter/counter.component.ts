import { Component, OnInit, Input, EventEmitter, Output } from '@angular/core';
import { FormGroup } from '@angular/forms';

@Component({
    selector: 'app-counter',
    templateUrl: './counter.component.html',
    styleUrls: ['./counter.component.scss']
  })

  export class CounterComponent implements OnInit {

    @Input() formGroup: FormGroup;
    @Input() controlName: string;
    @Input() label: string;
    @Input() value: string;
    @Input() minVal: number;
    @Input() maxVal: number;
    @Input() required: boolean;
    @Input() readOnly: boolean;
    @Input() keyboardLayout: string;
    @Input() attrType: string;
    @Input() placeholder: string;
    @Input() iconName: string;

    @Output() valueChange = new EventEmitter<any>();

    minusDisabled: boolean;
    plusDisabled: boolean;

    ngOnInit(): void {
        this.plusDisabled = this.checkPlusDisable();
        this.minusDisabled = this.checkMinusDisable();
    }

    decrementQty() {
        if (!this.minusDisabled) {
            let val = this._parseFormValue();
            val--;
            this.value = val.toString();
            this.formGroup.controls[this.controlName].setValue(this.value);
            this.minusDisabled = this.checkMinusDisable();
            this.plusDisabled = this.checkPlusDisable();
            this.valueChange.emit(this.value);
        }
      }

    incrementQty() {
        if (!this.plusDisabled) {
            let val = this._parseFormValue();
            val++;
            this.value = val.toString();
            this.formGroup.controls[this.controlName].setValue(this.value);
            this.plusDisabled = this.checkPlusDisable();
            this.minusDisabled = this.checkMinusDisable();
            this.valueChange.emit(this.value);
        }
    }

    onValueChange() {
        this.plusDisabled = this.checkPlusDisable();
        this.minusDisabled = this.checkMinusDisable();
        this.value = this.formGroup.value[this.controlName];
        this.valueChange.emit(this.value);
    }

    checkMinusDisable(): boolean {
        const formValue = this._parseFormValue();
        if (isNaN(formValue) || (this.minVal != null && formValue <= this.minVal)) {
            return true;
        }
        return false;
    }

    checkPlusDisable(): boolean {
        const formValue = this._parseFormValue();
        if (isNaN(formValue) || (this.maxVal != null && formValue >= this.maxVal)) {
            return true;
        }
        return false;
    }

    private _parseFormValue(): number {
        return parseInt(this.formGroup.value[this.controlName], 10);
    }
}
