import { Injectable, ComponentFactory, ViewContainerRef, ComponentRef } from '@angular/core';
import { IScreen } from '../../shared/components/dynamic-screen/screen.interface';

@Injectable({providedIn: 'root'})
export class ScreenCreatorService {

    createScreenComponent( factory: ComponentFactory<IScreen>, viewContainer: ViewContainerRef): ComponentRef<IScreen> {
        const componentRef = viewContainer.createComponent(factory);
        return componentRef;
    }
}
