import { TestBed, ComponentFixture } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { NO_ERRORS_SCHEMA } from '@angular/core'
import { Subscription, Observable, of } from 'rxjs';
import {SaleTotalPanelComponent} from "./sale-total-panel.component";
import {SaleTotalPanelInterface} from "./sale-total-panel.interface";
import {MatDialog} from "@angular/material";
import {ElectronService} from "ngx-electron";
import {TimeZoneContext} from "../../../core/client-context/time-zone-context";
import {CLIENTCONTEXT} from "../../../core/client-context/client-context-provider.interface";
import {ActionService} from "../../../core/actions/action.service";
import {OpenposMediaService} from "../../../core/media/openpos-media.service";
import {KeyPressProvider} from "../../providers/keypress.provider";
import {IActionItem} from "../../../core/actions/action-item.interface";
import {By} from "@angular/platform-browser";
import {Configuration} from "../../../configuration/configuration";
import {ImageUrlPipe} from "../../pipes/image-url.pipe";
import {validateDoesNotExist, validateExist, validateIcon, validateText} from "../../../utilites/test-utils";

class MockMatDialog {};
class MockActionService {};
class MockKeyPressProvider {
    subscribe(): Subscription {
        return new Subscription();
    }
};
class MockElectronService {};
class ClientContext {};

describe('SaleTotalPanelComponent', () => {
    let component: SaleTotalPanelComponent;
    let fixture: ComponentFixture<SaleTotalPanelComponent>;
    let openposMediaSerivce: OpenposMediaService;

    describe('non mobile', () => {
        class MockOpenposMediaServiceMobile {
            observe(): Observable<boolean> {
                return of(false);
            }
        };
        beforeEach( () => {
            TestBed.configureTestingModule({
                imports: [HttpClientTestingModule, ],
                declarations: [
                    SaleTotalPanelComponent, ImageUrlPipe
                ],
                providers: [
                    { provide: ActionService, useClass: MockActionService },
                    { provide: MatDialog, useClass: MockMatDialog},
                    { provide: OpenposMediaService, useClass: MockOpenposMediaServiceMobile },
                    { provide: KeyPressProvider, useClass: MockKeyPressProvider },
                    { provide: ElectronService, useClass: MockElectronService },
                    { provide: ClientContext, useValue: {}},
                    { provide: CLIENTCONTEXT, useClass: TimeZoneContext}
                ],
                schemas: [
                    NO_ERRORS_SCHEMA,
                ]
            }).compileComponents();
            fixture = TestBed.createComponent(SaleTotalPanelComponent);
            component = fixture.componentInstance;
            component.screenData = {
                profileIcon: 'account_circle'
            } as SaleTotalPanelInterface;
            openposMediaSerivce = TestBed.get(OpenposMediaService);
            fixture.detectChanges();
        });

        it('renders', () => {
            expect(component).toBeDefined();
        });

        describe('component', () => {
            describe('keybindsEnabled', () => {
                let menuItem;
                beforeEach(() => {
                    Configuration.enableKeybinds = true;
                    menuItem = {
                        keybind: "F5"
                    };
                })
                it('returns false when Configuration.enableKeybinds is false', () => {
                    Configuration.enableKeybinds = false;
                    expect(component.keybindsEnabled(menuItem)).toBe(false);
                });

                it('returns false when Configuration.enableKeybinds is true and the menuItem.keybind is falsey', () => {
                    menuItem.keybind = undefined;
                    expect(component.keybindsEnabled(menuItem)).toBe(false);
                });

                it('returns false when Configuration.enableKeybinds is true and the menuItem.keybind is truthy and is "Enter"', () => {
                    menuItem.keybind = 'Enter';
                    expect(component.keybindsEnabled(menuItem)).toBe(false);
                });

                it('returns true when the Configuration.enableKeybinds is true and the menuItem.keybind is truthy and not "Enter"', () => {
                    expect(component.keybindsEnabled(menuItem)).toBe(true);
                });
            });
        });

        describe('template', () => {
            describe('sale-total-header', () => {
                const configureComponent = (readOnly: boolean, linkedCustomerButton: IActionItem, loyaltyButton: IActionItem, customer = undefined) => {
                    component.screenData.readOnly = readOnly;
                    component.screenData.linkedCustomerButton = linkedCustomerButton;
                    component.screenData.loyaltyButton = loyaltyButton;
                    component.screenData.customer = customer;
                    component.screenData.loyaltyIDLabel = "Loyalty ID";
                    fixture.detectChanges();
                };

                describe('when read only', () => {
                    beforeEach(() => {
                        configureComponent(true, undefined, {} as IActionItem);
                    });

                    it('does not show the link customer button', () => {
                        validateDoesNotExist(fixture, '.sale-total-header .link-customer');
                    });
                });
                describe('when there is no screen data for the loyaltyButton', () => {
                    beforeEach(() => {
                        configureComponent(false, undefined, undefined);
                    });
                    it('does not show the link customer button', () => {
                        validateDoesNotExist(fixture, '.sale-total-header .link-customer');
                    });
                });
                describe('when no customer is linked', () => {
                    beforeEach(() => {
                        configureComponent(false, undefined, {
                            keybindDisplayName: "F5",
                            title: "Link customer to apply rewards!"
                        } as IActionItem);
                    });

                    it('does not show the linked customer button', () => {
                        validateDoesNotExist(fixture, '.sale-total-header .linked-customer-summary');
                    });

                    it('calls doAction with the loyaltyButton when an actionClick event is triggered', () => {
                        spyOn(component, 'doAction');
                        const button = fixture.debugElement.query(By.css('.sale-total-header .link-customer'));
                        button.nativeElement.dispatchEvent(new Event('actionClick'));
                        expect(component.doAction).toHaveBeenCalledWith(component.screenData.loyaltyButton);
                    });

                    it('calls doAction with the loyaltyButton when an click event is triggered', () => {
                        spyOn(component, 'doAction');
                        const button = fixture.debugElement.query(By.css('.sale-total-header .link-customer'));
                        button.nativeElement.click();
                        expect(component.doAction).toHaveBeenCalledWith(component.screenData.loyaltyButton);
                    });

                    it('shows the keybinding when keybinds are enabled', () => {
                        spyOn(component, 'keybindsEnabled').and.returnValue(true);
                        fixture.detectChanges();
                        const button = fixture.debugElement.query(By.css('.sale-total-header .link-customer .loyalty-keybind'));
                        expect(button.nativeElement.textContent).toContain('F5');
                    });

                    it('hides the keybinding when keybinds are disabled', () => {
                        spyOn(component, 'keybindsEnabled').and.returnValue(false);
                        fixture.detectChanges();
                        validateDoesNotExist(fixture, '.sale-total-header .link-customer .loyalty-keybind');
                    });
                });
                describe('when a customer is linked', () => {
                    beforeEach(() => {
                        configureComponent(false, { keybindDisplayName: "F7" } as IActionItem, {} as IActionItem, {
                            name: 'bob',
                            label: 'manager',
                            icon: undefined,
                            id: '123'
                        });
                    });

                    it('does not show the link customer button', () => {
                        validateDoesNotExist(fixture, '.sale-total-header .link-customer');
                    });

                    it('shows the linked customer button', () => {
                        const button = fixture.debugElement.query(By.css('.sale-total-header button.linked-customer-summary'));
                        expect(button.nativeElement).toBeDefined();
                    });

                    describe('linked customer button', () => {
                        let button;
                        beforeEach(() => {
                            button = fixture.debugElement.query(By.css('.sale-total-header button.linked-customer-summary'));
                        });

                        it('displays a account_circle icon', () => {
                            const icon = fixture.debugElement.query(By.css('.sale-total-header .icon'));
                            expect(icon.nativeElement).toBeDefined();
                        });

                        it('displays the customers name', () => {
                            expect(button.nativeElement.textContent).toContain('bob');
                        });

                        it('calls doAction with the linkedCustomerButton when an actionClick event is triggered', () => {
                            spyOn(component, 'doAction');
                            button.nativeElement.dispatchEvent(new Event('actionClick'));
                            expect(component.doAction).toHaveBeenCalledWith(component.screenData.linkedCustomerButton);
                        });

                        it('calls doAction with the linkedCustomerButton when an click event is triggered', () => {
                            spyOn(component, 'doAction');
                            button.nativeElement.click();
                            expect(component.doAction).toHaveBeenCalledWith(component.screenData.linkedCustomerButton);
                        });

                        it('calls doAction with the linkedCustomerButton when an clickEvent event is triggered on the app-membership-display component', () => {
                            spyOn(component, 'doAction');
                            component.screenData.membershipEnabled = true;
                            component.screenData.memberships = [{id: '1', name: 'loyalty', member: true}];
                            fixture.detectChanges();

                            const membershipDisplay = fixture.debugElement.query(By.css('.linked-customer-summary app-membership-display'));
                            membershipDisplay.nativeElement.dispatchEvent(new Event('clickEvent'));

                            expect(component.doAction).toHaveBeenCalledWith(component.screenData.linkedCustomerButton);
                        });

                        describe('when customerMissingInfoEnabled is true', () => {
                            beforeEach(() => {
                                component.screenData.customerMissingInfoEnabled = true;
                                fixture.detectChanges();
                            });

                            describe('when customerMissingInfo is true', () => {
                                beforeEach(() => {
                                    component.screenData.customerMissingInfo = true;
                                    component.screenData.customerMissingInfoIcon = 'some icon';
                                    component.screenData.customerMissingInfoLabel = 'some label';
                                    fixture.detectChanges();
                                });

                                it('shows the customer missing info button', () => {
                                    validateExist(fixture, '.customer-missing-info');
                                    validateIcon(fixture, '.customer-missing-info app-icon', component.screenData.customerMissingInfoIcon);
                                    validateText(fixture, '.customer-missing-info .text', component.screenData.customerMissingInfoLabel);
                                });
                                it('dose not show the customer memberships', function () {
                                    component.screenData.membershipEnabled = true;
                                    component.screenData.memberships = [
                                        { id: '123', name: 'membership1', member: true},
                                        { id: '124', name: 'membership2', member: false},
                                        { id: '125', name: 'membership3', member: false}
                                    ];
                                    fixture.detectChanges();
                                    validateDoesNotExist(fixture, 'app-membership-display');
                                    component.screenData.membershipEnabled = false;
                                    component.screenData.memberships = [];
                                    fixture.detectChanges();

                                });
                                it('dose not show the no memberships label', function () {
                                    validateDoesNotExist(fixture, '.noMembershipsFound')
                                });
                            });

                            describe('when customerMissingInfo is false', () => {
                                beforeEach(() => {
                                    component.screenData.customerMissingInfo = false;
                                    component.screenData.customerMissingInfoIcon = 'some icon';
                                    component.screenData.customerMissingInfoLabel = 'some label';
                                    fixture.detectChanges();
                                });
                                it('does not show the customer missing info button', () => {
                                    validateDoesNotExist(fixture, 'customer-missing-info');
                                });
                            });
                        });

                        describe('when customerMissingInfoEnabled is false', () => {
                            beforeEach(() => {
                                component.screenData.customerMissingInfoEnabled = false;
                                fixture.detectChanges();
                            });

                            it('does not show the customer missing info button', () => {
                                validateDoesNotExist(fixture, 'customer-missing-info');
                            });
                        });

                        describe('when membershipEnabled is false', () => {
                            beforeEach(() => {
                                component.screenData.membershipEnabled = false;
                                fixture.detectChanges();
                                button = fixture.debugElement.query(By.css('.sale-total-header button.linked-customer-summary .loyaltyId'));
                            });

                            it('displays the loyalty id', () => {
                                expect(button.nativeElement.textContent).toContain('Loyalty ID:');
                                expect(button.nativeElement.textContent).toContain('123');
                            });
                        });

                        describe('when membershipEnabled is true', () => {
                            describe('when there are memberships', () => {
                                beforeEach(() => {
                                    component.screenData.membershipEnabled = true;
                                    component.screenData.memberships = [
                                        { id: '123', name: 'membership1', member: true},
                                        { id: '124', name: 'membership2', member: false},
                                        { id: '125', name: 'membership3', member: false}
                                    ];
                                    fixture.detectChanges();
                                });

                                it('does not display the loyalty id', () => {
                                    validateDoesNotExist(fixture, '.memberships .loyaltyId');
                                });

                                it('displays a membership-display component for each membership', () => {
                                    const membershipDisplays = fixture.debugElement.queryAll(By.css('app-membership-display'));
                                    expect(membershipDisplays.length).toBe(component.screenData.memberships.length);
                                });
                            });

                            describe('when there are no memberships', () => {
                                beforeEach(() => {
                                    component.screenData.membershipEnabled = true;
                                    component.screenData.memberships = [];
                                    component.screenData.noMembershipsFoundLabel = 'no memberships yet';
                                    fixture.detectChanges();
                                });

                                it('shows the noMembershipsFound label', () => {
                                    validateText(fixture, '.memberships', component.screenData.noMembershipsFoundLabel);
                                });
                            });


                        });

                    });
                });
            });
        });
    });
});