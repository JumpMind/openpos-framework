import { IAbstractScreenTemplate } from "./iAbstractScreenTemplate";

export interface IAbstractScreen {
    name: string;
    type: string;
    template: IAbstractScreenTemplate;
    locale: string; 
}