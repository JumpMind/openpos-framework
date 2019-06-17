import { PageEvent } from '@angular/material';
import { IForm } from './form.interface';
import { IActionItem } from './action-item.interface';
import { ISellItem } from './sell-item.interface';

export interface ICatalogBrowserForm {
    form: IForm;
    pageEvent?: PageEvent;
    selectedCategory?: IActionItem;
    selectedItems?: ISellItem[];
}
