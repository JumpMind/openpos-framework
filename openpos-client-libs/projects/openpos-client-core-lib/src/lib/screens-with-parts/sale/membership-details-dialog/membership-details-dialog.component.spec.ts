import { TestBed, ComponentFixture } from '@angular/core/testing';
import { NO_ERRORS_SCHEMA } from '@angular/core'

import {ActionService} from '../../../core/actions/action.service';
import {validateDoesNotExist, validateExist, validateText} from '../../../utilites/test-utils';
import {By} from '@angular/platform-browser';
import {IActionItem} from '../../../core/actions/action-item.interface';
import {PhonePipe} from '../../../shared/pipes/phone.pipe';
import {MatDialog} from '@angular/material';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {ElectronService} from 'ngx-electron';
import {CLIENTCONTEXT} from '../../../core/client-context/client-context-provider.interface';
import {TimeZoneContext} from '../../../core/client-context/time-zone-context';
import {Observable, of, Subscription} from 'rxjs';
import {MediaBreakpoints, OpenposMediaService} from '../../../core/media/openpos-media.service';
import {Reward} from '../../../shared/screen-parts/rewards-line-item/rewards-line-item.interface';
import {KeyPressProvider} from "../../../shared/providers/keypress.provider";
import {MembershipDetailsDialogComponent} from "./membership-details-dialog.component";

import {promisify} from "util";
import {ActionItem} from "../../../core/actions/action-item";
import {Configuration} from "../../../configuration/configuration";
import {EnrollmentItem, Plan, SubscriptionAccounts} from "../program-interface";
import {MembershipDetailsDialogInterface} from "./membership-details-dialog.interface";
// import custom = module

class MockKeyPressProvider {
  subscribe(): Subscription {
    return new Subscription();
  }
};
class MockActionService {};
class MockMatDialog {};
class MockElectronService {};
class ClientContext {};

describe('LinkedCustomerMembershipState', () => {
  let component: MembershipDetailsDialogComponent;
  let fixture: ComponentFixture<MembershipDetailsDialogComponent>;
  let customer;
  let subscriptionAccount: SubscriptionAccounts;
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

  beforeEach(() => {
    customer = {
      name: 'Ige Wana',
      email: 'IgeWana@gmail.com',
      phoneNumber: '1118798322',
      loyaltyNumber: 's321111111',
      address: {
        line1: '123 Lizard Lane',
        city: 'Columbus',
        state: 'OH',
        postalCode: '11111'
      }
    };
  });

  describe('shared', () => {
    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [
          MembershipDetailsDialogComponent, PhonePipe
        ],
        providers: [
          {provide: KeyPressProvider, useClass: MockKeyPressProvider},
          {provide: ActionService, useClass: MockActionService},
          {provide: MatDialog, useClass: MockMatDialog},
          {provide: OpenposMediaService, useClass: MockOpenposMediaServiceMobileFalse},
          {provide: ElectronService, useClass: MockElectronService},
          {provide: ClientContext, useValue: {}},
          {provide: CLIENTCONTEXT, useClass: TimeZoneContext}
        ],
        schemas: [
          NO_ERRORS_SCHEMA,
        ]
      }).compileComponents();
      fixture = TestBed.createComponent(MembershipDetailsDialogComponent);
      component = fixture.componentInstance;
      component.screen = {
        customer: customer,
        membershipLabel: "membershipLabel",
        membershipCardIcon: "membershipCardIcon",
        profileIcon: "profileIcon"
      } as MembershipDetailsDialogInterface;
      fixture.detectChanges();
    });

    it('renders', () => {
      expect(component).toBeDefined();
    });

    describe('component', () =>{
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
      describe('keybindsEnabled ', () => {
        describe("configuration keybinds are enabled", () =>{
          it('and menuItem keybind is not enter', function () {
            Configuration.enableKeybinds = true;
            let menuItem: IActionItem = {keybind: 'NotEnter'} as IActionItem;
            expect(component.keybindsEnabled(menuItem)).toBeTruthy();
          });

          it('and menuItem keybind is Enter', function () {
            Configuration.enableKeybinds = true;
            let menuItem: IActionItem = {keybind: "Enter"} as IActionItem;
            expect(component.keybindsEnabled(menuItem)).toBeFalsy();
          });

          it('and menuItem keybind is null', function () {
            Configuration.enableKeybinds = true;
            let menuItem: IActionItem = {keybind: null} as IActionItem;
            expect(component.keybindsEnabled(menuItem)).toBeFalsy();
          });
        });
        describe("configuration keybinds are disabled", () =>{
          it('and menuItem keybind is not enter', function () {
            Configuration.enableKeybinds = false;
            let menuItem: IActionItem = {keybind: "NotEnter"} as IActionItem;
            expect(component.keybindsEnabled(menuItem)).toBeFalsy();
          });

          it('and menuItem keybind is Enter', function () {
            Configuration.enableKeybinds = false;
            let menuItem: IActionItem = {keybind: "Enter"} as IActionItem;
            expect(component.keybindsEnabled(menuItem)).toBeFalsy();
          });

          it('and menuItem keybind is null', function () {
            Configuration.enableKeybinds = false;
            let menuItem: IActionItem = {keybind: null} as IActionItem;
            expect(component.keybindsEnabled(menuItem)).toBeFalsy();
          });
        });
      });
    });

    describe('template', () => {
      beforeEach(() => {
        let subscriptionAccounts:SubscriptionAccounts[] = [];
        let enrollmentItem: EnrollmentItem = {} as EnrollmentItem;
        let plan: Plan = {} as Plan;
        subscriptionAccount = {
          iconImageUrl: "iconImageUrl",
          iconText: "iconText",
          enrollmentItems: [enrollmentItem],
          listTitle: "listTitle",
          plans: [plan],
          signupActionItem: {
            enabled: true,
            icon: "signupActionItem.icon",
            title: "signupActionItem.title"
          } as ActionItem
        } as SubscriptionAccounts;
        subscriptionAccounts.push(subscriptionAccount);
        component.screen.subscriptionAccounts = subscriptionAccounts
        fixture.detectChanges();
      });
      it('renders the profile icon in the customer details', () => {
        validateExist(fixture, '.details-wrapper .icon app-icon');
      });
      it('renders the customer name in the customer details', () => {
        validateText(fixture, '.details-wrapper .details .details-label', customer.name);
      });
      it('renders the customer name in the customer details', () => {
        validateExist(fixture, '.details-wrapper .memberships .list');
      });
      describe('tab functionality', () => {
        it('should display tabs', () => {
          validateExist(fixture, '.tabs');
        });
        it('should display subscriptionAccountListTitle', function () {
          validateText(fixture, '.tabs .tab-title', subscriptionAccount.listTitle);
        });
        it('should display app-enrollment-line-items', function () {
          validateExist(fixture, '.tabs app-enrollment-line-item');
        });
        it('should display app-program-plan-details', function () {
          component.screen.subscriptionAccounts[0].enrollmentItems = null;
          fixture.detectChanges();

          validateExist(fixture, 'app-program-plan-details');
        });
        it('should display have sign-up', function () {
          validateExist(fixture, '.tabs .sign-up')
        });
      });
    });
  });
});