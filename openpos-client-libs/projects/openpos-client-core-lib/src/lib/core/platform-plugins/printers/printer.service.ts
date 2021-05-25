import { Inject, Injectable, InjectionToken, Optional } from '@angular/core';
import { ActionService } from '../../actions/action.service';
import { Observable } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { MessageTypes } from '../../messages/message-types';
import { PrintMessage } from '../../messages/print-message';
import { SessionService } from '../../services/session.service';
import { BrowserPrinterPlugin } from './browser-printer.plugin';
import { IPrinter } from './printer.interface';

export const PRINTERS = new InjectionToken<IPrinter[]>('Printers');

@Injectable()
export class PrinterService {
    private readonly _selectedPrinter: IPrinter;

    constructor(
        actionService: ActionService,
        @Optional() @Inject(PRINTERS) private printers: Array<IPrinter>, 
        sessionService: SessionService,

        // browser printer will be used as fallback if no other printers are avaialable.
        browserPrinter: BrowserPrinterPlugin
    ) {
        this._selectedPrinter = browserPrinter;

        if (printers) {
            const supportedPrinters = printers.filter(p => p.isSupported());
            if (supportedPrinters.length > 0) {
                this._selectedPrinter = supportedPrinters[0];
            }
        }

        console.log(`using '${this._selectedPrinter.name()}' printer`);

        sessionService.getMessages(MessageTypes.PRINT).pipe(
            switchMap(m => {
                console.log('printing a doc...')
                return this.print((m as PrintMessage).html)
            }),
            switchMap(() => {
                console.log('requesting next page....');
                return actionService.doAction({ action: 'PrintNextDocument', doNotBlockForResponse: true })
            })
        ).subscribe();
    }

    print(html: string): Observable<void> {
        return this._selectedPrinter.print(html);
    }
}
