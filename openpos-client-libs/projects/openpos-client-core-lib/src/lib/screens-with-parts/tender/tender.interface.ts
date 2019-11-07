import { IAbstractScreen } from '../../core/interfaces/abstract-screen.interface';
import { IForm } from '../../core/interfaces/form.interface';
import { ITotal } from '../../core/interfaces/total.interface';
import { OptionsListInterface } from '../../shared/screen-parts/options-list/options-list.interface';

export interface TenderScreenInterface extends IAbstractScreen {
    form: IForm;
    title: string;
    prompt: string;
    amountDue: ITotal;
    amounts: ITotal[];
    imageUrl: string;
    optionsList: OptionsListInterface;
}
