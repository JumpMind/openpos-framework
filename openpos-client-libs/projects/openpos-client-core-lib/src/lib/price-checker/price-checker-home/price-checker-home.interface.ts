import { IAbstractScreen } from '../../core/interfaces/abstract-screen.interface';
import { IActionItem } from '../../core/interfaces/action-item.interface';


export interface PriceCheckerHomeInterface extends IAbstractScreen {
    backgroundImageUrl: string;
    scanAction: IActionItem;
    message: string;
}
