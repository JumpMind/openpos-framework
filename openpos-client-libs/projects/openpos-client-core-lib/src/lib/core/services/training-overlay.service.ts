import { Injectable, Injector, ComponentRef } from '@angular/core';
import { Overlay, OverlayConfig, OverlayRef } from '@angular/cdk/overlay';
import { ComponentPortal, PortalInjector } from '@angular/cdk/portal';
import { TrainingOverlayRef } from '../../shared/components/training-dialog/training-overlay-ref';
import { TrainingWrapperComponent } from '../../shared/components/training-dialog/training-wrapper.component';

@Injectable()
export class TrainingOverlayService {

  constructor(public injector: Injector, private overlay: Overlay) { }

  open() {
    // Returns an OverlayRef which is a PortalHost
    const overlayRef = this.createOverlay();

    // Instantiate remote control
    const dialogRef = new TrainingOverlayRef(overlayRef);

    // Create and attach the dialog component
    const overlayComponent = this.attachDialogContainer(overlayRef, dialogRef);

    // Close dialog on backdrop click
    overlayRef.backdropClick().subscribe(_ => dialogRef.close());

    return dialogRef;
  }

  private createOverlay() {
    const overlayConfig = this.getOverlayConfig();
    return this.overlay.create(overlayConfig);
  }

  private attachDialogContainer(overlayRef: OverlayRef, dialogRef: TrainingOverlayRef) {
    const portalInjector = this.createInjector(dialogRef);

    // Create component portal (i.e. the component to display as a dialog)
    const containerPortal = new ComponentPortal(TrainingWrapperComponent, null, portalInjector);

    // Attach the component portal to the overlay
    const containerRef: ComponentRef<TrainingWrapperComponent> = overlayRef.attach(containerPortal);

    return containerRef.instance;
  }

  private createInjector(dialogRef: TrainingOverlayRef): PortalInjector {
    const injectionTokens = new WeakMap();

    // Pass the dialogRef to the TrainingWrapperComponent
    injectionTokens.set(TrainingOverlayRef, dialogRef);

    return new PortalInjector(this.injector, injectionTokens);
  }

  private getOverlayConfig(): OverlayConfig {
    // Position the dialog
    const positionStrategy = this.overlay.position()
      .global()
      .centerHorizontally()
      .centerVertically();

    // Create overlay config
    const overlayConfig = new OverlayConfig({
      hasBackdrop: true,
      scrollStrategy: this.overlay.scrollStrategies.block(),
      positionStrategy
    });

    return overlayConfig;
  }

}
