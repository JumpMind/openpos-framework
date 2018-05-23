import { Directive, Input, ElementRef, forwardRef, Renderer2, OnInit, HostListener } from '@angular/core';
import { ControlValueAccessor, NG_VALUE_ACCESSOR } from '@angular/forms';
import { IFormatter } from './formatters/iformatter';
import { FormattersService } from './../services/formatters.service';

export const FORMATTED_INPUT_VALUE_ACCESSOR: any = {
    provide: NG_VALUE_ACCESSOR,
    useExisting: forwardRef(() => FormattedInputValueAccessor),
    multi: true
};

@Directive({
    // tslint:disable-next-line:directive-selector
    selector: 'input[formatterName]',
    providers: [FORMATTED_INPUT_VALUE_ACCESSOR]
})
// tslint:disable-next-line:directive-class-suffix
export class FormattedInputValueAccessor implements ControlValueAccessor, OnInit {

    @Input() formatterName: string;

    private formatter: IFormatter;

    onChange = (value: string) => { };
    onTouched = () => { };

    constructor(private renderer: Renderer2, private elRef: ElementRef, private formatterService: FormattersService) {
    }

    ngOnInit(): void {
        this.formatter = this.formatterService.getFormatter(this.formatterName);
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

    @HostListener('input', ['$event.target.value'])
    handleInput(value: string) {
        // Clean out any special characters
        const cleanValue = this.formatter.unFormatValue(value);

        const newCaret = this.getCaretPos(value);

        // Our new value to display is a formatted version of the clean value
        const newValue = this.formatter.formatValue(cleanValue);

        this.renderer.setProperty(this.elRef.nativeElement, 'value', newValue);
        this.renderer.setProperty(this.elRef.nativeElement, 'selectionStart', newCaret);
        this.renderer.setProperty(this.elRef.nativeElement, 'selectionEnd', newCaret);

        // We are going to unformat the new value one more time before sending it back to the model
        // This makes sure our model has a true unformatted version of our display value
        this.onChange(this.formatter.unFormatValue(newValue));
    }

    getCaretPos(value: string) {
        // We need to remap the caret position of the raw input string to the now new formatted string
        // Save off the caret position in the raw input
        const caret = this.elRef.nativeElement.selectionStart;

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
