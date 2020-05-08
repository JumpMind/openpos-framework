import { MessageTypes } from './message-types';

export class SplashScreen {
    type = MessageTypes.SCREEN;
    screenType = 'SplashScreen';
    message: string;

    constructor(message?: string) {
        this.message = message;
    }
}
