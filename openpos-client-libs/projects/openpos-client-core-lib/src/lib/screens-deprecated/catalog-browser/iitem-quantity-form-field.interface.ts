import { IFormElement } from '../../core/interfaces/form-field.interface';

/**
 * @ignore
 */
export interface IItemQuantityFormElement extends IFormElement {
    min: number;
    max: number;
}
