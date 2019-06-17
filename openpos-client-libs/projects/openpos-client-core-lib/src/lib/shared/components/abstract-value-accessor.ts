import { forwardRef } from '@angular/core';
import { ControlValueAccessor, NG_VALUE_ACCESSOR } from '@angular/forms';

/**
 * Custom components can inherit from this class in order to be able to use
 * ngModel binding.
 */

 // TODO Doesn't look like anything uses this we can probably remove it.
export abstract class AbstractValueAccessor implements ControlValueAccessor {
    _value: any = '';
    get value(): any { return this._value; }
    set value(v: any) {
      if (v !== this._value) {
        this._value = v;
        this.onChange(v);
      }
    }

    writeValue(value: any) {
      this._value = value;
      // warning: comment below if only want to emit on user intervention
      // this.onChange(value);
    }

    onChange = (_) => {};
    onTouched = () => {};
    registerOnChange(fn: (_: any) => void): void { this.onChange = fn; }
    registerOnTouched(fn: () => void): void { this.onTouched = fn; }
}

export function MakeProvider(type: any) {
  return {
    provide: NG_VALUE_ACCESSOR,
    useExisting: forwardRef(() => type),
    multi: true
  };
}
