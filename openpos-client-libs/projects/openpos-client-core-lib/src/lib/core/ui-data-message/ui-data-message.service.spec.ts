import {TestBed} from '@angular/core/testing';
import {cold, getTestScheduler} from 'jasmine-marbles';
import {UIDataMessage} from '../messages/ui-data-message';
import {MessageTypes} from '../messages/message-types';
import {SessionService} from '../services/session.service';

import { UIDataMessageService } from './ui-data-message.service';

describe('UIDataMessageService', () => {

    let subscribe;
    let input;
    let results;

    beforeEach( () => {
        const sessionSpy = jasmine.createSpyObj('SessionService', ['getMessages']);
        TestBed.configureTestingModule({
            providers: [
                UIDataMessageService,
                {provide: SessionService, useValue: sessionSpy},
            ]
        });
        TestBed.get(SessionService).getMessages.and.callFake( type => type === MessageTypes.DATA ? input : subscribe );
        subscribe = cold( 's');
        results = [];

    });

    function subscribeToSut() {
        let sut = TestBed.get(UIDataMessageService);

        getTestScheduler().flush();

        sut.getData$('ItemSearchResults').subscribe( v => {
            results.push(...v);
        });
    }

    it('should accumulate messages with matching series id', () => {

        let values = {  x: new UIDataMessage<string[]>('ItemSearchResults', 1, ['123', '321']),
            y: new UIDataMessage<string[]>('ItemSearchResults', 1, ['456', '654']),
            z: new UIDataMessage<string[]>('ItemSearchResults', 1, ['789', '987']),
            a: ['123', '321', '456', '654', '789', '987']
        };

        input = cold('-x-y-z--|', values);

        subscribeToSut();

        expect(results).toEqual(values.a);
    });

    it( 'should drop old results if series changes', () => {

        let values = {  x: new UIDataMessage<string[]>('ItemSearchResults', 1, ['123', '321', '678']),
            y: new UIDataMessage<string[]>('ItemSearchResults', 1, ['456']),
            z: new UIDataMessage<string[]>('ItemSearchResults', 2, ['789', '987']),
            a: ['789', '987']
        };

        input = cold('-x-y-z--|', values);

        subscribeToSut();

        expect(results).toEqual(values.a);
    });

    it( 'should remove listener if series is -1', () => {
        let values = {  x: new UIDataMessage<string[]>('ItemSearchResults', 1, ['123', '321']),
            y: new UIDataMessage<string[]>('ItemSearchResults', -1, []),
            z: new UIDataMessage<string[]>('ItemSearchResults', 1, ['789', '987']),
            a: ['789', '987']
        };

        input = cold('-x-y-z--|', values);

        subscribeToSut();

        expect(results).toEqual(values.a);
    });

    it( 'should delete cache on session connect', ()=> {

        let values = {  x: new UIDataMessage<string[]>('ItemSearchResults', 1, ['123', '321']),
            y: new UIDataMessage<string[]>('ItemSearchResults', 1, ['456', '654']),
            z: new UIDataMessage<string[]>('ItemSearchResults', 1, ['789', '987']),
            a: ['789', '987']
        };

        input = cold('-x-y-z--|', values);
        subscribe = cold( '----s');

        subscribeToSut();

        expect(results).toEqual(values.a);

    });

});