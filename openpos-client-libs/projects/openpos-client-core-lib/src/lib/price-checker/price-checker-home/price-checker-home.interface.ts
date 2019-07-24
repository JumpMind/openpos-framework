import { IAbstractScreen } from '../../core/interfaces/abstract-screen.interface';
import { IActionItem } from '../../core/actions/action-item.interface';


export interface PriceCheckerHomeInterface extends IAbstractScreen {
    backgroundImageUrl: string;
    scanAction: IActionItem;
    message: string;
}
