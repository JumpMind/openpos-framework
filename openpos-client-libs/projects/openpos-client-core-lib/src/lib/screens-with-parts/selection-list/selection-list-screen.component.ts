import {Component, Injector} from '@angular/core';
import {ScreenComponent} from '../../shared/decorators/screen-component.decorator';
import {ISelectionListItem} from './selection-list-item.interface';
import {GenericSelectionListScreen} from './generic-selection-list-screen';
import {SessionService} from '../../core/services/session.service';

@ScreenComponent({
    name: 'SelectionList'
})
@Component({
    selector: 'app-selection-list-screen',
    templateUrl: './selection-list-screen.component.html',
    styleUrls: ['./selection-list-screen.component.scss']
})
export class SelectionListScreenComponent extends GenericSelectionListScreen<ISelectionListItem>{
    constructor(injector: Injector, session: SessionService) { super(injector, session); }
}