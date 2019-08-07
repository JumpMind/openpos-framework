import { IAbstractScreen } from '../../core/interfaces/abstract-screen.interface';
import { IForm } from '../../core/interfaces/form.interface';
import { DisplayProperty } from '../../shared/components/display-property/display-property.interface';
import { TenderItem } from './tender-item';
import { IActionItem } from '../../core/interfaces/action-item.interface';

export interface TenderScreenInterface extends IAbstractScreen {
    instructions: string;
    form: IForm;
    balanceDue: DisplayProperty;
    tenderTypeActionNames: string[];
    completedTenderListLabel: string;
    tenderItems: TenderItem[];
    tenderItemActions: IActionItem[];
    noCompletedTendersMessage: string;
    noCompletedTendersIcon: string;
}
