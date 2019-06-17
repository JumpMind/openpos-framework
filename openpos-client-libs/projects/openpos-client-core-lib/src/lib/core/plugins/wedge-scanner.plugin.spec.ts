import { TestBed, fakeAsync, tick } from '@angular/core/testing';
import { WedgeScannerPlugin } from './wedge-scanner.plugin';
import { SessionService } from '../services/session.service';
import { cold, getTestScheduler } from 'jasmine-marbles';
import { IScanData } from './scan.interface';
import { DomEventManager } from '../services/dom-event-manager.service';
import { Subscription, of, Subject } from 'rxjs';
import { Logger } from '../services/logger.service';
import { ElectronService } from 'ngx-electron';

describe('WedgeScanner', () => {

    const config = {
        configType: 'WedgeScanner',
        startSequence: '*',
        endSequence: 'Enter',
        codeTypeLength: 1,
        timeout: null
    };

    let scanResults: IScanData[];
    let keyDownEvents: KeyboardEvent[];
    let subscription: Subscription;
    let sessionService: jasmine.SpyObj<SessionService>;
    let domEventManager: jasmine.SpyObj<DomEventManager>;
    let wedgeScannerPlugin: WedgeScannerPlugin;
    let domEventManagerSpy;

    function queueEvent( key: string, ctrlKey: boolean, altKey: boolean ) {
        const event = new KeyboardEvent('keydown', {
            key,
            ctrlKey,
            altKey,
        });

        keyDownEvents.push(event);
    }

    function queueTime( time: string ) {
        keyDownEvents.push( new KeyboardEvent(time));
    }

    function makeEventCold() {
        let marbles = '-';
        const values = {};

        keyDownEvents.forEach( ( e, index ) => {
            if ( e.type === 'keydown') {
                marbles = marbles.concat(String.fromCharCode(index + 97) + '-');
                values[String.fromCharCode(index + 97)] = e;
            } else {
                marbles = marbles.concat(e.type);
            }
        });
        return cold(marbles, values);
    }

    function getConfig() {
        return cold('-x', {x: config});
    }

    function setup() {
        const sessionSpy = jasmine.createSpyObj('SessionService', ['getMessages']);
        domEventManagerSpy = jasmine.createSpyObj('DomEventManager', ['createEventObserver']);

        TestBed.configureTestingModule({
            providers: [
                WedgeScannerPlugin,
                ElectronService,
                Logger,
                { provide: SessionService, useValue: sessionSpy },
                { provide: DomEventManager, useValue: domEventManagerSpy}
            ]
        });

        sessionService = TestBed.get(SessionService);
        domEventManager = TestBed.get(DomEventManager);

        sessionService.getMessages.and.callFake(getConfig);

        wedgeScannerPlugin = TestBed.get(WedgeScannerPlugin);
    }

    function setupSync() {
        setup();
        domEventManager.createEventObserver.and.callFake(makeEventCold);

        subscription = wedgeScannerPlugin.startScanning().subscribe( s => scanResults.push(s));
    }

    const fakeEventSubject = new Subject();

    function dispatchEvent( key: string, ctrlKey: boolean, altKey: boolean ) {
        const event = new KeyboardEvent('keydown', {
            key,
            ctrlKey,
            altKey,
        });

        fakeEventSubject.next(event);
    }

    function setupAsync() {
        setup();
        domEventManager.createEventObserver.and.callFake(() => fakeEventSubject);

        subscription = wedgeScannerPlugin.startScanning().subscribe( s => scanResults.push(s));
    }

    beforeEach( () => {
        scanResults = [];
        keyDownEvents = [];
        TestBed.resetTestingModule();
        if (subscription) {
            subscription.unsubscribe();
        }
    });

    it('should replace special character codes with the correct special character', () => {

        config.startSequence = '*';
        config.endSequence = 'Enter';

        queueEvent( '*', false, false );
        queueEvent( 'X', false, false );
        queueEvent( '1', false, false );
        queueEvent( '2', false, false );
        queueEvent( '3', false, false );
        queueEvent( '4', false, false );
        queueEvent( 'A', false, false );
        queueEvent( 'B', false, false );
        queueEvent( '1', false, true);
        queueEvent( '0', false, true);
        queueEvent( '3', false, true);
        queueEvent( '0', false, true);
        queueEvent( 'Enter', false, false );

        setupSync();

        getTestScheduler().flush();

        expect(scanResults[0].type).toEqual('X');
        expect(scanResults[0].data).toEqual('1234AB' + String.fromCharCode(10) + String.fromCharCode(30));
    });

    it('should buffer barcode between * and Enter', () => {

        config.startSequence = '*';
        config.endSequence = 'Enter';

        queueEvent( '*', false, false );
        queueEvent( 'X', false, false );
        queueEvent( '1', false, false );
        queueEvent( '2', false, false );
        queueEvent( '3', false, false );
        queueEvent( '4', false, false );
        queueEvent( 'A', false, false );
        queueEvent( 'B', false, false );
        queueEvent( 'Enter', false, false );

        setupSync();

        getTestScheduler().flush();

        expect(scanResults[0].type).toEqual('X');
        expect(scanResults[0].data).toEqual('1234AB');
    });

    it('should buffer barcode between ctrl+b and ctrl+j', () => {

        config.startSequence = 'ctrl+b';
        config.endSequence = 'ctrl+j';

        queueEvent( 'b', true, false );
        queueEvent( 'X', false, false );
        queueEvent( '1', false, false );
        queueEvent( '2', false, false );
        queueEvent( '3', false, false );
        queueEvent( '4', false, false );
        queueEvent( 'Z', false, false );
        queueEvent( 'B', false, false );
        queueEvent( 'j', true, false );

        setupSync();

        getTestScheduler().flush();

        expect(scanResults[0].type).toEqual('X');
        expect(scanResults[0].data).toEqual('1234ZB');
    });

    it('should timeout and only send second scan', fakeAsync(() => {
        config.startSequence = 'ctrl+b';
        config.endSequence = 'ctrl+j';

        setupAsync();

        getTestScheduler().flush();

        dispatchEvent( 'b', true, false );
        dispatchEvent( 'T', false, false );
        dispatchEvent( 'K', false, false );
        dispatchEvent( 'L', false, false );
        dispatchEvent( 'B', false, false );
        dispatchEvent( 'R', false, false );

        tick(500);

        expect(scanResults.length).toBe(0);

        dispatchEvent( 'b', true, false );
        dispatchEvent( 'X', false, false );
        dispatchEvent( '1', false, false );
        dispatchEvent( '2', false, false );
        dispatchEvent( '3', false, false );
        dispatchEvent( '4', false, false );
        dispatchEvent( 'Z', false, false );
        dispatchEvent( 'B', false, false );
        dispatchEvent( 'j', true, false );

        expect(scanResults[0].type).toEqual('X');
        expect(scanResults[0].data).toEqual('1234ZB');
    }));

});
