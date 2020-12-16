import { Logger } from './../../../core/services/logger.service';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { BlankComponent } from './blank.component';
import { AppInjector } from '../../../core/app-injector';
import { Injector } from '@angular/core';
import { SessionService } from '../../../core/services/session.service';
import { ActionService } from '../../../core/actions/action.service';
import { MatDialog } from '@angular/material';
import { MessageProvider } from '../../../shared/providers/message.provider';
import { BehaviorSubject } from 'rxjs';

const testScreen = {};
const scopedMessages$ = new BehaviorSubject(testScreen);
const allMessages$ = new BehaviorSubject(testScreen);

describe('BlankComponent', () => {
  let component: BlankComponent;
  let fixture: ComponentFixture<BlankComponent>;

  beforeEach((() => {

    const sessionServiceSpy = jasmine.createSpyObj('SessionService', ['getMessages']);
    const loggerSpy = jasmine.createSpyObj('Logger', ['info']);
    const matDialogSpy = jasmine.createSpyObj('MatDialog', ['open']);
    const messageProviderSpy = jasmine.createSpyObj('MessageProvider', ['sendMessage', 'getScopedMessages$', 'getAllMessages$']);

    TestBed.configureTestingModule({
      declarations: [ BlankComponent ],
      providers: [
        { provide: SessionService, useValue: sessionServiceSpy },
        { provide: Logger, useValue: loggerSpy },
        { provide: MatDialog, useValue: matDialogSpy },
        ActionService,
        { provide: MessageProvider, useValue: messageProviderSpy}
    ]
    });
    messageProviderSpy.getScopedMessages$.and.returnValue(scopedMessages$);
    messageProviderSpy.getAllMessages$.and.returnValue(allMessages$);

    AppInjector.Instance = TestBed.get(Injector);
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(BlankComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });
});
