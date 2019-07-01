import { Component } from '@angular/core';
import { StartupService } from '../services/startup.service';

@Component({
    templateUrl: './startup.component.html',
    styleUrls: ['startup.component.scss']
})
export class StartupComponent {

    title = 'Initializing ...';
    message: string;
    startupFailed = false;

    constructor( public startup: StartupService) {
        startup.startupTaskMessages$.subscribe( message => this.message = message);
    }
}

