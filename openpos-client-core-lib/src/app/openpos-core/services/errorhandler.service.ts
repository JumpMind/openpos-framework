import {ErrorHandler, Injectable} from '@angular/core';

@Injectable()
export class ErrorHandlerService extends ErrorHandler {
  constructor() {
    super();
  }

  handleError(error) {
    if (! this.isRunningInBrowser()) {
        console.group(">>>>> [OpenPOS] Exception <<<<<");
        console.error(error);
        console.error(error.message);
        console.error('-------------------- STACK TRACE (start) --------------------');
        try { console.error(error.stack.replace(/(.*@|file:\/\/).*(\/www\/.*)/g, "$1$2"));} catch(error) {}
        console.error('-------------------- STACK TRACE (end) ----------------------');
        console.groupEnd();
    }
    super.handleError(error);
  }

  public isRunningInBrowser(): boolean {
    const app = document.URL.indexOf('http://') === -1 && document.URL.indexOf('https://') === -1;
    return !app;
  }

}