import { IAbstractScreen } from '../../core/interfaces/abstract-screen.interface';
import { IDataTableRow } from './data-table-row.interface';
import { IActionItem } from '../../core/actions/action-item.interface';

export interface DataTableInterface extends IAbstractScreen {
    instructions: string;
    columnHeaders: string[];
    rows: IDataTableRow[];
    actionButtons: IActionItem[];
}
