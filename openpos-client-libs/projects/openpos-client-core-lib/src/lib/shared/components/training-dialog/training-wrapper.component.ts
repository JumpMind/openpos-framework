import { Component, Inject, Injector, ReflectiveInjector, ComponentRef } from '@angular/core';
import { TrainingOverlayRef } from './training-overlay-ref';
import { INSTRUCTIONS_DATA } from './training-token';
import { TrainingDialogComponent } from './training-dialog.component';
import { TrainingElement } from './training-element';

@Component({
    selector: 'app-training-wrapper',
    templateUrl: './training-wrapper.component.html'
})
export class TrainingWrapperComponent {

    trainingDialogComponent = TrainingDialogComponent;
    trainingData: TrainingElement[];

    constructor(public injector: Injector, @Inject(TrainingOverlayRef) public dialogRef: TrainingOverlayRef) {
    }

    handleScreenChange(event: { componentRef: ComponentRef<any>, screen: any }) {
        this.updateTrainingData(event.componentRef, event.screen);
    }

    updateTrainingData(componentRef: ComponentRef<any>, screen: any) {
        if (screen.trainingInstructions && Object.keys(screen.trainingInstructions).length > 0) {
            const training = screen.trainingInstructions;
            this.trainingData = [];

            for (const key in training) {
                if (training.hasOwnProperty(key)) {
                    const element = new TrainingElement();
                    element.key = key;

                    // Inject instructions to the TrainingDialogComponent
                    element.instructions = ReflectiveInjector.resolveAndCreate([
                        { provide: INSTRUCTIONS_DATA, useValue: training[key] }
                    ], this.injector);

                    // Create projectable nodes for the TrainingDialogComponent
                    element.projectableNodes = [[componentRef.location.nativeElement.querySelector('[' + key + ']')]];

                    this.trainingData.push(element);
                }
            }
        }
    }

    close() {
        this.dialogRef.close();
    }

}
