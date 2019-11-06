import { Component, Input } from '@angular/core';

@Component({
    selector: 'app-openpos-help-container-root',
    templateUrl: './openpos-app-help-container.component.html'
})
export class OpenposAppHelpContainerComponent {

    @Input()
    helpPosition = 'end';

    constructor() {
    }
}
