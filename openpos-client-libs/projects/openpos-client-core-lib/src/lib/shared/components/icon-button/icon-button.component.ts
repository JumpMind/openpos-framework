import { Component, Input, EventEmitter, Output, ViewChild, ElementRef } from '@angular/core';

@Component({
  selector: 'app-icon-button',
  templateUrl: './icon-button.component.html',
  styleUrls: ['./icon-button.component.scss']
})
export class IconButtonComponent {

    @Input() disabled = false;
    @Input() iconName: string;
    @Input() color: string;
    @Input() iconClass = 'mat-24';

    @Output() buttonClick = new EventEmitter();

    @ViewChild('button', { read: ElementRef }) button: ElementRef;

    clickFn(): void {
        this.buttonClick.emit();
    }

    public allowTabbing(allow: boolean): void {
       if ( allow && this.button ) {
           this.button.nativeElement.setAttribute('tabindex', '0');
           console.info('Resetting Tab Index to 0');
       } else if (!allow && this.button ) {
           this.button.nativeElement.setAttribute('tabindex', '-1');
           console.info('Resetting Tab Index to -1');
       }
    }
}
