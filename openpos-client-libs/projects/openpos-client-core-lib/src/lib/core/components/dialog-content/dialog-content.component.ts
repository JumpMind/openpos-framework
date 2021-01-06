import {ActionService} from '../../actions/action.service';
import { ScreenDirective } from './../../../shared/directives/screen.directive';
import { IScreen } from '../../../shared/components/dynamic-screen/screen.interface';
import { Component, OnDestroy, ViewChild, ComponentRef, ComponentFactory, Injector } from '@angular/core';
import { ScreenCreatorService } from '../../services/screen-creator.service';

@Component({
  selector: 'app-dialog-content',
  templateUrl: './dialog-content.component.html',
  styleUrls: ['./dialog-content.component.scss'],
    providers: [ActionService]
})
export class DialogContentComponent implements OnDestroy, IScreen {


    @ViewChild(ScreenDirective, { static: true }) host: ScreenDirective;

    private currentScreenRef: ComponentRef<IScreen>;

    private content: IScreen;

    constructor( private screenCreator: ScreenCreatorService ) {
    }

    public installScreen(screenComponentFactory: ComponentFactory<IScreen>): void {
        const viewContainerRef = this.host.viewContainerRef;
        viewContainerRef.clear();
        if (this.currentScreenRef) {
            this.currentScreenRef.destroy();
        }
        this.currentScreenRef = this.screenCreator.createScreenComponent(screenComponentFactory, viewContainerRef);
        this.content = this.currentScreenRef.instance;
    }

    show(screen: any): void {
        this.content.show(screen);
    }

    ngOnDestroy(): void {
        if (this.currentScreenRef) {
            this.currentScreenRef.destroy();
        }
    }

}
