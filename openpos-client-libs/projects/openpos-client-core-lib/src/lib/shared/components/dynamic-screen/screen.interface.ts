import { AbstractTemplate } from '../../../core/components/abstract-template';
import { Injector } from '@angular/core';


export interface IScreen {
    show(screen: any, template?: AbstractTemplate<any>): void;
}
