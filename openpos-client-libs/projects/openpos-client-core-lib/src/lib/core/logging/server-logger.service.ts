import { ILogger } from './logger.interface';
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable()
export class ServerLogger implements ILogger {
    constructor( public http: HttpClient ) {}
    log() {
        throw new Error("Method not implemented.");
    }
    info() {
        throw new Error("Method not implemented.");
    }
    error() {
        throw new Error("Method not implemented.");
    }
    warn() {
        throw new Error("Method not implemented.");
    }
    debug() {
        throw new Error("Method not implemented.");
    }


}
