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
import {IForm} from "../../core/interfaces/form.interface";
import {IFormElement} from "../../core/interfaces/form-field.interface";
import {By} from "@angular/platform-browser";
import {Membership} from "../../shared/screen-parts/membership-display/memebership-display.interface";

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
                beforeEach(() => {
                    component.line1Field = null;
                    component.line2Field = null;
                    component.cityField = null;
                    component.stateField = null;
                    component.postalCodeField = null;
                    component.countryField = null;
                })
                it("returns true when any address fields are present", () => {
                    component.line1Field = {} as IFormElement;
                    expect(component.anyAddressFieldsPresent()).toBeTruthy();
                    component.line1Field = null;

                    component.line2Field = {} as IFormElement;
                    expect(component.anyAddressFieldsPresent()).toBeTruthy();
                    component.line2Field = null;

                    component.cityField = {} as IFormElement;
                    expect(component.anyAddressFieldsPresent()).toBeTruthy();
                    component.cityField = null;

                    component.stateField = {} as IFormElement;
                    expect(component.anyAddressFieldsPresent()).toBeTruthy();
                    component.stateField = null;

                    component.postalCodeField = {} as IFormElement;
                    expect(component.anyAddressFieldsPresent()).toBeTruthy();
                    component.postalCodeField = null;

                    component.countryField = {} as IFormElement;
                    expect(component.anyAddressFieldsPresent()).toBeTruthy();
                    component.countryField = null;
                });

                it("returns false when none of the address fields are present", () => {
                    expect(component.anyAddressFieldsPresent()).toBeFalsy();
                });
            });

            describe("buildScreen()", () => {
                it("can build a structured form", () => {
                    spyOn<any>(component, 'buildStructuredForm');
                    component.screen.isStructuredForm = true;
                    component.buildScreen();
                    expect(component['buildStructuredForm']).toHaveBeenCalledTimes(1);
                    expect(component.screen.formGroup).toBeTruthy();

                    component.screen.isStructuredForm = true;
                });

                it("does not have to build a structured form", () => {
                    spyOn<any>(component, 'buildStructuredForm');
                    component.screen.isStructuredForm = false;
                    component.buildScreen();
                    expect(component['buildStructuredForm']).toHaveBeenCalledTimes(0);
                    expect(component.screen.formGroup).toBeTruthy();
                });
            });

            describe("getFormElementById", () => {
                beforeEach(()=>{
                    let form = {} as IForm;
                    let formElements = {} as IFormElement;
                    let formElementId = "formElementId";
                    let firstName = "testFirstName";
                    let lastName = "testLastName";
                    formElements.id = formElementId;

                    form.formElements = [formElements, formElements, formElements];
                    form.formElements[1].id = firstName;
                    form.formElements[2].id = lastName;
                    component.screen.form = form;
                });

                it("properly fetches a formElement by the id that dose exist", () => {
                    let form = {} as IForm;
                    let firstName = "testFirstName";
                    let lastName = "testLastName";
                    let firstNameElement = {id: firstName} as IFormElement;
                    let lastNameElement = {id: lastName} as IFormElement;

                    form.formElements = [firstNameElement, lastNameElement];
                    component.screen.form = form;

                    expect(component.getFormElementById(firstName)).toBe(form.formElements[0])
                    expect(component.getFormElementById(lastName)).toBe(form.formElements[1])
                });

                it("returns null if element id in the not in formElements", () => {
                    let form = {} as IForm;
                    form.formElements = [];

                    component.screen.form = form;
                    expect(component.getFormElementById("Something")).toBe(undefined)
                });
            });

            describe("buildStructuredForm()", () => {
                it("properly sets the address icon location class to the line1 line", () => {
                    let formElement = {id: "line1"} as IFormElement;
                    let form = {} as IForm;
                    form.formElements = [formElement];

                    component.screen.form = form;
                    component.screen.isStructuredForm = true;

                    component.buildScreen();
                    expect(component.addressIconLocationClass).toBe('icon1');
                });

                it("properly sets the address icon location class to the line2 line", () => {
                    let formElement = {id: "line2"} as IFormElement;
                    let form = {} as IForm;
                    form.formElements = [formElement];

                    component.screen.form = form;
                    component.screen.isStructuredForm = true;

                    component.buildScreen();
                    expect(component.addressIconLocationClass).toBe('icon2');
                });

                it("properly sets the address icon location class to the city/state/zip line", () => {
                    let city = {id: "city"} as IFormElement;
                    let state = {id: "state"} as IFormElement;
                    let postal = {id: "postalCode"} as IFormElement;
                    let form = {} as IForm;
                    form.formElements = [city, state, postal];

                    component.screen.form = form;
                    component.screen.isStructuredForm = true;

                    component.buildScreen();
                    expect(component.addressIconLocationClass).toBe('icon3');
                });

                it("properly sets the address icon location class to the country line", () => {
                    let formElement = {id: "country"} as IFormElement;
                    let form = {} as IForm;
                    form.formElements = [formElement];

                    component.screen.form = form;
                    component.screen.isStructuredForm = true;

                    component.buildScreen();
                    expect(component.addressIconLocationClass).toBe('icon4');
                });

                it("populates the phone list fields when phonesList form elements are present", () => {
                    let formElement0 = {id: "phonesList0"} as IFormElement;
                    let formElement1 = {id: "phonesList1"} as IFormElement;
                    let formElement2 = {id: "phonesList2"} as IFormElement;

                    let formElementLabel0 = {id: "phonesListLabel0"} as IFormElement;
                    let formElementLabel1 = {id: "phonesListLabel1"} as IFormElement;
                    let formElementLabel2 = {id: "phonesListLabel2"} as IFormElement;
                    let form = {} as IForm;
                    form.formElements = [formElement0, formElement1, formElement2, formElementLabel0, formElementLabel1, formElementLabel2];

                    component.screen.form = form;
                    component.screen.isStructuredForm = true;

                    component.buildScreen();
                    expect(component.phoneFields).toBeDefined();
                    expect(component.phoneLabelFields).toBeDefined();

                    expect(component.phoneFields.length).toBe(3);
                    expect(component.phoneFields).toEqual([formElement0, formElement1, formElement2]);
                    expect(component.phoneLabelFields.length).toBe(3);
                    expect(component.phoneLabelFields).toEqual([formElementLabel0, formElementLabel1, formElementLabel2]);
                });

                it("populates the email list fields when emailsList form elements are present", () => {
                    let formElement0 = {id: "emailsList0"} as IFormElement;
                    let formElement1 = {id: "emailsList1"} as IFormElement;
                    let formElement2 = {id: "emailsList2"} as IFormElement;

                    let formElementLabel0 = {id: "emailsListLabel0"} as IFormElement;
                    let formElementLabel1 = {id: "emailsListLabel1"} as IFormElement;
                    let formElementLabel2 = {id: "emailsListLabel2"} as IFormElement;
                    let form = {} as IForm;
                    form.formElements = [formElement0, formElement1, formElement2, formElementLabel0, formElementLabel1, formElementLabel2];

                    component.screen.form = form;
                    component.screen.isStructuredForm = true;

                    component.buildScreen();
                    expect(component.emailFields).toBeDefined();
                    expect(component.emailLabelFields).toBeDefined();

                    expect(component.emailFields.length).toBe(3);
                    expect(component.emailFields).toEqual([formElement0, formElement1, formElement2]);
                    expect(component.emailLabelFields.length).toBe(3);
                    expect(component.emailLabelFields).toEqual([formElementLabel0, formElementLabel1, formElementLabel2]);
                });
            });
        });

        describe("template", () => {
            describe("basic-info", () => {
                it("renders when the firstName or lastName field is present", () => {
                    component.firstNameField = {id: 'firstName'} as IFormElement;
                    component.lastNameField = {id: 'lastName'} as IFormElement;

                    fixture.detectChanges();
                    let test = fixture.debugElement.queryAll(By.css('.names app-dynamic-form-field'));
                    expect(test.length).toBe(2)
                    expect(test[0].properties.formField.id).toBe(component.firstNameField.id);
                    expect(test[1].properties.formField.id).toBe(component.lastNameField.id);
                });

                it("firstName field is rendered when present", () => {
                    component.firstNameField = {id: 'firstName'} as IFormElement;

                    fixture.detectChanges();
                    let test = fixture.debugElement.queryAll(By.css('.names app-dynamic-form-field'));
                    expect(test.length).toBe(1)
                    expect(test[0].properties.formField.id).toBe(component.firstNameField.id);
                });

                it("lastName field is rendered when present", () => {
                    component.lastNameField = {id: 'lastName'} as IFormElement;

                    fixture.detectChanges();
                    let test = fixture.debugElement.queryAll(By.css('.names app-dynamic-form-field'));
                    expect(test.length).toBe(1)
                    expect(test[0].properties.formField.id).toBe(component.lastNameField.id);
                });
            });

            describe("contact", () => {
                it("renders when any of the email or phone fields are present", () => {
                    component.phoneFields = [{id: 'phoneFields1'} as IFormElement, {id: 'phoneFields2'} as IFormElement];
                    component.emailFields = [{id: 'emailFields1'} as IFormElement, {id: 'emailFields2'} as IFormElement];

                    fixture.detectChanges();
                    let test = fixture.debugElement.queryAll(By.css('.contact app-mutable-list-item-with-label'));
                    expect(test.length).toBe(4);
                });

                it("email field is rendered when present", () => {
                    component.emailField = {id: 'emailField'} as IFormElement;

                    fixture.detectChanges();
                    let test = fixture.debugElement.queryAll(By.css('.contact .email app-dynamic-form-field'));
                    expect(test.length).toBe(1);
                    expect(test[0].properties.formField.id).toBe(component.emailField.id);
                });

                it("emailsList fields are rendered when emailsList is present", () => {
                    component.emailFields = [{id: 'emailFields1'} as IFormElement, {id: 'emailFields2'} as IFormElement];

                    fixture.detectChanges();
                    let test = fixture.debugElement.queryAll(By.css('.email-list app-mutable-list-item-with-label'));
                    expect(test.length).toBe(2);
                    expect(test[0].properties.inputField.id).toBe(component.emailFields[0].id);
                    expect(test[1].properties.inputField.id).toBe(component.emailFields[1].id);
                });

                it("phone field is rendered when present", () => {
                    component.phoneField = {id: 'phoneField'} as IFormElement;

                    fixture.detectChanges();
                    let test = fixture.debugElement.queryAll(By.css('.contact .phone app-dynamic-form-field'));
                    expect(test.length).toBe(1);
                    expect(test[0].properties.formField.id).toBe(component.phoneField.id);
                });

                it("phonesList fields are rendered when phonesList is present", () => {
                    component.phoneFields = [{id: 'phoneFields1'} as IFormElement, {id: 'phoneFields2'} as IFormElement];

                    fixture.detectChanges();
                    let test = fixture.debugElement.queryAll(By.css('.phone-list app-mutable-list-item-with-label'));
                    expect(test.length).toBe(2);
                    expect(test[0].properties.inputField.id).toBe(component.phoneFields[0].id);
                    expect(test[1].properties.inputField.id).toBe(component.phoneFields[1].id);
                });
            });

            describe("location", () => {
                it("line1 field is rendered when present", () => {
                    component.line1Field = {id: "line1Field"} as IFormElement

                    fixture.detectChanges();
                    let test = fixture.debugElement.queryAll(By.css('.location app-dynamic-form-field.line1'));
                    expect(test.length).toBe(1);
                    expect(test[0].properties.formField.id).toEqual(component.line1Field.id);
                });

                it("line2 field is rendered when present", () => {
                    component.line2Field = {id: "line2Field"} as IFormElement

                    fixture.detectChanges();
                    let test = fixture.debugElement.queryAll(By.css('.location app-dynamic-form-field.line2'));
                    expect(test.length).toBe(1);
                    expect(test[0].properties.formField.id).toEqual(component.line2Field.id);
                });

                it("city field is rendered when present", () => {
                    component.cityField = {id: "city"} as IFormElement

                    fixture.detectChanges();
                    let test = fixture.debugElement.queryAll(By.css('.location app-dynamic-form-field.city'));
                    expect(test.length).toBe(1);
                    expect(test[0].properties.formField.id).toEqual(component.cityField.id);
                });

                it("state field is rendered when present", () => {
                    component.stateField = {id: "state"} as IFormElement

                    fixture.detectChanges();
                    let test = fixture.debugElement.queryAll(By.css('.location app-dynamic-form-field.state'));
                    expect(test.length).toBe(1);
                    expect(test[0].properties.formField.id).toEqual(component.stateField.id);
                });

                it("zip/postalCode field is rendered when present", () => {
                    component.postalCodeField = {id: "postalCodeField"} as IFormElement

                    fixture.detectChanges();
                    let test = fixture.debugElement.queryAll(By.css('.location app-dynamic-form-field.postalCode'));
                    expect(test.length).toBe(1);
                    expect(test[0].properties.formField.id).toEqual(component.postalCodeField.id);
                });

                it("country field is rendered when present", () => {
                    component.countryField = {id: "countryField"} as IFormElement

                    fixture.detectChanges();
                    let test = fixture.debugElement.queryAll(By.css('.location app-dynamic-form-field.country'));
                    expect(test.length).toBe(1);
                    expect(test[0].properties.formField.id).toEqual(component.countryField.id);
                });
            });

            describe("loyalty and memberships", () => {
                it("loyaltyNumber field is rendered when present", () => {
                    component.loyaltyNumberField = {id: "loyaltyNumberField"} as IFormElement;

                    fixture.detectChanges();
                    let test = fixture.debugElement.queryAll(By.css('.loyalty-and-membership .loyalty-number app-dynamic-form-field'));
                    expect(test.length).toBe(1)
                    expect(test[0].properties.formField.id).toBe(component.loyaltyNumberField.id);
                });

                it("memberships only display when memberships are enabled and is a structured form", () => {
                    component.screen.membershipEnabled = true;
                    component.screen.isStructuredForm = true;
                    component.screen.memberships = [{id:"1"} as Membership, {id:"2"} as Membership, {id:"3"} as Membership, {id:"4"} as Membership];

                    fixture.detectChanges();
                    let test = fixture.debugElement.queryAll(By.css('.loyalty-and-membership .memberships .memberships-list app-membership-display'));
                    expect(test.length).toBe(4)
                    expect(test[0].properties.membership.id).toEqual(component.screen.memberships[0].id);
                    expect(test[1].properties.membership.id).toEqual(component.screen.memberships[1].id);
                    expect(test[2].properties.membership.id).toEqual(component.screen.memberships[2].id);
                    expect(test[3].properties.membership.id).toEqual(component.screen.memberships[3].id);
                });

                it("a special message is displayed when there are no memberships on the customer", () => {
                    component.screen.membershipEnabled = true;
                    component.screen.isStructuredForm = true
                    component.screen.memberships = []
                    component.screen.noMembershipsLabel = "Special message!"

                    fixture.detectChanges();
                    let test = fixture.debugElement.queryAll(By.css('.loyalty-and-membership .memberships .no-memberships'));
                    expect(test.length).toBe(1);
                    expect(test[0].nativeElement.textContent).toBe(component.screen.noMembershipsLabel)
                });
            });

            describe("extra non-structured form fields", () => {
                it("unhandled/unexpected form fields are rendered", () => {
                    let element = {id: "Something else"} as IFormElement;
                    component.screen.form.formElements = [element];

                    fixture.detectChanges();
                    let test = fixture.debugElement.queryAll(By.css('.extraFormFields app-dynamic-form-field'));
                    expect(test.length).toBe(1);
                    expect(test[0].properties.formField.id).toEqual(component.screen.form.formElements[0].id);
                });
            });
        });
    });
});