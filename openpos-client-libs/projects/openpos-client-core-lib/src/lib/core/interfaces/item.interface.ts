import { IField } from './field.interface';
export interface IItem {
    index: number;
    id: string;
    description: string;
    subtitle: string;
    amount: string;
    fields: IField[];
    selected: boolean;
    enabled: boolean;
}
