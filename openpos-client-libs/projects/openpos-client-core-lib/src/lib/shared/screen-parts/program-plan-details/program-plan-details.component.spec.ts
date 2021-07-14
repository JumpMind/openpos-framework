import { TestBed, ComponentFixture } from '@angular/core/testing';
import {NO_ERRORS_SCHEMA, SecurityContext} from '@angular/core'

import {ActionService} from '../../../core/actions/action.service';
import {validateExist, validateText} from '../../../utilites/test-utils';
import {MatDialog} from '@angular/material';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {ElectronService} from 'ngx-electron';
import {CLIENTCONTEXT} from '../../../core/client-context/client-context-provider.interface';
import {TimeZoneContext} from '../../../core/client-context/time-zone-context';
import {Subscription} from 'rxjs';
import {KeyPressProvider} from "../../../shared/providers/keypress.provider";
import {ProgramPlanDetailsComponent} from "./program-plan-details.component";
import {Plan} from "../../../screens-with-parts/sale/program-interface";
import {ActionItem} from "../../../core/actions/action-item";
import {By, DomSanitizer} from "@angular/platform-browser";
import {
    SafeHtml, SafeResourceUrl,
    SafeScript,
    SafeStyle, SafeUrl,
    SafeValue
} from "@angular/platform-browser/src/security/dom_sanitization_service";

class MockActionService {};
class MockMatDialog {};
class MockElectronService {};
class ClientContext {};
class MockKeyPressProvider {
    subscribe(): Subscription {
        return new Subscription();
    }
};

class MockDomSanitizer extends DomSanitizer {
    bypassSecurityTrustHtml(copy: string): SafeHtml {
        return "" as SafeHtml;
    }

    sanitize(context: SecurityContext, value: SafeValue | string | null): string | null {
        return undefined;
    }

    bypassSecurityTrustStyle(value: string): SafeStyle {
        return undefined;
    }

    bypassSecurityTrustScript(value: string): SafeScript {
        return undefined;
    }

    bypassSecurityTrustUrl(value: string): SafeUrl {
        return undefined;
    }

    bypassSecurityTrustResourceUrl(value: string): SafeResourceUrl {
        return undefined;
    }
}

describe('ProgramPlanDetailsComponent', () => {
    let component: ProgramPlanDetailsComponent;
    let fixture: ComponentFixture<ProgramPlanDetailsComponent>;
    let domSanitizer: DomSanitizer;

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
                { provide: CLIENTCONTEXT, useClass: TimeZoneContext},
                { provide: DomSanitizer, useClass: MockDomSanitizer}
            ],
            schemas: [
                NO_ERRORS_SCHEMA,
            ]
        }).compileComponents();
        fixture = TestBed.createComponent(ProgramPlanDetailsComponent);
        component = fixture.componentInstance;
        component.programCopy = "<div styles='color: green'>Hello World</div>" as string;
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
        describe('ngOnInit', () => {
           it('initializes the safeProgramCopy by sanitizing the programCopy', () => {
               let sanitizedDom = "banana" as SafeHtml;
               component.safeProgramCopy = undefined;
               component.programCopy = "taco";
               domSanitizer = TestBed.get(DomSanitizer);
               expect(domSanitizer).toBeDefined();
               spyOn(domSanitizer, 'bypassSecurityTrustHtml').and.returnValue(sanitizedDom);

               component.ngOnInit();

               expect(domSanitizer.bypassSecurityTrustHtml).toHaveBeenCalledWith(component.programCopy);
               expect(component.safeProgramCopy).toBe(sanitizedDom);
           });
        });
    });

    describe('template', function () {
        it('should display any safeProgramCopy given', function () {
            component.safeProgramCopy = 'dumb';
            fixture.detectChanges();
            validateExist(fixture, '.program div');
        });

        it('should display not display programCopy when there is no copy', function () {
            component.safeProgramCopy = 'dumb';
            fixture.detectChanges();
            validateExist(fixture, '.program div');
        });

        it('should display any plans given', function () {
            let selector: string = ".plans app-plan-details-display";
            const element = fixture.debugElement.queryAll(By.css(selector));
            expect(element.length).toBe(component.plans.length);
            expect(element[0].nativeElement.plan).toBe(component.plans[0]);
            expect(element[1].nativeElement.plan).toBe(component.plans[1]);
        });
    });
});