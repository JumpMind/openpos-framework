import {ErrorHandler, Injectable} from '@angular/core';
import { Logger } from './logger.service';

@Injectable({
    providedIn: 'root',
  })
export class ErrorHandlerService extends ErrorHandler {

  constructor(private log: Logger) {
    super();
  }

  handleError(error) {
        this.log.error(error.message);
        try { this.log.error(error.stack.replace(/(.*@|file:\/\/).*(\/www\/.*)/g, '$1$2')); } catch (error) {}
        super.handleError(error);
  }


}
