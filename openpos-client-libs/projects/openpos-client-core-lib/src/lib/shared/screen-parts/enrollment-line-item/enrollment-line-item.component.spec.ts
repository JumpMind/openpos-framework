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
    // let component: RewardsLineItemComponent;
    // let fixture: ComponentFixture<RewardsLineItemComponent>;
    // class MockOpenposMediaServiceMobileFalse {
    //     observe(): Observable<boolean> {
    //         return of(false);
    //     }
    // };
    //
    // class MockOpenposMediaServiceMobileTrue {
    //     observe(): Observable<boolean> {
    //         return of(true);
    //     }
    // };
    //
    // describe('shared', () => {
    //     beforeEach( () => {
    //         TestBed.configureTestingModule({
    //             imports: [ HttpClientTestingModule],
    //             declarations: [
    //                 RewardsLineItemComponent
    //             ],
    //             providers: [
    //                 { provide: ActionService, useClass: MockActionService },
    //                 { provide: MatDialog, useClass: MockMatDialog },
    //                 { provide: OpenposMediaService, useClass: MockOpenposMediaServiceMobileFalse },
    //                 { provide: KeyPressProvider, useClass: MockKeyPressProvider },
    //                 { provide: ElectronService, useClass: MockElectronService },
    //                 { provide: ClientContext, useValue: {}},
    //                 { provide: CLIENTCONTEXT, useClass: TimeZoneContext}
    //             ],
    //             schemas: [
    //                 NO_ERRORS_SCHEMA,
    //             ]
    //         }).compileComponents();
    //         fixture = TestBed.createComponent(RewardsLineItemComponent);
    //         component = fixture.componentInstance;
    //         component.reward = {
    //             expirationDate: '01/01/2000',
    //             actionButton: {title: 'a title', enabled: true}
    //         } as Reward;
    //         component.screenData = {
    //             expiresLabel: 'Expires',
    //             loyaltyIcon: 'loyalty',
    //             expiredIcon: 'access_time'
    //         } as RewardsLineItemComponentInterface;
    //         fixture.detectChanges();
    //     });
    //
    //     it('renders', () => {
    //         expect(component).toBeDefined();
    //     });
    //
    //     describe('component', () => {
    //         describe('initIsMobile', () => {
    //            it('sets the values for isMobile', () => {
    //                const media: OpenposMediaService = TestBed.get(OpenposMediaService);
    //                spyOn(media, 'observe');
    //
    //                component.initIsMobile();
    //
    //                expect(media.observe).toHaveBeenCalledWith(new Map([
    //                    [MediaBreakpoints.MOBILE_PORTRAIT, true],
    //                    [MediaBreakpoints.MOBILE_LANDSCAPE, true],
    //                    [MediaBreakpoints.TABLET_PORTRAIT, true],
    //                    [MediaBreakpoints.TABLET_LANDSCAPE, false],
    //                    [MediaBreakpoints.DESKTOP_PORTRAIT, false],
    //                    [MediaBreakpoints.DESKTOP_LANDSCAPE, false]
    //                ]));
    //            });
    //         });
    //     });
    //
    //     describe('template', () => {
    //         describe('details', () => {
    //             it('renders the name of the reward', () => {
    //                 component.reward.name = 'a name';
    //                 fixture.detectChanges();
    //
    //                 validateText(fixture, '.details .name', component.reward.name);
    //             });
    //
    //             describe('expiration', () => {
    //                 describe('when there is an expiration date', () => {
    //                     it('renders the access_time icon', () => {
    //                         validateIcon(fixture, '.details .expiration app-icon', 'access_time');
    //                     });
    //
    //                     it('renders the expirationLabel', () => {
    //                         component.screenData.expiresLabel = 'a label';
    //                         fixture.detectChanges();
    //
    //                         validateText(fixture, '.details .expiration', component.screenData.expiresLabel);
    //                     });
    //                 });
    //
    //                 describe('when there is no expiration date', () => {
    //                     beforeEach(() => {
    //                         component.reward.expirationDate = undefined;
    //                         fixture.detectChanges();
    //                     });
    //
    //                     it('does not display the expiration section', () => {
    //                         validateDoesNotExist(fixture, '.expiration');
    //                     });
    //                 });
    //             });
    //         });
    //
    //         describe('reward', () => {
    //             describe('when reward has an amount', () => {
    //                 beforeEach(() => {
    //                     component.reward.amount = 200;
    //                     fixture.detectChanges();
    //                 });
    //                 it('renders the app-currency-text', () => {
    //                     validateExist(fixture, '.reward app-currency-text');
    //                 });
    //             });
    //
    //             describe('when reward does not have an amount', () => {
    //                 beforeEach(() => {
    //                     component.reward.amount = undefined;
    //                     fixture.detectChanges();
    //                 });
    //                 it('does not render the app-currency-text', () => {
    //                     validateDoesNotExist(fixture, '.reward app-currency-text');
    //                 });
    //             });
    //         });
    //
    //         describe('reward extra status text', () => {
    //             beforeEach(() => {
    //                 component.reward.promotionId = '123';
    //                 component.reward.statusText = 'Bonus Reward';
    //                 component.reward.actionButton = {title: 'a title', enabled: true} as IActionItem;
    //                 fixture.detectChanges();
    //             });
    //
    //             it('shows the configured status text when enabled', () => {
    //                 validateText(fixture, '.status-text', component.reward.statusText);
    //             });
    //         });
    //
    //         describe('when status text does not have a value', () => {
    //             beforeEach(() => {
    //                 component.reward.statusText = undefined;
    //                 fixture.detectChanges();
    //             });
    //             it('does not render the status text', () => {
    //                 validateDoesNotExist(fixture, '.status-text .name');
    //             });
    //         });
    //
    //         describe('apply button', () => {
    //             beforeEach(() => {
    //                 component.reward.promotionId = '123';
    //                 component.reward.actionButton = {title: 'a title', enabled: true} as IActionItem;
    //                 component.reward.actionIcon = 'chevron_right'
    //                 fixture.detectChanges();
    //             });
    //
    //             it('renders the button', () => {
    //                 validateExist(fixture, '.apply a');
    //             });
    //
    //             it('renders the button title', () => {
    //                 validateText(fixture, '.apply a', component.reward.actionButton.title);
    //             });
    //
    //             it('renders the chevron icon', () => {
    //                 validateIcon(fixture, '.apply a app-icon', 'chevron_right');
    //             });
    //
    //             it('calls doAction with the configuration when an actionClick event is triggered', () => {
    //                 spyOn(component, 'doAction');
    //                 const button = fixture.debugElement.query(By.css('.apply a'));
    //                 button.nativeElement.dispatchEvent(new Event('actionClick'));
    //                 expect(component.doAction).toHaveBeenCalledWith(component.reward.actionButton);
    //             });
    //
    //             it('calls doAction with the configuration and promotionId when the button is clicked', () => {
    //                 spyOn(component, 'doAction');
    //                 const button = fixture.debugElement.query(By.css('.apply a'));
    //                 button.nativeElement.click();
    //                 expect(component.doAction).toHaveBeenCalledWith(component.reward.actionButton);
    //             });
    //
    //             it('is enabled when the button is enabled', () => {
    //                 component.reward.actionButton.enabled = true;
    //                 fixture.detectChanges();
    //                 const button = fixture.debugElement.query(By.css('.apply a'));
    //                 expect(button.properties.disabled).toBe(false);
    //             });
    //
    //             it('is disabled when the button is disabled', () => {
    //                 component.reward.actionButton.enabled = false;
    //                 fixture.detectChanges();
    //                 const button = fixture.debugElement.query(By.css('.apply a'));
    //                 expect(button.properties.disabled).toBe(true);
    //             });
    //         });
    //     });
    // });
    //
    // describe('mobile', () => {
    //     beforeEach( () => {
    //         TestBed.configureTestingModule({
    //             imports: [ HttpClientTestingModule],
    //             declarations: [
    //                 RewardsLineItemComponent
    //             ],
    //             providers: [
    //                 { provide: ActionService, useClass: MockActionService },
    //                 { provide: MatDialog, useClass: MockMatDialog },
    //                 { provide: OpenposMediaService, useClass: MockOpenposMediaServiceMobileTrue },
    //                 { provide: KeyPressProvider, useClass: MockKeyPressProvider },
    //                 { provide: ElectronService, useClass: MockElectronService },
    //                 { provide: ClientContext, useValue: {}},
    //                 { provide: CLIENTCONTEXT, useClass: TimeZoneContext}
    //             ],
    //             schemas: [
    //                 NO_ERRORS_SCHEMA,
    //             ]
    //         }).compileComponents();
    //         fixture = TestBed.createComponent(RewardsLineItemComponent);
    //         component = fixture.componentInstance;
    //         component.reward = {
    //             actionButton: {title: 'a title', enabled: true}
    //         } as Reward;
    //         component.screenData = {
    //             expiresLabel: 'Expires',
    //             loyaltyIcon: 'loyalty',
    //             expiredIcon: 'access_time',
    //         } as RewardsLineItemComponentInterface;
    //         fixture.detectChanges();
    //     });
    //     describe('template', () => {
    //         it('has the mobile-reward-line-item-wrapper class and not the reward-line-item-wrapper', () => {
    //            validateExist(fixture, '.mobile-reward-line-item-wrapper');
    //            validateDoesNotExist(fixture, '.reward-line-item-wrapper');
    //         });
    //
    //         it('does not render the loyalty-icon', () => {
    //             validateDoesNotExist(fixture, '.loyalty-icon');
    //         });
    //     });
    // });
    //
    // describe('non-mobile', () => {
    //     beforeEach( () => {
    //         TestBed.configureTestingModule({
    //             imports: [ HttpClientTestingModule],
    //             declarations: [
    //                 RewardsLineItemComponent
    //             ],
    //             providers: [
    //                 { provide: ActionService, useClass: MockActionService },
    //                 { provide: MatDialog, useClass: MockMatDialog },
    //                 { provide: OpenposMediaService, useClass: MockOpenposMediaServiceMobileFalse },
    //                 { provide: KeyPressProvider, useClass: MockKeyPressProvider },
    //                 { provide: ElectronService, useClass: MockElectronService },
    //                 { provide: ClientContext, useValue: {}},
    //                 { provide: CLIENTCONTEXT, useClass: TimeZoneContext}
    //             ],
    //             schemas: [
    //                 NO_ERRORS_SCHEMA,
    //             ]
    //         }).compileComponents();
    //         fixture = TestBed.createComponent(RewardsLineItemComponent);
    //         component = fixture.componentInstance;
    //         component.reward = {
    //             actionButton: {title: 'a title', enabled: true}
    //         } as Reward;
    //         component.screenData = {
    //             expiresLabel: 'Expires',
    //             loyaltyIcon: 'loyalty',
    //             expiredIcon: 'access_time',
    //         } as RewardsLineItemComponentInterface;
    //         fixture.detectChanges();
    //     });
    //     describe('template', () => {
    //         it('has the reward-line-item-wrapper class and not the mobile-reward-line-item-wrapper', () => {
    //             validateExist(fixture, '.reward-line-item-wrapper');
    //             validateDoesNotExist(fixture, '.mobile-reward-line-item-wrapper');
    //         });
    //         it('renders the loyalty-icon', () => {
    //            validateExist(fixture, '.loyalty-icon');
    //            validateIcon(fixture, '.loyalty-icon app-icon', 'loyalty');
    //         });
    //     });
    // });
});