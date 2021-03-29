import { TestBed, ComponentFixture } from '@angular/core/testing';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { DisplayCustomerLookupComponent } from "./display-customer-lookup.component";
import { ICustomerDetails } from "../../../screens-with-parts/customer-search-result-dialog/customer-search-result-dialog.interface";
import { validateText } from "../../../utilites/test-utils";
import { Observable } from "rxjs/internal/Observable";
import { PhonePipe } from "../../pipes/phone.pipe";
import { MatDialog } from "@angular/material";
import { HttpClientTestingModule } from "@angular/common/http/testing";
import { ElectronService } from "ngx-electron";
import { CLIENTCONTEXT } from "../../../core/client-context/client-context-provider.interface";
import { TimeZoneContext } from "../../../core/client-context/time-zone-context";
import { By } from "@angular/platform-browser";
import { Membership } from "../membership-display/memebership-display.interface";

class MockMatDialog {};
class MockElectronService {};
class ClientContext {};

describe('DisplayCustomerLookupComponent', () => {
    let component: DisplayCustomerLookupComponent;
    let fixture: ComponentFixture<DisplayCustomerLookupComponent>;

    let testCustomer = {name: 'testName', loyaltyNumber: '12345', phoneNumber: '6142345678',
        email: 'testUser@test.com', address:{
            line1: 'testAddressLine1', line2: 'testAddressLine2',
            city: 'testCity', state: 'testState', postalCode: '12345'
        }, memberships: [
            {
                id: '1', name: 'testGroup', member: false
            } as Membership,{
                id: '2', name: 'testGroup2', member: true
            } as Membership
        ],
        privacyRestricted: 'testRestricted'

    } as ICustomerDetails;

    describe('non mobile', () => {
        beforeEach( () => {
            TestBed.configureTestingModule({
                imports: [ HttpClientTestingModule ],
                declarations: [
                    DisplayCustomerLookupComponent,
                    PhonePipe
                ],
                providers: [
                    { provide: MatDialog, useClass: MockMatDialog },
                    { provide: ElectronService, useClass: MockElectronService },
                    { provide: ClientContext, useValue: {}},
                    { provide: CLIENTCONTEXT, useClass: TimeZoneContext}
                ],
                schemas: [
                    NO_ERRORS_SCHEMA,
                ]
            }).compileComponents();
            fixture = TestBed.createComponent(DisplayCustomerLookupComponent);
            component = fixture.componentInstance;
            component.customer = testCustomer;
            fixture.detectChanges();
        });
        it('renders', () => {
            expect(component).toBeDefined();
        });

        describe('template', () => {
            it('should display customer name', function () {
                validateText(fixture, '.customer-name', testCustomer.name);
            });
            it('should display customer loyalty number', function () {
                validateText(fixture, '.customer-loyaltyNumber', testCustomer.loyaltyNumber);
            });
            it('should display customer email address', function () {
                validateText(fixture, '.customer-email', testCustomer.email);
            });
            it('should display customer phone number', function () {
                validateText(fixture, '.customer-phoneNumber', "(614) 234-5678");
            });
            it('should display customer address', function () {
                const address = testCustomer.address;

                validateText(fixture, '.customer-line1', address.line1);

                validateText(fixture, '.customer-line2', address.line2);

                const expected = `${address.city}, ${address.state} ${address.postalCode}`;
                validateText(fixture, '.customer-cityStateZip', expected);
            });
            it('should display all memberships as badges', function () {
                const membershipDisplayComponents = fixture.debugElement.queryAll(By.css('app-membership-display'));
                expect(membershipDisplayComponents.length).toBe(testCustomer.memberships.length);
            });
            it('should display privacy message when privacy is restricted.', function () {
                const privacyDiv = fixture.debugElement.query(By.css(".privacy"));
                expect(privacyDiv).toBeDefined()
                expect(privacyDiv.nativeElement.textContent).toContain(testCustomer.privacyRestricted);
            });
        });
    });

    describe('mobile', () => {
        beforeEach( () => {
            TestBed.configureTestingModule({
                imports: [ HttpClientTestingModule ],
                declarations: [
                    DisplayCustomerLookupComponent,
                    PhonePipe
                ],
                providers: [
                    { provide: MatDialog, useClass: MockMatDialog },
                    { provide: ElectronService, useClass: MockElectronService },
                    { provide: ClientContext, useValue: {}},
                    { provide: CLIENTCONTEXT, useClass: TimeZoneContext}
                ],
                schemas: [
                    NO_ERRORS_SCHEMA,
                ]
            }).compileComponents();
            fixture = TestBed.createComponent(DisplayCustomerLookupComponent);
            component = fixture.componentInstance;
            component.customer = testCustomer;
            component.isMobile = new Observable<boolean>(subscriber => {
                subscriber.next(true);
            });
            fixture.detectChanges();
        });
        it('renders', () => {
            expect(component).toBeDefined();
        });

        describe('template', () => {
            it('should display customer name', function () {
                validateText(fixture, '.customer-name', testCustomer.name);
            });
            it('should display customer loyalty number', function () {
                validateText(fixture, '.customer-loyaltyNumber', testCustomer.loyaltyNumber);
            });
            it('should display customer email address', function () {
                validateText(fixture, '.customer-email', testCustomer.email);
            });
            it('should display customer phone number', function () {
                validateText(fixture, '.customer-phoneNumber', "(614) 234-5678");
            });
            it('should display all memberships as badges', function () {
                const membershipDisplayComponents = fixture.debugElement.queryAll(By.css('app-membership-display'));
                expect(membershipDisplayComponents.length).toBe(testCustomer.memberships.length);
            });
            it('should display privacy message when privacy is restricted.', function () {
                const privacyDiv = fixture.debugElement.query(By.css(".privacy"));
                expect(privacyDiv).toBeDefined()
                expect(privacyDiv.nativeElement.textContent).toContain(testCustomer.privacyRestricted);
            });
        });
    });
});