import {fakeAsync, TestBed} from '@angular/core/testing';
import {getTestScheduler} from 'jasmine-marbles';
import {Subject} from 'rxjs';
import {UIDataMessage} from '../messages/ui-data-message';
import {OpenposMessage} from '../messages/message';
import {ConnectedMessage, SessionService} from '../services/session.service';

import { UIDataMessageService } from './ui-data-message.service';

describe('UIDataMessageService', () => {

  let sessionService: jasmine.SpyObj<SessionService>;

  let fakeMessages: Subject<OpenposMessage>;
  let itemSearchResultsService: UIDataMessageService
  beforeEach(() => {
    TestBed.resetTestingModule();
      fakeMessages = new Subject<UIDataMessage<string[]>>();
    const sessionSpy = jasmine.createSpyObj('SessionService', ['getMessages']);

    TestBed.configureTestingModule({
      providers: [
          UIDataMessageService,
          { provide: SessionService, useValue: sessionSpy },
      ]
    });

    sessionService = TestBed.get(SessionService);
    sessionService.getMessages.and.callFake(() => fakeMessages);
    itemSearchResultsService = TestBed.get(UIDataMessageService);

  });

  it('should be created', () => {
    const service: UIDataMessageService = TestBed.get(UIDataMessageService);
    expect(service).toBeTruthy();
  });

  it( 'should accumulate messages with matching series id', fakeAsync(() => {
      fakeMessages.next(new UIDataMessage( 'ItemSearchResults', 1, [
          '123',
          '321'
      ]));

      fakeMessages.next(new UIDataMessage( 'ItemSearchResults', 1, [
        '456',
        '654'
      ]));

      fakeMessages.next(new UIDataMessage( 'ItemSearchResults', 1, [
        '789',
        '987'
      ]));

      let results: string[];

      itemSearchResultsService.getData$('ItemSearchResults').subscribe( r => {
        results = r;
      });

      getTestScheduler().flush();

      expect(results.length).toBe(6);

  }));

  it( 'should drop old results if series changes', fakeAsync( () => {
      fakeMessages.next(new UIDataMessage( 'ItemSearchResults', 1, [
          '123',
          '456'
      ]));
      fakeMessages.next(new UIDataMessage( 'ItemSearchResults', 1, [
          '456',
          '654'
      ]));
      fakeMessages.next(new UIDataMessage( 'ItemSearchResults', 2, [
          '789',
          '987'
      ]));

      let results: string[];

      itemSearchResultsService.getData$('ItemSearchResults').subscribe( r => {
          results = r;
      });

      getTestScheduler().flush();

      expect(results.length).toBe(2);
  }));

  it( 'should remove listener if series is -1', fakeAsync( () => {
      fakeMessages.next(new UIDataMessage( 'ItemSearchResults', 1, [
          '123',
          '321'
      ]));
      fakeMessages.next(new UIDataMessage( 'ItemSearchResults', -1, []));

      fakeMessages.next(new UIDataMessage( 'ItemSearchResults', 1, [
          '789',
          '987'
      ]));

      let results: string[];

      itemSearchResultsService.getData$('ItemSearchResults').subscribe( r => {
          results = r;
      });

      getTestScheduler().flush();

      expect(results.length).toBe(2);
  }));

  it( 'should delete cache on session connect', fakeAsync( ()=> {

      fakeMessages.next(new UIDataMessage( 'ItemSearchResults', 1, [
          '123',
          '321'
      ]));
      fakeMessages.next(new ConnectedMessage());

      fakeMessages.next(new UIDataMessage( 'ItemSearchResults', 1, [
          '789',
          '987'
      ]));

      let results: string[];

      itemSearchResultsService.getData$('ItemSearchResults').subscribe( r => {
          results = r;
      });

      getTestScheduler().flush();

      expect(results.length).toBe(2);

  }));
});
