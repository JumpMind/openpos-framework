import { OverlayRef } from '@angular/cdk/overlay';

export class TrainingOverlayRef {

  constructor(private overlayRef: OverlayRef) { }

  close(): void {
    this.overlayRef.dispose();
  }
}
