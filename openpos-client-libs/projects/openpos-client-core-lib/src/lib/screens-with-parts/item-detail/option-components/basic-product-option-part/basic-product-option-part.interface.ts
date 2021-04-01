import {IActionItem} from '../../../../core/actions/action-item.interface';

export interface BasicProductOptionPartInterface {
    optionName: string;
    optionPlaceholder: string;
    options: Map<string, string>;
    selectOptionAction: IActionItem;
    selectedOption: string;
}