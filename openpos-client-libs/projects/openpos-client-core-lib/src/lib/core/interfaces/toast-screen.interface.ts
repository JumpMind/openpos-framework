import { IAbstractScreen } from './abstract-screen.interface';

export interface IToastScreen extends IAbstractScreen {
    message: string;
    toastType: ToastType;
    duration: number;
    verticalPosition: string;
    icon?: string;
    persistent?: boolean;
    persistedId?: string;
    close?: boolean;
}

export enum ToastType {
    Success= 'Success',
    Warn= 'Warn',
    Info= 'Info'
}
