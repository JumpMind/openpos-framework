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
import {RewardsHistoryLineItemComponent} from './rewards-history-line-item.component';
import {RewardHistory, RewardsHistoryLineItemComponentInterface} from './rewards-history-line-item.interface';
import {validateDoesNotExist, validateExist, validateIcon, validateText} from '../../../utilites/test-utils';
import {By} from '@angular/platform-browser';

class MockActionService {};
class MockMatDialog {};
class MockKeyPressProvider {
    subscribe(): Subscription {
        return new Subscription();
    }
};
class MockElectronService {};
class ClientContext {};

describe('RewardsHistoryLineItemComponent', () => {
    let component: RewardsHistoryLineItemComponent;
    let fixture: ComponentFixture<RewardsHistoryLineItemComponent>;
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
                    RewardsHistoryLineItemComponent
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
            fixture = TestBed.createComponent(RewardsHistoryLineItemComponent);
            component = fixture.componentInstance;
            component.reward = {
                expirationDate: '01/01/2000'
            } as RewardHistory;
            component.screenData = {
                expiredLabel: 'Expired',
                redeemedLabel: 'Redeemed',
                loyaltyIcon: 'loyalty',
                expiredIcon: 'access_time'
            } as RewardsHistoryLineItemComponentInterface;
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

                it('has the expired class when it is not redeemed', () => {
                    component.reward.redeemed = false;
                    fixture.detectChanges();

                    const nameElement = fixture.debugElement.query(By.css('.details .name'));
                    expect(nameElement.nativeElement.classList).toContain('expired');
                })

                describe('expiration', () => {
                    describe('when there is an expiration date', () => {
                        it('renders the access_time icon', () => {
                            validateIcon(fixture, '.details .expiration app-icon', 'access_time');
                        });

                        it('renders the expirationLabel', () => {
                            component.screenData.expiredLabel = 'a label';
                            fixture.detectChanges();

                            validateText(fixture, '.details .expiration', component.screenData.expiredLabel);
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

            describe('status', () => {
               it('shows the redeemedLabel when the reward is redeemed', () => {
                   component.screenData.redeemedLabel = 'redeemed label';
                    component.reward.redeemed = true;
                    fixture.detectChanges();
                    validateText(fixture, '.status', component.screenData.redeemedLabel);
               });

                it('shows the expiredLabel when the reward is not redeemed', () => {
                    component.screenData.expiredLabel = 'expired label';
                    component.reward.redeemed = false;
                    fixture.detectChanges();
                    validateText(fixture, '.status', component.screenData.expiredLabel);
                });
            });
        });
    });

    describe('mobile', () => {
        beforeEach( () => {
            TestBed.configureTestingModule({
                imports: [ HttpClientTestingModule],
                declarations: [
                    RewardsHistoryLineItemComponent
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
            fixture = TestBed.createComponent(RewardsHistoryLineItemComponent);
            component = fixture.componentInstance;
            component.reward = {} as RewardHistory;
            component.screenData = {
                expiredLabel: 'Expired',
                redeemedLabel: 'Redeemed',
                loyaltyIcon: 'loyalty',
                expiredIcon: 'access_time'
            } as RewardsHistoryLineItemComponentInterface;
            fixture.detectChanges();
        });
        describe('template', () => {
            describe('template', () => {
                it('has the mobile-reward-history-line-item-wrapper class and not the reward-history-line-item-wrapper', () => {
                    validateExist(fixture, '.mobile-reward-history-line-item-wrapper');
                    validateDoesNotExist(fixture, '.reward-history-line-item-wrapper');
                });

                it('does not render the loyalty-icon', () => {
                    validateDoesNotExist(fixture, '.loyalty-icon');
                });
            });
        });
    });

    describe('non-mobile', () => {
        beforeEach( () => {
            TestBed.configureTestingModule({
                imports: [ HttpClientTestingModule],
                declarations: [
                    RewardsHistoryLineItemComponent
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
            fixture = TestBed.createComponent(RewardsHistoryLineItemComponent);
            component = fixture.componentInstance;
            component.reward = {} as RewardHistory;
            component.screenData = {
                expiredLabel: 'Expired',
                redeemedLabel: 'Redeemed',
                loyaltyIcon: 'loyalty',
                expiredIcon: 'access_time'
            } as RewardsHistoryLineItemComponentInterface;
            fixture.detectChanges();
        });
        describe('template', () => {
            it('has the reward-history-line-item-wrapper class and not the mobile-reward-history-line-item-wrapper', () => {
                validateExist(fixture, '.reward-history-line-item-wrapper');
                validateDoesNotExist(fixture, '.mobile-reward-history-line-item-wrapper');
            });
            it('renders the loyalty-icon', () => {
                validateExist(fixture, '.loyalty-icon');
                validateIcon(fixture, '.loyalty-icon app-icon', 'loyalty');
            });
        });
    });
});