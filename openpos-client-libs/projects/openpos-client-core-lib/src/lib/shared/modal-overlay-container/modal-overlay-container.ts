import { OverlayContainer } from '@angular/cdk/overlay';

export class ModalOverlayContainer extends OverlayContainer {
    public setContainerElement(elemet: HTMLElement): void {
        this._containerElement = elemet;
    }
}
