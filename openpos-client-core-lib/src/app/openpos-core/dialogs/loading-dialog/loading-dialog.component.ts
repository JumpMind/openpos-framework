import { Component } from '@angular/core';
import { ILoadingDialogScreen } from './iloading-dialog-screen';
import { IScreen } from '../../common/iscreen';
import { SessionService } from '../../services/session.service';


@Component({
    selector: 'app-loading-dialog',
    templateUrl: './loading-dialog.component.html'
})
export class LoadingDialogComponent implements IScreen {
    
    screen: ILoadingDialogScreen;

    constructor( public session: SessionService ){}

    show(screen: any): void {
        this.screen = screen;
    }
}
