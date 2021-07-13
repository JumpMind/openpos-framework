import { TestBed, ComponentFixture } from '@angular/core/testing';
import { NO_ERRORS_SCHEMA } from '@angular/core'
import {MatDialog} from '@angular/material';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {Observable, of, Subscription} from 'rxjs';
import {MediaBreakpoints, OpenposMediaService} from '../../../core/media/openpos-media.service';
import {ElectronService} from 'ngx-electron';
import {CLIENTCONTEXT} from '../../../core/client-context/client-context-provider.interface';
import {TimeZoneContext} from '../../../core/client-context/time-zone-context';
import {ActionService} from '../../../core/actions/action.service';
import {KeyPressProvider} from '../../providers/keypress.provider';
import {validateDoesNotExist, validateExist, validateIcon, validateText} from '../../../utilites/test-utils';
import {IActionItem} from '../../../core/actions/action-item.interface';
import {By} from '@angular/platform-browser';
import {EnrollmentLineItemComponent} from "./enrollment-line-item.component";
import {ActionItem} from "../../../core/actions/action-item";
import {EnrollmentItem, EnrollmentItemProperty} from "../../../screens-with-parts/sale/program-interface";

class MockActionService {};
class MockMatDialog {};
class MockKeyPressProvider {
    subscribe(): Subscription {
        return new Subscription();
    }
};
class MockElectronService {};
class ClientContext {};

describe('EnrollmentLineItemComponent', () => {
    let component: EnrollmentLineItemComponent;
    let fixture: ComponentFixture<EnrollmentLineItemComponent>;
    class MockOpenposMediaServiceMobileFalse {
        observe(): Observable<boolean> {
            return of(false);
        }
    };

    class MockOpenposMediaServiceMobileTrue {
        observe(): Observable<boolean> {
            return of(true);
        }
    };

    describe('shared', () => {
        beforeEach( () => {
            TestBed.configureTestingModule({
                imports: [ HttpClientTestingModule],
                declarations: [
                    EnrollmentLineItemComponent
                ],
                providers: [
                    { provide: ActionService, useClass: MockActionService },
                    { provide: MatDialog, useClass: MockMatDialog },
                    { provide: OpenposMediaService, useClass: MockOpenposMediaServiceMobileFalse },
                    { provide: KeyPressProvider, useClass: MockKeyPressProvider },
                    { provide: ElectronService, useClass: MockElectronService },
                    { provide: ClientContext, useValue: {}},
                    { provide: CLIENTCONTEXT, useClass: TimeZoneContext}
                ],
                schemas: [
                    NO_ERRORS_SCHEMA,
                ]
            }).compileComponents();
            fixture = TestBed.createComponent(EnrollmentLineItemComponent);
            component = fixture.componentInstance;
            component.enrollment = {
                title: {
                    icon: 'titleIcon',
                    value: 'value'
                } as EnrollmentItemProperty,
                icon: "icon",
                details: [
                    {value: "detailOne"} as EnrollmentItemProperty ,
                    {value: "detailTwo"} as EnrollmentItemProperty
                ],
                actionItemList: [
                    {title: "actionOne"} as ActionItem,
                    {title: "actionTwo"} as ActionItem
                ]
            } as EnrollmentItem;
            fixture.detectChanges();
        });

        it('renders', () => {
            expect(component).toBeDefined();
        });

        describe('component', () => {
            describe('initIsMobile', () => {
               it('sets the values for isMobile', () => {
                   const media: OpenposMediaService = TestBed.get(OpenposMediaService);
                   spyOn(media, 'observe');

                   component.initIsMobile();

                   expect(media.observe).toHaveBeenCalledWith(new Map([
                       [MediaBreakpoints.MOBILE_PORTRAIT, true],
                       [MediaBreakpoints.MOBILE_LANDSCAPE, true],
                       [MediaBreakpoints.TABLET_PORTRAIT, true],
                       [MediaBreakpoints.TABLET_LANDSCAPE, false],
                       [MediaBreakpoints.DESKTOP_PORTRAIT, false],
                       [MediaBreakpoints.DESKTOP_LANDSCAPE, false]
                   ]));
               });
            });
        });

        describe('template', () => {
            it('should display the title section', function () {
                validateExist(fixture, ".title app-icon");
                validateText(fixture, ".title", component.enrollment.title.value);
            });
            it('should display all details', function () {
                validateText(fixture, ".details", component.enrollment.details[0].value)
                validateText(fixture, ".details", component.enrollment.details[1].value);
            });
            function validateTextQueryAllActionItems( index ) {
                const element = fixture.debugElement.queryAll(By.css(".actions a"));
                expect(element[index].nativeElement).toBeDefined();
                expect(element[index].nativeElement.textContent).toContain(component.enrollment.actionItemList[index].title);
            }
            it('should display all actions', function () {
                validateTextQueryAllActionItems(0);
                validateTextQueryAllActionItems(1)
            });
        });
    });
});