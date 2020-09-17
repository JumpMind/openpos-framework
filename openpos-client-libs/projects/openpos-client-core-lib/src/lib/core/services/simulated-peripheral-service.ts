import {Injectable} from "@angular/core";
import {SessionService} from "./session.service";
import {MessageTypes} from "../messages/message-types";
import {SimulatedPeripheralMessage} from "../messages/simulated-peripheral-message";
import {Observable, ReplaySubject} from "rxjs";
import {PeripheralTypeEnum} from "../messages/peripheral-type.enum";
import {IMessageHandler} from "../interfaces/message-handler.interface";

@Injectable({
    providedIn: 'root',
})
export class SimulatedPeripheralService implements IMessageHandler<SimulatedPeripheralMessage> {

    private eventHandlers = new Map([
       [PeripheralTypeEnum.RECEIPT, this.handleReceipt]
    ]);

    private latestReceipt$ = new ReplaySubject<string>();

    constructor(sessionService: SessionService) {
        sessionService.registerMessageHandler(this, MessageTypes.SIMULATED_PERIPHERAL);
    }

    handle(message: SimulatedPeripheralMessage) {
        this.eventHandlers.get(message.peripheralType).apply(this, [message]);
    }

    private handleReceipt(simulatedPeripheralMessage: SimulatedPeripheralMessage) : void {
        this.latestReceipt$.next(simulatedPeripheralMessage.data);
    }

    public getReceiptData() : Observable<string> {
        return this.latestReceipt$;
    }
}