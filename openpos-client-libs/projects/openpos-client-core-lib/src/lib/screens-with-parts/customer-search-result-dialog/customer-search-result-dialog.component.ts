import {Component, Injector} from '@angular/core';
import {DialogComponent} from '../../shared/decorators/dialog-component.decorator';
import {ICustomerDetails} from "./customer-search-result-dialog.interface";
import {GenericSelectionListScreen} from "../selection-list/generic-selection-list-screen";
import {SessionService} from "../../core/services/session.service";

@DialogComponent({
    name: 'CustomerSearchResultDialog'
})
@Component({
    selector: 'app-customer-search-result-dialog',
    templateUrl: './customer-search-result-dialog.component.html',
    styleUrls: ['./customer-search-result-dialog.component.scss']
})
export class CustomerSearchResultDialogComponent extends GenericSelectionListScreen<ICustomerDetails>{

    constructor(injector: Injector, session: SessionService) { super(injector, session); }
}