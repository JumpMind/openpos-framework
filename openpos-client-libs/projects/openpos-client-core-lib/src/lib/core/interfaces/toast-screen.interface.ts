import { IAbstractScreen } from './abstract-screen.interface';

export interface IToastScreen extends IAbstractScreen {
    message: string;
    toastType: ToastType;
    duration: number;
    verticalPosition: string;
}

export enum ToastType {
    Success= 'Success',
    Warn= 'Warn'
}
