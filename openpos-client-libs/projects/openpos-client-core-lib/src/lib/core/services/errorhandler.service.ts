import {ErrorHandler, Injectable} from '@angular/core';

@Injectable({
    providedIn: 'root',
  })
export class ErrorHandlerService extends ErrorHandler {

  handleError(error) {
        console.error(error.message);
        try { console.error(error.stack.replace(/(.*@|file:\/\/).*(\/www\/.*)/g, '$1$2')); } catch (error) {}
        super.handleError(error);
  }


}
