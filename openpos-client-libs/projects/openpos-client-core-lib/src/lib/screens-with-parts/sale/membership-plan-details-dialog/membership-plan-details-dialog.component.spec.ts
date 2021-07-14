import { TestBed, ComponentFixture } from '@angular/core/testing';
import { NO_ERRORS_SCHEMA } from '@angular/core'

import {ActionService} from '../../../core/actions/action.service';
import {validateDoesNotExist, validateExist, validateText} from '../../../utilites/test-utils';
import {PhonePipe} from '../../../shared/pipes/phone.pipe';
import {MatDialog} from '@angular/material';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {ElectronService} from 'ngx-electron';
import {CLIENTCONTEXT} from '../../../core/client-context/client-context-provider.interface';
import {TimeZoneContext} from '../../../core/client-context/time-zone-context';
import {Observable, of, Subscription} from 'rxjs';
import {MediaBreakpoints, OpenposMediaService} from '../../../core/media/openpos-media.service';
import {KeyPressProvider} from "../../../shared/providers/keypress.provider";

import {ActionItem} from "../../../core/actions/action-item";
import {EnrollmentItem, EnrollmentItemProperty, Plan} from "../program-interface";
import {MembershipPlanDetailsDialogComponent} from "./membership-plan-details-dialog.component";
import {MembershipPlanDetailsDialogInterface} from "./membership-plan-details-dialog.interface";

class MockKeyPressProvider {
  subscribe(): Subscription {
    return new Subscription();
  }
};
class MockActionService {};
class MockMatDialog {};
class MockElectronService {};
class ClientContext {};

describe('LinkedCustomerMembershipState - Plan Details Dialog', () => {
  let component: MembershipPlanDetailsDialogComponent;
  let fixture: ComponentFixture<MembershipPlanDetailsDialogComponent>;

  class MockOpenposMediaServiceMobileFalse {
    observe(): Observable<boolean> {
      return of(false);
    }
  };

  describe('shared', () => {
    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [
          MembershipPlanDetailsDialogComponent, PhonePipe
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
      fixture = TestBed.createComponent(MembershipPlanDetailsDialogComponent);
      component = fixture.componentInstance;
      component.screen = {
        doneButton: { title: 'Done', enabled: true } as ActionItem
      } as MembershipPlanDetailsDialogInterface;
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

      describe('template', () => {
        beforeEach(() => {
          fixture.detectChanges();
        });

        it('renders the enrollment line item component', () => {
          validateExist(fixture, '.plan-details-dialog-enrollment');
        });

        it('renders the plan details component', () => {
          validateExist(fixture, '.plan-details-dialog-plan');
        });

        it('has an optional done button', () => {
          component.screen.doneButton = undefined;
          fixture.detectChanges();
          validateDoesNotExist(fixture, '.plan-details-dialog-done-button');
        });

        it('renders the done button', () => {
          validateText(fixture, '.plan-details-dialog-done-button', component.screen.doneButton.title);
        });
      });
    });
  });
});