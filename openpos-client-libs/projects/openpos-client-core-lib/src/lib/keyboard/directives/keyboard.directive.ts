import { Subscription } from 'rxjs';
import { Directive, ElementRef, EventEmitter, HostListener, Input, OnDestroy, Output } from '@angular/core';
import { MatKeyboardComponent } from '../components/keyboard/keyboard.component';
import { MatKeyboardRef } from '../classes/keyboard-ref.class';
import { MatKeyboardService } from '../services/keyboard.service';
import { SessionService } from '../../core/services/session.service';
import { Configuration } from '../../configuration/configuration';
import { IMessageHandler } from '../../core/interfaces/message-handler.interface';

@Directive({
    // tslint:disable-next-line:directive-selector
    selector: 'input:not([type=checkbox]):not([no-keyboard]), textarea:not([no-keyboard])'
})
export class KeyboardDirective implements OnDestroy, IMessageHandler<any> {

    private keyboardRef: MatKeyboardRef<MatKeyboardComponent>;

    private screen: any;

    private screenSubscription: Subscription;

    @Input() keyboardLayout = 'en-US';

    @Input() darkTheme: boolean;

    @Input() duration: number;

    @Input() isDebug: boolean;

    @Output() enterClick: EventEmitter<void> = new EventEmitter<void>();

    @Output() capsClick: EventEmitter<void> = new EventEmitter<void>();

    @Output() altClick: EventEmitter<void> = new EventEmitter<void>();

    @Output() shiftClick: EventEmitter<void> = new EventEmitter<void>();

    constructor(
            public session: SessionService,
            private elementRef: ElementRef,
            private keyboardService: MatKeyboardService) {
        this.screenSubscription = this.session.registerMessageHandler(this, 'Screen');
    }

    ngOnDestroy() {
        this._hideKeyboard();
        this.screenSubscription.unsubscribe();
    }

    handle(message: any) {
        if (message.template && !message.template.dialog) {
            this.screen = message;
        }
    }

    @HostListener('focus', [''])
    public _showKeyboard() {
        if (Configuration.useOnScreenKeyboard) {

            this.keyboardRef = this.keyboardService.open(this.keyboardLayout, {
                darkTheme: true,
                duration: this.duration,
                isDebug: this.isDebug
            });

            // reference the input element
            this.keyboardRef.instance.setInputInstance(this.elementRef);

            // connect outputs
            this.keyboardRef.instance.enterClick.subscribe(() => {
                const event = new Event('submit', { cancelable: true, bubbles: true });
                if (this.elementRef.nativeElement.form) {
                    this.elementRef.nativeElement.form.dispatchEvent(event);
                }
                const enterPressedEvent = new KeyboardEvent('keypress', { key: 'Enter', code: 'Enter', cancelable: true, bubbles: true });
                this.elementRef.nativeElement.dispatchEvent(enterPressedEvent);
                const enterDownEvent = new KeyboardEvent('keydown', { key: 'Enter', code: 'Enter', cancelable: true, bubbles: true });
                this.elementRef.nativeElement.dispatchEvent(enterDownEvent);
                const enterUpEvent = new KeyboardEvent('keyup', { key: 'Enter', code: 'Enter', cancelable: true, bubbles: true });
                this.elementRef.nativeElement.dispatchEvent(enterUpEvent);

                this.enterClick.next();
            });
            this.keyboardRef.instance.capsClick.subscribe(() => this.capsClick.next());
            this.keyboardRef.instance.altClick.subscribe(() => this.altClick.next());
            this.keyboardRef.instance.shiftClick.subscribe(() => this.shiftClick.next());
        }
    }

    @HostListener('blur', [])
    public _hideKeyboard() {
        if (this.keyboardRef) {
            this.keyboardRef.dismiss();
        }
    }

}
