import { Directive, Input, ElementRef, forwardRef, Renderer2, HostListener } from '@angular/core';
import { ControlValueAccessor, NG_VALUE_ACCESSOR } from '@angular/forms';
import { Platform } from '@angular/cdk/platform';
import { IFormatter } from '../formatters/formatter.interface';
import { FormattersService } from '../../core/services/formatters.service';

export const FORMATTED_INPUT_VALUE_ACCESSOR: any = {
    provide: NG_VALUE_ACCESSOR,
    useExisting: forwardRef(() => InputFormatterDirective),
    multi: true
};

@Directive({
    // tslint:disable-next-line:directive-selector
    selector: 'input[formatterName]',
    providers: [FORMATTED_INPUT_VALUE_ACCESSOR]
})
// tslint:disable-next-line:directive-class-suffix
export class InputFormatterDirective implements ControlValueAccessor {

    // tslint:disable-next-line:variable-name
    private _formatterName: string;

    private formatter: IFormatter;

    onChange = (value: string) => { };
    onTouched = () => { };

    constructor(private renderer: Renderer2, private elRef: ElementRef, private formatterService: FormattersService,
                private platform: Platform) {
    }

    @Input()
    set formatterName(formatterName: string) {
        this._formatterName = formatterName;
        this.formatter = this.formatterService.getFormatter(this.formatterName);
    }

    get formatterName(): string {
        return this._formatterName;
    }

    writeValue(value: string): void {
        if (!value) {
            value = '';
        }
        this.renderer.setProperty(this.elRef.nativeElement, 'value', this.formatter.formatValue(value));
    }

    registerOnChange(fn: (tel: string) => void): void {
        this.onChange = fn;
    }

    registerOnTouched(fn: any): void {
        this.onTouched = fn;
    }

    @HostListener('blur', ['$event'])
    handleBlur(event) {
        this.onTouched();

        // The input formatter is not triggering the change event in webkit,
        // Manually dispatching the change event on blur.
        if (this.platform.WEBKIT) {
            this.elRef.nativeElement.dispatchEvent(new Event('change'));
        }
    }

    @HostListener('input', ['$event'])
    handleInput(event) {
        const value = event.target.value;

        // Clean out any special characters
        const cleanValue = this.formatter.unFormatValue(value);

        let caret: number;
        if (event.detail) {
            caret = event.detail;
        } else {
            caret = this.elRef.nativeElement.selectionStart;
        }

        const newCaret = this.getCaretPos(value, caret);

        // Our new value to display is a formatted version of the clean value
        const newValue = this.formatter.formatValue(cleanValue);

        this.renderer.setProperty(this.elRef.nativeElement, 'value', newValue);
        this.renderer.setProperty(this.elRef.nativeElement, 'selectionStart', newCaret);
        this.renderer.setProperty(this.elRef.nativeElement, 'selectionEnd', newCaret);

        // We are going to unformat the new value one more time before sending it back to the model
        // This makes sure our model has a true unformatted version of our display value
        this.onChange(this.formatter.unFormatValue(newValue));
    }

    getCaretPos(value: string, caret: number) {
        // Get the cleaned substring of the raw value before the caret
        const beforeCaretClean = this.formatter.unFormatValue(value.slice(0, caret));

        // Format the substring
        const beforeCaretFormatted = this.formatter.formatValue(beforeCaretClean);
        let newCaret = 0;
        let i = 0;

        // Loop through the cleaned and formatted substrings to find where the new caret should be
        while (newCaret < beforeCaretFormatted.length && i < beforeCaretClean.length) {
            if (beforeCaretClean[i] === beforeCaretFormatted[newCaret]) {
                ++newCaret;
                ++i;
            } else {
                ++newCaret;
            }
        }

        return newCaret;
    }

    @HostListener('keypress', ['$event.key', '$event.target.value'])
    onKeyPress(key: string, value: string) {
        // Compute what our proposed new value is going to look like
        const newValue = value.slice(0,
            this.elRef.nativeElement.selectionStart) + key + value.slice(this.elRef.nativeElement.selectionEnd, value.length);

        // Ask the formatter to validate the addition either with the key or new value
        if (!this.formatter.allowKey(key, this.formatter.unFormatValue(newValue)) && key !== 'Enter') {
            event.preventDefault();
        }
    }

}
