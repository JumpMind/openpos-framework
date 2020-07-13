import {Inject, Injectable, InjectionToken, Optional} from '@angular/core';
import {MessageTypes} from '../../messages/message-types';
import {PrintMessage} from '../../messages/print-message';
import {SessionService} from '../../services/session.service';
import {IPrinter} from './printer.interface';

export const PRINTERS = new InjectionToken<IPrinter[]>('Printers');

@Injectable({providedIn: 'root'})
export class PrinterService{
    constructor( @Optional() @Inject(PRINTERS) private printers: Array<IPrinter>, sessionService: SessionService) {
        sessionService.getMessages(MessageTypes.PRINT).subscribe( m => this.print((m as PrintMessage).printerId, (m as PrintMessage).html));
    }

    print( printerId: string, html: string){
        const printer = this.printers.filter( p => p.id === printerId);
        if(printer.length > 0){
            printer[0].print(html);
        }
    }

}