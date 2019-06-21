import { IItem } from './item.interface';

export interface ITenderItem extends IItem {
    type: string;
    number: number;
}
