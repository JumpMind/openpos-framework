import { TestBed, ComponentFixture } from '@angular/core/testing';
import { NO_ERRORS_SCHEMA } from '@angular/core'

import {ActionService} from '../../../core/actions/action.service';
import {validateExist, validateText} from '../../../utilites/test-utils';
import {IActionItem} from '../../../core/actions/action-item.interface';
import {MatDialog} from '@angular/material';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {ElectronService} from 'ngx-electron';
import {CLIENTCONTEXT} from '../../../core/client-context/client-context-provider.interface';
import {TimeZoneContext} from '../../../core/client-context/time-zone-context';
import {Observable, of, Subscription} from 'rxjs';
import {MediaBreakpoints, OpenposMediaService} from '../../../core/media/openpos-media.service';
import {KeyPressProvider} from "../../../shared/providers/keypress.provider";
import {ProgramPlanDetailsComponent} from "./program-plan-details.component";
import {promisify} from "util";
import {Plan} from "../../../screens-with-parts/sale/program-interface";
import {ActionItem} from "../../../core/actions/action-item";
import {By} from "@angular/platform-browser";

class MockActionService {};
class MockMatDialog {};
class MockElectronService {};
class ClientContext {};
class MockKeyPressProvider {
    subscribe(): Subscription {
        return new Subscription();
    }
};

describe('ProgramPlanDetailsComponent', () => {
    let component: ProgramPlanDetailsComponent;
    let fixture: ComponentFixture<ProgramPlanDetailsComponent>;

    beforeEach( () => {
        TestBed.configureTestingModule({
            imports: [ HttpClientTestingModule ],
            declarations: [
                ProgramPlanDetailsComponent
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
        fixture = TestBed.createComponent(ProgramPlanDetailsComponent);
        component = fixture.componentInstance;
        component.programCopy = "programCopy" as String;
        component.plans = [{
                iconImageUrl: "iconImageUrl",
                iconText: "iconText",
                title: "title",
                copy: "copy",
                signupActionItem: {} as ActionItem
            } as Plan,
            {
                iconImageUrl: "iconImageUrl2",
                iconText: "iconText2",
                title: "title2",
                copy: "copy2",
                signupActionItem: {} as ActionItem
            } as Plan]

        fixture.detectChanges();
    });

    it('renders', () => {
        expect(component).toBeDefined();
    });

    describe('component', () => {
    });

    describe('template', function () {
        it('should display any programCopy given', function () {
            validateText(fixture, ".program div", component.programCopy as string);
        });

        it('should display any plans given', function () {
            let selector: string = ".plans app-plan-details-display";
            const element = fixture.debugElement.queryAll(By.css(selector));
            expect(element.length).toBe(component.plans.length);
            expect(element[0].nativeElement.plan).toBe(component.plans[0]);
            expect(element[1].nativeElement.plan).toBe(component.plans[1]);
        });
    });
    //
    // describe('template', () => {
    //     it('shows the name of the membership', () => {
    //         validateText(fixture, 'mat-chip', component.membership.name);
    //     });
    //
    //     it('calls clickEvent.emit when the chip is clicked', () => {
    //         spyOn(component.clickEvent, 'emit');
    //
    //         const chip = fixture.debugElement.query(By.css('mat-chip'));
    //         chip.nativeElement.click();
    //
    //         expect(component.clickEvent.emit).toHaveBeenCalledWith(component.membership);
    //     })
    //
    //     describe('when the user is a member', () => {
    //         beforeEach(() => {
    //             component.membership.member = true;
    //             fixture.detectChanges();
    //         });
    //
    //         it('has the "in" class', () => {
    //             const chip = fixture.debugElement.query(By.css('mat-chip'));
    //             expect(chip.nativeElement.classList).toContain('in');
    //         });
    //
    //         it('displays the membership icon', () => {
    //             validateIcon(fixture, 'mat-chip app-icon', 'check');
    //         });
    //
    //         it('does not have the non membership icon', () => {
    //             validateDoesNotExist(fixture, 'mat-chip app-icon .not-in');
    //         });
    //     });
    //
    //     describe('when the user is not a member', () => {
    //         beforeEach(() => {
    //             component.membership.member = false
    //             fixture.detectChanges();
    //         });
    //
    //         it('has the "not-in" class', () => {
    //             const chip = fixture.debugElement.query(By.css('mat-chip'));
    //             expect(chip.nativeElement.classList).toContain('not-in');
    //         });
    //
    //         it('does not have the membership icon', () => {
    //             validateDoesNotExist(fixture, 'mat-chip app-icon .in');
    //         });
    //
    //         it('has the non membership icon', () => {
    //             validateIcon(fixture, 'mat-chip app-icon', 'close');
    //         });
    //     });
    // });
});