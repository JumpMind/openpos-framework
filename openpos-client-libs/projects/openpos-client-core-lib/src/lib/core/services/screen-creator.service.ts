import { Injectable, ComponentFactory, ViewContainerRef, ComponentRef, Injector } from '@angular/core';
import { IScreen } from '../../shared/components/dynamic-screen/screen.interface';
import { ActionService } from '../actions/action.service';
import { MatDialog } from '@angular/material';
import { Logger } from './logger.service';
import { MessageProvider } from '../../shared/providers/message.provider';

@Injectable({providedIn: 'root'})
export class ScreenCreatorService {

    createScreenComponent( factory: ComponentFactory<IScreen>, viewContainer: ViewContainerRef): ComponentRef<IScreen> {
        // Create our own injector and add the action service to it.
        const componentInjector = Injector.create(
            {   providers: [ { provide: ActionService, useClass: ActionService, deps: [MatDialog, Logger, MessageProvider] }],
                parent: viewContainer.parentInjector }
            );
        const componentRef = viewContainer.createComponent(factory, viewContainer.length, componentInjector);
        return componentRef;
    }
}
