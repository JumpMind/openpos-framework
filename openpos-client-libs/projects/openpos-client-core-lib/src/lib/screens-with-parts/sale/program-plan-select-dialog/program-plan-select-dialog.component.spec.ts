import { TestBed, ComponentFixture } from '@angular/core/testing';
import { NO_ERRORS_SCHEMA } from '@angular/core'

import {ActionService} from '../../../core/actions/action.service';
import {validateExist} from '../../../utilites/test-utils';
import {IActionItem} from '../../../core/actions/action-item.interface';
import {MatDialog} from '@angular/material';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {ElectronService} from 'ngx-electron';
import {CLIENTCONTEXT} from '../../../core/client-context/client-context-provider.interface';
import {TimeZoneContext} from '../../../core/client-context/time-zone-context';
import {Observable, of, Subscription} from 'rxjs';
import {MediaBreakpoints, OpenposMediaService} from '../../../core/media/openpos-media.service';
import {KeyPressProvider} from "../../../shared/providers/keypress.provider";

import {ProgramPlanSelectDialogComponent} from "./program-plan-select-dialog.component";
import {ProgramPlanSelectDialogInterface} from "./program-plan-select-dialog.interface";
import {Configuration} from "../../../configuration/configuration";

class MockKeyPressProvider {
  subscribe(): Subscription {
    return new Subscription();
  }
};
class MockActionService {};
class MockMatDialog {};
class MockElectronService {};
class ClientContext {};

describe('ProgramPlansSelectDialog', () => {
  let component: ProgramPlanSelectDialogComponent;
  let fixture: ComponentFixture<ProgramPlanSelectDialogComponent>;
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

  });

  describe('shared', () => {
    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [
          ProgramPlanSelectDialogComponent
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
      fixture = TestBed.createComponent(ProgramPlanSelectDialogComponent);
      component = fixture.componentInstance;
      component.screen = {
      } as ProgramPlanSelectDialogInterface;
      fixture.detectChanges();
    });

    it('renders', () => {
      expect(component).toBeDefined();
    });

    describe('template', () => {
      it('should have a header', function () {
        validateExist(fixture, "app-dialog-header");
      });

      it('should display a app-program-plan-details', function () {
        validateExist(fixture, ".program-plan-selection-dialog app-program-plan-details");
      });
    });

    describe('component', () => {
      describe('initIsMobile', () => {
        it('sets the values for isMobile', () => {
          const media: OpenposMediaService = TestBed.get(OpenposMediaService);
          spyOn(media, 'observe');
          component.initIsMobile();
          expect(media.observe).toHaveBeenCalledWith(new Map([[MediaBreakpoints.MOBILE_PORTRAIT, true],
            [MediaBreakpoints.MOBILE_LANDSCAPE, true],
            [MediaBreakpoints.TABLET_PORTRAIT, true],
            [MediaBreakpoints.TABLET_LANDSCAPE, true],
            [MediaBreakpoints.DESKTOP_PORTRAIT, false],
            [MediaBreakpoints.DESKTOP_LANDSCAPE, false]
          ]));
        });
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
  });
});