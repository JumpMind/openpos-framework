import {ComponentFixture, TestBed} from "@angular/core/testing";
import {LoyaltyCustomerFormDialogComponent} from "./loyalty-customer-form-dialog.component";
import {Observable, of} from "rxjs";
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {ActionService} from "../../core/actions/action.service";
import {MediaBreakpoints, OpenposMediaService} from "../../core/media/openpos-media.service";
import {ElectronService} from "ngx-electron";
import {CLIENTCONTEXT} from "../../core/client-context/client-context-provider.interface";
import {TimeZoneContext} from "../../core/client-context/time-zone-context";
import {NO_ERRORS_SCHEMA} from "@angular/core";
import {MatDialog} from "@angular/material";
import {LoyaltyCustomerFormInterface} from "./loyalty-customer-form.interface";
import {it} from "@angular/core/testing/src/testing_internal";

class ClientContext {};
class MockActionService {};
class MockElectronService {};
class MockMatDialog {};

describe('LoyaltyCustomerFormDialog', () => {
    let component: LoyaltyCustomerFormDialogComponent;
    let fixture: ComponentFixture<LoyaltyCustomerFormDialogComponent>;
    let customer;

    class MockOpenposMediaServiceMobileFalse { observe(): Observable<boolean> { return of(false); } };
    class MockOpenposMediaServiceMobileTrue { observe(): Observable<boolean> { return of(true); } };

    beforeEach(() => {
        customer = {
            name: 'Bob Bobert',
            loyaltyNumber: 's321111111',
            address: {
                line1: '123 Mockingbird Lane',
                city: 'Columbus',
                state: 'OH',
                postalCode: '11111',
                country: 'United States of America'
            }
        };
    });

    describe('shared', () => {

        beforeEach( () => {
            TestBed.configureTestingModule({
                imports: [ HttpClientTestingModule],
                declarations: [
                    LoyaltyCustomerFormDialogComponent
                ],
                providers: [
                    { provide: ActionService, useClass: MockActionService },
                    { provide: MatDialog, useClass: MockMatDialog },
                    { provide: OpenposMediaService, useClass: MockOpenposMediaServiceMobileFalse },
                    // What is the ElectronService and why do I need to mock it?
                    { provide: ElectronService, useClass: MockElectronService },
                    { provide: ClientContext, useValue: {} },
                    { provide: CLIENTCONTEXT, useClass: TimeZoneContext }
                ],
                schemas: [
                    NO_ERRORS_SCHEMA,
                ]
            }).compileComponents();
            fixture = TestBed.createComponent(LoyaltyCustomerFormDialogComponent);
            component = fixture.componentInstance;
            component.screen = {
                form: {
                    formElements: [

                    ]
                }
            } as LoyaltyCustomerFormInterface;
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
                        [MediaBreakpoints.TABLET_LANDSCAPE, true],
                        [MediaBreakpoints.DESKTOP_PORTRAIT, false],
                        [MediaBreakpoints.DESKTOP_LANDSCAPE, false]
                    ]));
                });
            });

            describe("anyAddressFieldsPresent()", () => {
                it("returns true when any address fields are present", () => {

                });

                it("returns false when none of the address fields are present", () => {

                });
            });

            describe("buildScreen()", () => {
                it("can build a structured form", () => {

                });

                it("does not have to build a structured form", () => {

                });
            });

            describe("buildStructuredForm()", () => {
                it("properly sets the address icon location class to the line1 line", () => {

                });

                it("properly sets the address icon location class to the line2 line", () => {

                });

                it("properly sets the address icon location class to the city/state/zip line", () => {

                });

                it("properly sets the address icon location class to the country line", () => {

                });

                it("populates the phone list fields when phonesList form elements are present", () => {

                });

                it("populates the email list fields when emailsList form elements are present", () => {

                });
            });
        });

        describe("template", () => {
            describe("basic-info", () => {
                it("renders when the firstName or lastName field is present", () => {

                });

                it("firstName field is rendered when present", () => {

                });

                it("lastName field is rendered when present", () => {

                });
            });

            describe("contact", () => {
                it("renders when any of the email or phone fields are present", () => {

                });

                it("email field is rendered when present", () => {

                });

                it("emailsList fields are rendered when emailsList is present", () => {

                });

                it("phone field is rendered when present", () => {

                });

                it("phonesList fields are rendered when phonesList is present", () => {

                });
            });

            describe("location", () => {
                it("renders when any of the address fields are present", () => {

                });

                it("line1 field is rendered when present", () => {

                });

                it("line2 field is rendered when present", () => {

                });

                it("city field is rendered when present", () => {

                });

                it("state field is rendered when present", () => {

                });

                it("zip/postalCode field is rendered when present", () => {

                });

                it("country field is rendered when present", () => {

                });
            });

            describe("loyalty and memberships", () => {
                it("loyaltyNumber field is rendered when present", () => {

                });

                it("memberships only display when memberships are enabled and is a structured form", () => {

                });

                it("a special message is displayed when there are no memberships on the customer", () => {

                });
            });

            describe("extra non-structured form fields", () => {
                it("unhandled/unexpected form fields are rendered", () => {

                });

                it("handled/structured form fields are not rendered twice", () => {

                });
            });

        });
    });
});