import {IActionItem} from '../../core/actions/action-item.interface';
import { IAbstractScreen } from '../../core/interfaces/abstract-screen.interface';
import { DisplayProperty } from '../../shared/components/display-property/display-property.interface';
import {BuddyStoreInterface} from './buddy-store.interface';
import {ProductOptionInterface} from './product-option.interface';
import {IPromotionInterface} from './promotion.interface';

export interface ItemDetailInterface extends IAbstractScreen {
    imageUrls: string[];
    alternateImageUrl: string;
    itemName: string;
    itemValueDisplay: DisplayProperty;
    summary: string;
    itemProperties: DisplayProperty[];
    itemActions: IActionItem[];
    itemPromotionsTitle: string;
    itemNoPromotionsTitle: string;
    promotions: IPromotionInterface[];
    promotionStackingDisclaimer: string;
    
    productOptionsComponents: ProductOptionInterface[];
    onHandLabel: string;
    onHandCount: string;
    buddyStoreOffline: boolean;
    buddyStoreTitle: string;
    buddyStoreIcon: string;
    buddyStoreOfflineTitle: string;
    buddyStores: BuddyStoreInterface[];
}
