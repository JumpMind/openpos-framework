
import { MatSnackBar } from '@angular/material';
import {
    Component,
    Input,
    ViewChild,
    ViewContainerRef,
    Inject,
    ComponentFactoryResolver,
    AfterViewInit,
    InjectionToken,
    OnInit
} from '@angular/core';
import { StatusBarData } from './status-bar-data';
import { ComponentType } from '@angular/cdk/overlay/';
import { IStatusBarControl } from '../../../core/interfaces/status-bar-control.interface';
import { SessionService } from '../../../core/services/session.service';
import { TrainingOverlayService } from '../../../core/services/training-overlay.service';


export const STATUS_BAR_STATUS_CONTROL_COMPONENT = new InjectionToken<ComponentType<IStatusBarControl>>('StatusBarStatusControlComponent');
@Component({
    selector: 'app-statusbar',
    templateUrl: './status-bar.component.html'
})
export class StatusBarComponent implements IStatusBarControl, AfterViewInit, OnInit {

    @Input()
    data: StatusBarData;
    /** Swappable component that is placed on right hand side of status bar.  Clients
     *  may override this with their own component using a provider with token STATUS_BAR_STATUS_CONTROL_COMPONENT.
     */
    @ViewChild('statusBarStatusControl', { read: ViewContainerRef }) statusBarControlContainer: ViewContainerRef;

    constructor(public session: SessionService,
        public snackBar: MatSnackBar, private trainingService: TrainingOverlayService,
        @Inject(STATUS_BAR_STATUS_CONTROL_COMPONENT) private statusBarStatusCtrlType: ComponentType<IStatusBarControl>,
        private componentFactoryResolver: ComponentFactoryResolver) {
    }

    ngOnInit(): void {
        if (this.data.backButton) {
            this.data.backButton.keybind = 'Escape';
        }
    }

    ngAfterViewInit(): void {
        const compFactory = this.componentFactoryResolver.resolveComponentFactory(this.statusBarStatusCtrlType);
        const controlRef = this.statusBarControlContainer.createComponent(compFactory);
        controlRef.instance.data = this.data;
        // Run change detection or else get dreaded 'ExpressionChangedAfterItHasBeenCheckedError'
        controlRef.changeDetectorRef.detectChanges();
    }

    public onTraining() {
        this.trainingService.open();
    }

    public onHome() {
        if (this.data.enableHomeAction === true) {
            this.session.onAction('Home');
        }
    }

}
