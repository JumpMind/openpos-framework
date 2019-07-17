import { Injectable, ComponentFactory, ViewContainerRef, ComponentRef, Injector } from '@angular/core';
import { IScreen } from '../../shared/components/dynamic-screen/screen.interface';
import { ActionService } from './action.service';

@Injectable({providedIn: 'root'})
export class ScreenCreatorService {

    createScreenComponent( factory: ComponentFactory<IScreen>, viewContainer: ViewContainerRef): ComponentRef<IScreen> {
        // Create our own injector and add the action service to it.
        const componentInjector = Injector.create(
            {   providers: [ { provide: ActionService, useClass: ActionService }],
                parent: viewContainer.parentInjector }
            );
        const componentRef = viewContainer.createComponent(factory, viewContainer.length, componentInjector);
        return componentRef;
    }
}
