import { AbstractTemplate } from '../abstract-template';


export interface IScreen {
    show(screen: any, template?: AbstractTemplate<any>): void;
}
