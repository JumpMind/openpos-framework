import { Injectable, ComponentFactory, ViewContainerRef, ComponentRef, Injector } from '@angular/core';
import { IScreen } from '../../shared/components/dynamic-screen/screen.interface';
import { ActionService } from '../actions/action.service';
import { MatDialog } from '@angular/material';
import { MessageProvider } from '../../shared/providers/message.provider';

@Injectable({providedIn: 'root'})
export class ScreenCreatorService {

    createScreenComponent( factory: ComponentFactory<IScreen>, viewContainer: ViewContainerRef): ComponentRef<IScreen> {

        const componentRef = viewContainer.createComponent(factory);

        return componentRef;
    }
}
