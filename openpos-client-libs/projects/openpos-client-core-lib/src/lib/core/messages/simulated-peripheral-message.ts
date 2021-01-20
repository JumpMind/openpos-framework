import {OpenposMessage} from './message';
import {MessageTypes} from './message-types';
import {PeripheralTypeEnum} from "./peripheral-type.enum";

export class SimulatedPeripheralMessage implements OpenposMessage {
    type = MessageTypes.SIMULATED_PERIPHERAL;
    peripheralType: PeripheralTypeEnum;
    data: any;
}