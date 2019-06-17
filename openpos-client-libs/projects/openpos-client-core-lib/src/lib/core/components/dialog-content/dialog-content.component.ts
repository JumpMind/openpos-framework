import { ScreenDirective } from './../../../shared/directives/screen.directive';
import { IScreen } from './../dynamic-screen/screen.interface';
import { Component, OnDestroy, ViewChild, ComponentRef, ComponentFactory } from '@angular/core';
import { MessageProvider } from '../../../shared/providers/message.provider';

@Component({
  selector: 'app-dialog-content',
  templateUrl: './dialog-content.component.html',
  styleUrls: ['./dialog-content.component.scss'],
  providers: [MessageProvider]
})
export class DialogContentComponent implements OnDestroy, IScreen {

    @ViewChild(ScreenDirective) host: ScreenDirective;

    private currentScreenRef: ComponentRef<IScreen>;

    private content: IScreen;

    constructor( messageProvider: MessageProvider ) {
        messageProvider.setMessageType('Dialog');
    }

    public installScreen(screenComponentFactory: ComponentFactory<IScreen>): void {
        const viewContainerRef = this.host.viewContainerRef;
        viewContainerRef.clear();
        if (this.currentScreenRef) {
            this.currentScreenRef.destroy();
        }
        this.currentScreenRef = viewContainerRef.createComponent(screenComponentFactory);
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
