import { IMenuItem } from './imenuitem';
import { IItem } from './iitem';

export interface ISellItem extends IItem {
    posItemId: string;
    quantity: number;
    imageUrl: string;
    productDescription: string;
    menuItems: IMenuItem[];
    sellingPrice: string;
    color: string;
    size: string;
}
