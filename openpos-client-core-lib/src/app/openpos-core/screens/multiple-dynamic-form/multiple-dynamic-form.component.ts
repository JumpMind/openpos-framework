import { Component, OnInit, DoCheck, OnDestroy } from '@angular/core';
import { IScreen, AbstractTemplate } from '../..';
import { IMultipleDynamicFormScreen } from './imultipleDynamicFormScreen';


@Component({
    selector: 'app-multiple-dynamic-form',
    templateUrl: './multiple-dynamic-form.component.html'
})
export class MultipleDynamicFormComponent implements IScreen {
    screen: IMultipleDynamicFormScreen;

    show(screen: any): void {
        this.screen = screen;
    }
}