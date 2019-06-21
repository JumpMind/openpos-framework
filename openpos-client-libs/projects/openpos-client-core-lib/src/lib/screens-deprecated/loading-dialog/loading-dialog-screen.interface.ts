import { IAbstractScreen } from '../../core/interfaces/abstract-screen.interface';

/**
 * @ignore
 */
export interface ILoadingDialogScreen extends IAbstractScreen {
    title: string;
    message: string;
    dismissable: boolean;
    dismissAction: string;
}
