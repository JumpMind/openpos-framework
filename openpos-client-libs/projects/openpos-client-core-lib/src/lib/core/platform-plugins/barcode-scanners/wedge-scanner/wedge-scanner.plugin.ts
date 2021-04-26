import { Injectable } from '@angular/core';
import { Observable, of} from 'rxjs';
import { map, filter, bufferToggle, timeout, catchError, windowToggle, tap, mergeAll, publish, refCount} from 'rxjs/operators';
import { SessionService } from '../../../services/session.service';
import { WEDGE_SCANNER_ACCEPTED_KEYS } from './wedge-scanner-accepted-keys';
import { DomEventManager } from '../../../services/dom-event-manager.service';
import { Scanner, ScanData, ScanDataType} from '../scanner';

interface ControlSequence { modifiers: string[]; key: string; }

@Injectable({
    providedIn: 'root'
  })
  export class WedgeScannerPlugin implements Scanner {
    private startSequence = '*';
    private endSequence = 'Enter';
    private codeTypeLength = 0;
    private timeout = 500;
    private typeMap: Map<string, ScanDataType>;
    private scannerActive: boolean;
    private startSequenceObj = this.getControlStrings(this.startSequence);
    private endSequenceObj = this.getControlStrings(this.endSequence);

    private readonly scanObservable = new Observable(observer => {
        this.scannerActive = true;

        const subscription = this.createScanBuffer().subscribe({
            next: d => observer.next(d),
        });

        return () => {
            subscription.unsubscribe();
            this.scannerActive = false;
        };
    }).pipe(
        publish(),
        refCount()
    );

    constructor( sessionService: SessionService, private domEventManager: DomEventManager ) {

        sessionService.getMessages('ConfigChanged').pipe(
            filter( m => m.configType === 'WedgeScanner')
        ).subscribe( m => {
            if ( m.startSequence ) {
                this.startSequence = m.startSequence;
                this.startSequenceObj = this.getControlStrings(this.startSequence);
            }
            if ( m.endSequence ) {
                this.endSequence = m.endSequence;
                this.endSequenceObj = this.getControlStrings(this.endSequence);
            }
            if ( m.codeTypeLength ) {
                this.codeTypeLength = m.codeTypeLength;
            }
            if ( m.acceptKeys ) {
                m.acceptKeys.forEach(key => {
                    WEDGE_SCANNER_ACCEPTED_KEYS.push(key);
                });
            }
            if ( m.timeout ) {
                this.timeout = m.timeout;
            }
        });

        sessionService.getMessages('ConfigChanged').pipe(
            filter(m => m.configType === 'WedgeScannerTypes')
        ).subscribe(m => {
            this.typeMap = new Map<string, ScanDataType>();
            Object.getOwnPropertyNames(m).forEach(element => {
                this.typeMap.set(element, m[element]);
            });
        });
    }

    beginScanning(): Observable<ScanData> {
        return this.scanObservable;
    }

    private createScanBuffer(): Observable<ScanData> {
        const startScanBuffer = this.domEventManager.createEventObserver(window, 'keydown', {capture: true}).pipe(
            filter( (e: KeyboardEvent) => this.filterForControlSequence(this.startSequenceObj, e),
            tap( e => console.debug(`Starting Scan Capture: ${e}`))));

        const stopScanBuffer = this.domEventManager.createEventObserver(window, 'keydown', {capture: true}).pipe(
            timeout(this.timeout),
            filter((e: KeyboardEvent) => this.filterForControlSequence(this.endSequenceObj, e)),
            tap( e => console.debug(`Stopping Scan Capture: ${e}`)),
            catchError( e => {
                console.debug('Scan Capture timed out');
                return of('timed out');
            }),
        );

        // This subscription will block keyboard events during a scan
        this.domEventManager.createEventObserver(window, ['keypress', 'keyup', 'keydown'], {capture: true}).pipe(
            windowToggle(startScanBuffer, () => stopScanBuffer),
            mergeAll()
        ).subscribe( (e: KeyboardEvent) => {
            if ( e.type === 'keydown') {
                e.stopPropagation();
            } else {
                e.stopImmediatePropagation();
            }
            e.preventDefault();
        });

        // buffer up all keydown events and prevent them from propagating while scanning
        return this.domEventManager.createEventObserver(window, 'keydown', {capture: true}).pipe(
            bufferToggle(
                startScanBuffer,
                () => stopScanBuffer
            ),
            // We need to filter out any incomplete scans
            filter( (events: KeyboardEvent[]) => this.filterForControlSequence(this.endSequenceObj, events[events.length - 1])),
            tap( events => console.debug(`Complete Scan: ${events.map(e => e.key).join(', ')}`)),
            map( (events: KeyboardEvent[]) => this.convertKeyEventsToChars(events) ),
            // Join the buffer into a string and remove the start and stop characters
            map( (s) => s.join('')),
            map( (s: string) => this.getScanData(s))
        );
    }

    private getControlStrings( sequence: string): ControlSequence {
        let modifiers: string[];
        let key: string;

        if ( sequence.includes('+')) {
            modifiers = sequence.split('+');
            modifiers = modifiers.slice(0, modifiers.length - 1 );
            key = sequence.slice(sequence.lastIndexOf('+') + 1)[0];
        } else {
            key = sequence;
        }

        return { modifiers, key};
    }

    private filterForControlSequence( sequence: ControlSequence, e: KeyboardEvent ): boolean {
        if ( !this.scannerActive ) {
            return false;
        }
        const keyPressed = e.key === sequence.key;
        console.debug(`Start/Stop key (${e.key}) pressed: ${keyPressed} `);
        if ( !!sequence.modifiers ) {
            const modifiersPressed = sequence.modifiers.map( m => this.checkModifier(e, m)).reduce((accum, m) => accum && m );
            console.debug(`Start/Stop Modifiers (${sequence.modifiers.join(', ')}) pressed: ${modifiersPressed}`);
            return modifiersPressed && keyPressed;
        }
        return keyPressed;
    }

    private checkModifier( e: KeyboardEvent, modifier: string): boolean {
        switch (modifier) {
            case 'ctrl':
                return e.ctrlKey;
            case 'alt':
                return e.altKey;
        }
    }

    private convertKeyEventsToChars( events: KeyboardEvent[] ): string[] {
        // We need to look for 4 character sequences with the alt key pressed and convert them into the
        // special characters they represent
        // We also want to filter out keys that are not in our list of accepted keys
        const charList = [];
        for ( let i = 0; i < events.length; i++ ) {
            const e = events[i];

            // if the first key is the start key skip it
            if ( i === 0 && this.filterForControlSequence( this.startSequenceObj, events[i])) {
                continue;
            }

            // if the first key is the end key skip it
            if ( i === events.length - 1 && this.filterForControlSequence( this.endSequenceObj, events[i])) {
                continue;
            }

            if ( e.altKey && i < events.length - 1 ) {
                // get the next number
                const e1 = events[i + 1]
                const e2 = events[i + 2];
                const e3 = events[i + 3];
                const e4 = events[i + 4];

                // convert the char code into a string
                charList.push(String.fromCharCode( parseInt(e1.key + e2.key + e3.key + e4.key, 10) ));

                // skip the next value since we already accounted for it.
                i+=4;
            } else if ( WEDGE_SCANNER_ACCEPTED_KEYS.includes(e.key)) {
                charList.push(e.key);
            }
        }

        return charList;
    }

    private getScanData( s: string ): ScanData {
        let type = s.slice(0, this.codeTypeLength);
        const scanData: ScanData = {data: s.slice(this.codeTypeLength)};
        if ( !!this.typeMap && this.typeMap.has(type) ) {
            scanData.type = this.typeMap.get(type);
        }
        if (!!type && type !== '') {
           scanData.rawType = type;
        }
        return scanData;
    }
}
