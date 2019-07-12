import { Component, ViewChild } from '@angular/core';
import {  MatPaginator, PageEvent } from '@angular/material';
import { PosScreen } from '../../screens-with-parts/pos-screen.component';
import { IItemQuantityFormElement } from './iitem-quantity-form-field.interface';
import { FormGroup, FormBuilder } from '@angular/forms';
import { ISellItem } from '../../core/interfaces/sell-item.interface';
import { IActionItem } from '../../core/interfaces/action-item.interface';
import { IForm } from '../../core/interfaces/form.interface';
import { ICatalogBrowserForm } from '../../core/interfaces/catalog-browser-form.interface';


/**
 * @ignore
 */
@Component({
    selector: 'app-catalog-browser',
    templateUrl: './catalog-browser.component.html',
    styleUrls: ['./catalog-browser.component.scss']
  })
  export class CatalogBrowserComponent extends PosScreen<any> {
    @ViewChild('drawer') drawer;
    @ViewChild(MatPaginator) paginator;

    items: ISellItem[];
    categories: IActionItem[];
    maxItemsPerPage: number;
    totalItems: number;
    selectedItemQuantity: IItemQuantityFormElement;
    form: IForm;
    formGroup: FormGroup;

    constructor(private formBuilder: FormBuilder) {
        super();
    }

    buildScreen() {
        this.items = this.screen.items;
        this.categories = this.screen.categories;
        this.maxItemsPerPage = this.screen.maxItemsPerPage;
        this.totalItems = this.screen.itemTotalCount;
        this.form = this.screen.form;
        this.selectedItemQuantity = <IItemQuantityFormElement> this.form.formElements.find(e => e.id === 'selectedItemQuantity');
        this.formGroup = this.formBuilder.group(this.form);
    }

    public onItemSelected(item: ISellItem) {
        if (this.formGroup.valid) {
            const returnForm: ICatalogBrowserForm = {selectedItems: [item], form: this.form};
            this.session.onAction('ItemSelected', returnForm);
        } else {
            this.showErrors();
        }
    }

    private showErrors() {
        // Forces redisplay of error
        this.formGroup.controls[this.selectedItemQuantity.id].markAsDirty();
    }
    public onCategorySelected(category: IActionItem, event?: any) {
        const returnForm: ICatalogBrowserForm = {selectedCategory: category, form: this.form};
        this.session.onAction(category.action, returnForm);
    }

    public onPageEvent(event?: PageEvent) {
        const returnForm: ICatalogBrowserForm = {pageEvent: event, form: this.form};
        this.session.onAction('PageEvent', returnForm);
    }

    onItemQuantityChange(value: string) {
        const newValue = this.formGroup.controls[this.selectedItemQuantity.id].value;
        this.selectedItemQuantity.value = newValue;
    }
  }
