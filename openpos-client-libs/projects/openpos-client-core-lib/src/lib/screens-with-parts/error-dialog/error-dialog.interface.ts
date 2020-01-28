import { IAbstractScreen } from '../../core/interfaces/abstract-screen.interface';
import {IActionItem} from "../../core/actions/action-item.interface";



export interface ErrorDialogInterface extends IAbstractScreen {
    title: string;
    message: string;
    button: IActionItem;
    imageUrl: string;
    altImageUrl: string;
}
