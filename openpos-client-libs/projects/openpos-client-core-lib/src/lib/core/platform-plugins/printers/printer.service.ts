import { Inject, Injectable, InjectionToken, Optional } from '@angular/core';
import { Observable } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { ActionMessage } from '../../messages/action-message';
import { MessageTypes } from '../../messages/message-types';
import { PrintMessage } from '../../messages/print-message';
import { SessionService } from '../../services/session.service';
import { BrowserPrinterPlugin } from './browser-printer.plugin';
import { IPrinter } from './printer.interface';

export const PRINTERS = new InjectionToken<IPrinter[]>('Printers');

@Injectable({
    providedIn: 'root'
})
export class PrinterService {
    private readonly _selectedPrinter: IPrinter;

    constructor(
        @Optional() @Inject(PRINTERS) printers: Array<IPrinter>, 
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
            switchMap(m => this.print((m as PrintMessage).html))
        ).subscribe(() => {
            sessionService.sendMessage(new ActionMessage('PrintNextDocument', true));
        });
    }

    print(html: string): Observable<void> {
        return this._selectedPrinter.print(html);
    }
}
