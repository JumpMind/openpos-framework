import { CordovaPlugin } from '../common/cordova-plugin';

export class InAppBrowserPlugin extends CordovaPlugin {

    ref: any;

    constructor() {
        super('InAppBrowser');
        // Override Window.open with our own method so that
        // we can grab the reference to the Window upon open
        // and use it for showing and hiding the window
        window.open = (url, target, options?: any) => {
            return this.open(url, target, options);
        };
    }

    open(url, target, options?: any): Window {
        this.ref = this.impl.open(url, target, options);
        return this.ref;
    }

    show(): void {
        this.ref.show();
    }

    hide(): void {
        this.ref.hide();
    }

    init(successCallback: () => void, errorCallback: (error?: string) => void): void {
        super.init(
            () => {
                successCallback();
            },
            (error) => {
                errorCallback(error);
            }
        );
    }
}
