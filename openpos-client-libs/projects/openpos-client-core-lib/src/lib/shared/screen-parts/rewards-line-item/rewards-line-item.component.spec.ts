import { TestBed, ComponentFixture } from '@angular/core/testing';
import { NO_ERRORS_SCHEMA } from '@angular/core'
import {MatDialog} from '@angular/material';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {Observable, of, Subscription} from 'rxjs';
import {MediaBreakpoints, OpenposMediaService} from '../../../core/media/openpos-media.service';
import {RewardsLineItemComponent} from './rewards-line-item.component';
import {ElectronService} from 'ngx-electron';
import {CLIENTCONTEXT} from '../../../core/client-context/client-context-provider.interface';
import {TimeZoneContext} from '../../../core/client-context/time-zone-context';
import {ActionService} from '../../../core/actions/action.service';
import {KeyPressProvider} from '../../providers/keypress.provider';
import {validateDoesNotExist, validateExist, validateIcon, validateText} from '../../../utilites/test-utils';
import {IActionItem} from '../../../core/actions/action-item.interface';
import {By} from '@angular/platform-browser';
import {Reward, RewardsLineItemComponentInterface} from './rewards-line-item.interface';

class MockActionService {};
class MockMatDialog {};
class MockKeyPressProvider {
    subscribe(): Subscription {
        return new Subscription();
    }
};
class MockElectronService {};
class ClientContext {};

describe('RewardsLineItemComponent', () => {
    let component: RewardsLineItemComponent;
    let fixture: ComponentFixture<RewardsLineItemComponent>;
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
                    RewardsLineItemComponent
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
            fixture = TestBed.createComponent(RewardsLineItemComponent);
            component = fixture.componentInstance;
            component.reward = {
                expirationDate: '01/01/2000'
            } as Reward;
            component.screenData = {
                expiresLabel: 'Expires',
                loyaltyIcon: 'loyalty',
                expiredIcon: 'access_time',
                applyIcon: 'chevron_right'
            } as RewardsLineItemComponentInterface;
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
            describe('details', () => {
                it('renders the name of the reward', () => {
                    component.reward.name = 'a name';
                    fixture.detectChanges();

                    validateText(fixture, '.details .name', component.reward.name);
                });

                describe('expiration', () => {
                    describe('when there is an expiration date', () => {
                        it('renders the access_time icon', () => {
                            validateIcon(fixture, '.details .expiration app-icon', 'access_time');
                        });

                        it('renders the expirationLabel', () => {
                            component.screenData.expiresLabel = 'a label';
                            fixture.detectChanges();

                            validateText(fixture, '.details .expiration', component.screenData.expiresLabel);
                        });
                    });

                    describe('when there is no expiration date', () => {
                        beforeEach(() => {
                            component.reward.expirationDate = undefined;
                            fixture.detectChanges();
                        });

                        it('does not display the expiration section', () => {
                            validateDoesNotExist(fixture, '.expiration');
                        });
                    });
                });
            });

            describe('reward', () => {
                describe('when reward has an amount', () => {
                    beforeEach(() => {
                        component.reward.amount = 200;
                        fixture.detectChanges();
                    });
                    it('renders the app-currency-text', () => {
                        validateExist(fixture, '.reward app-currency-text');
                    });
                });

                describe('when reward does not have an amount', () => {
                    beforeEach(() => {
                        component.reward.amount = undefined;
                        fixture.detectChanges();
                    });
                    it('does not render the app-currency-text', () => {
                        validateDoesNotExist(fixture, '.reward app-currency-text');
                    });
                });
            });

            describe('apply button', () => {
                describe('when applicable', () => {
                    beforeEach(() => {
                        component.reward.promotionId = '123';
                        component.reward.applyButton = {title: 'a title'} as IActionItem;
                        fixture.detectChanges();
                    });

                    it('renders the button', () => {
                        validateExist(fixture, '.apply a');
                    });

                    it('renders the button title', () => {
                        validateText(fixture, '.apply a', component.reward.applyButton.title);
                    });

                    it('renders the chevron icon', () => {
                        validateIcon(fixture, '.apply a app-icon', 'chevron_right');
                    });

                    it('calls doAction with the configuration when an actionClick event is triggered', () => {
                        spyOn(component, 'doAction');
                        const button = fixture.debugElement.query(By.css('.apply a'));
                        button.nativeElement.dispatchEvent(new Event('actionClick'));
                        expect(component.doAction).toHaveBeenCalledWith(component.reward.applyButton, component.reward.promotionId);
                    });

                    it('calls doAction with the configuration and promotionId when the button is clicked', () => {
                        spyOn(component, 'doAction');
                        const button = fixture.debugElement.query(By.css('.apply a'));
                        button.nativeElement.click();
                        expect(component.doAction).toHaveBeenCalledWith(component.reward.applyButton, component.reward.promotionId);
                    });
                });

                describe('when not applicable', () => {
                   beforeEach(() => {
                      component.reward.applyButton = undefined;
                      fixture.detectChanges();
                   });

                   it('does not render the apply button', () => {
                      validateDoesNotExist(fixture, '.apply a');
                   });
                });
            });
        });
    });

    describe('mobile', () => {
        beforeEach( () => {
            TestBed.configureTestingModule({
                imports: [ HttpClientTestingModule],
                declarations: [
                    RewardsLineItemComponent
                ],
                providers: [
                    { provide: ActionService, useClass: MockActionService },
                    { provide: MatDialog, useClass: MockMatDialog },
                    { provide: OpenposMediaService, useClass: MockOpenposMediaServiceMobileTrue },
                    { provide: KeyPressProvider, useClass: MockKeyPressProvider },
                    { provide: ElectronService, useClass: MockElectronService },
                    { provide: ClientContext, useValue: {}},
                    { provide: CLIENTCONTEXT, useClass: TimeZoneContext}
                ],
                schemas: [
                    NO_ERRORS_SCHEMA,
                ]
            }).compileComponents();
            fixture = TestBed.createComponent(RewardsLineItemComponent);
            component = fixture.componentInstance;
            component.reward = {} as Reward;
            component.screenData = {
                expiresLabel: 'Expires',
                loyaltyIcon: 'loyalty',
                expiredIcon: 'access_time',
                applyIcon: 'chevron_right'
            } as RewardsLineItemComponentInterface;
            fixture.detectChanges();
        });
        describe('template', () => {
            it('has the mobile-reward-line-item-wrapper class and not the reward-line-item-wrapper', () => {
               validateExist(fixture, '.mobile-reward-line-item-wrapper');
               validateDoesNotExist(fixture, '.reward-line-item-wrapper');
            });

            it('does not render the loyalty-icon', () => {
                validateDoesNotExist(fixture, '.loyalty-icon');
            });
        });
    });

    describe('non-mobile', () => {
        beforeEach( () => {
            TestBed.configureTestingModule({
                imports: [ HttpClientTestingModule],
                declarations: [
                    RewardsLineItemComponent
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
            fixture = TestBed.createComponent(RewardsLineItemComponent);
            component = fixture.componentInstance;
            component.reward = {} as Reward;
            component.screenData = {
                expiresLabel: 'Expires',
                loyaltyIcon: 'loyalty',
                expiredIcon: 'access_time',
                applyIcon: 'chevron_right'
            } as RewardsLineItemComponentInterface;
            fixture.detectChanges();
        });
        describe('template', () => {
            it('has the reward-line-item-wrapper class and not the mobile-reward-line-item-wrapper', () => {
                validateExist(fixture, '.reward-line-item-wrapper');
                validateDoesNotExist(fixture, '.mobile-reward-line-item-wrapper');
            });
            it('renders the loyalty-icon', () => {
               validateExist(fixture, '.loyalty-icon');
               validateIcon(fixture, '.loyalty-icon app-icon', 'loyalty');
            });
        });
    });
});