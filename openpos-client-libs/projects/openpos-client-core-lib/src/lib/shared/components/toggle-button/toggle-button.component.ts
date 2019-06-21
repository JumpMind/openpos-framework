import { Component, Input, Output, EventEmitter, ContentChild, HostListener, Renderer2 } from '@angular/core';
import { MatButton } from '@angular/material';

@Component({
    selector: 'app-toggle-button',
    templateUrl: './toggle-button.component.html',
    styleUrls: ['./toggle-button.component.scss']
})
export class ToggleButtonComponent {
    @Input()
    value: string;

    @Input()
    selected: boolean;

    @Input()
    allowUncheck = true;

    @Output()
    selectedChange = new EventEmitter();

    @Output()
    change: EventEmitter<ToggleChange> = new EventEmitter<ToggleChange>();

    @ContentChild(MatButton)
    button: MatButton;

    constructor(private renderer: Renderer2) {
    }

    @HostListener('click')
    onClick() {
        if ((!this.allowUncheck && this.selected) || this.button.disabled) {
            return;
        }

        this.setSelected(!this.selected);
        this.change.emit(new ToggleChange(this, this.value));

    }

    setSelected(value: boolean) {
        this.selected = value;
        if (!!value) {
            this.renderer.addClass(this.button._elementRef.nativeElement, 'selected');
        }
        else {
            this.renderer.removeClass(this.button._elementRef.nativeElement, 'selected');
        }
        this.selectedChange.emit(this.selected);
    }

}

export class ToggleChange {
    constructor(public source: ToggleButtonComponent, public value: any) { }
}
