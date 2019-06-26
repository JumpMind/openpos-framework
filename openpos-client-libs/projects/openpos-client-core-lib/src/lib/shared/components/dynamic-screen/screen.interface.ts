import { AbstractTemplate } from '../../../core/components/abstract-template';


export interface IScreen {
    show(screen: any, template?: AbstractTemplate<any>): void;
}
