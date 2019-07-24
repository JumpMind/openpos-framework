import { Injectable } from '@angular/core';
import { FocusTrap, FocusTrapFactory } from '@angular/cdk/a11y';

@Injectable({
    providedIn: 'root',
})
export class FocusService {

    private focusTrap: FocusTrap;

    constructor(private focusTrapFactory: FocusTrapFactory)  {
    }

    destroy() {
        if (!!this.focusTrap) {
            this.focusTrap.destroy();
        }
    }

    createInitialFocus(element: HTMLElement): Promise<boolean> {
        this.focusTrap = this.focusTrapFactory.create(element);
        return this.focusTrap.focusInitialElementWhenReady();
    }

    restoreInitialFocus() {
        if (!!this.focusTrap) {
            this.focusTrap.focusInitialElement();
        }
    }

    restoreFocus(element: HTMLElement) {
        setTimeout(() => {
            if (element) {
                element.focus();
            }
        });
    }
}
