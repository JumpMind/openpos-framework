import {validateDoesNotExist, validateExist, validateIcon, validateText} from "../../../utilites/test-utils";
import {ComponentFixture, TestBed} from "@angular/core/testing";
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {MatDialog} from "@angular/material";
import {ActionService} from "../../../core/actions/action.service";
import {ElectronService} from "ngx-electron";
import {CLIENTCONTEXT} from '../../../core/client-context/client-context-provider.interface';
import {TimeZoneContext} from "../../../core/client-context/time-zone-context";
import {NO_ERRORS_SCHEMA} from "@angular/core";
import {PlanDetailsDisplayComponent} from "./plan-details-display.component";
import {Plan} from "../../../screens-with-parts/sale/program-interface";
import {KeyPressProvider} from "../../providers/keypress.provider";
import {Subscription} from "rxjs";
import {ActionItem} from "../../../core/actions/action-item";

class MockKeyPressProvider {
    subscribe(): Subscription {
        return new Subscription();
    }
};
class MockActionService {};
class MockMatDialog {};
class MockElectronService {};
class ClientContext {};

describe('PlanDetailsDisplayComponent', () => {
    let component: PlanDetailsDisplayComponent;
    let fixture: ComponentFixture<PlanDetailsDisplayComponent>;

    beforeEach( () => {
        TestBed.configureTestingModule({
            imports: [ HttpClientTestingModule ],
            declarations: [
                PlanDetailsDisplayComponent
            ],
            providers: [
                { provide: MatDialog, useClass: MockMatDialog },
                { provide: ActionService, useClass: MockActionService },
                { provide: ElectronService, useClass: MockElectronService },
                { provide: KeyPressProvider, useClass: MockKeyPressProvider },
                { provide: ClientContext, useValue: {}},
                { provide: CLIENTCONTEXT, useClass: TimeZoneContext}
            ],
            schemas: [
                NO_ERRORS_SCHEMA,
            ]
        }).compileComponents();
        fixture = TestBed.createComponent(PlanDetailsDisplayComponent);
        component = fixture.componentInstance;
        component.plan = {
            title: 'Silver Tier',
            iconImageUrl: 'code',
            copy: '<b>SILVER TIER</b><hr>Get the good stuff without the extra fancy fluff.',
            signupActionItem: { title: 'Become A Silver Member', icon: 'add' } as ActionItem
        } as Plan;
        fixture.detectChanges();
        component.screenDataUpdated();
    });

    it('renders', () => {
        expect(component).toBeDefined();
    });

    describe('template', () => {
        beforeEach(() => {
            fixture.detectChanges();
        });

        it('shows the plan title icon', () => {
            validateExist(fixture, 'app-icon');
        });

        it('shows the plan title', () => {
            validateText(fixture, '.title', component.plan.title);
        });

        it('shows the plan copy/markup', () => {
            validateText(fixture, '.plan-copy div', 'SILVER TIER');
            validateText(fixture, '.plan-copy div', 'Get the good stuff without the extra fancy fluff.');
        });

        it('shows the plan signup action button', () => {
            validateText(fixture, '.actions a', component.plan.signupActionItem.title);
        });

        it('shows the plan signup action button icon', () => {
            validateExist(fixture, '.actions a app-icon');
        });
    });
});