import { LogMethodType } from './log-method-type.enum';

export class ServerLogEntry {
    constructor( public type: LogMethodType, public timestamp: number, public message: string ) {}

}
