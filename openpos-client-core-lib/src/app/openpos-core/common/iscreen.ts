import { DynamicScreenComponent } from './../screens/dynamic-screen/dynamic-screen.component';
import { SessionService } from '../services/session.service';
import { AbstractTemplate } from '.';

export interface IScreen {
    show(screen: any, app?: DynamicScreenComponent, template?: AbstractTemplate): void;
}
