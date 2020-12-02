import {IActionItem} from '../../core/actions/action-item.interface';
import { IAbstractScreen } from '../../core/interfaces/abstract-screen.interface';
import { DisplayProperty } from '../../shared/components/display-property/display-property.interface';
import {IPromotionInterface} from './promotion.interface';

export interface ItemDetailInterface extends IAbstractScreen {
    imageUrls: string[];
    alternateImageUrl: string;
    itemName: string;
    summary: string;
    itemProperties: DisplayProperty[];
    itemActions: IActionItem[];
    itemPromotionsTitle: string;
    itemNoPromotionsTitle: string;
    promotions: IPromotionInterface[];
    promotionStackingDisclaimer: string;
}
