import { IItem } from './item.interface';

export interface ITenderItem extends IItem {
    type: string;
    number: number;
    typeName: string;
    text: string;
    icon: string;
    cardLastFourDigits: string;
}
