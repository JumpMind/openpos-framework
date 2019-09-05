import { CordovaPlugin } from './cordova-plugin';


export class InAppBrowserPlugin extends CordovaPlugin {

    ref: any;
    private _active = false;

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
        if (this.ref) {
            this._active = true;
        }
        this.ref.addEventListener('exit', () => {
            this._active = false;
            this.log.info('InAppBrowser exited');
        });
        return this.ref;
    }

    isActive(): boolean {
        this.log.info(`InAppBrowser active? ${this._active}`);
        return this._active;
    }

    show(): void {
        this.ref.show();
        this._active = true;
    }

    hide(): void {
        this.ref.hide();
        this._active = false;
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
