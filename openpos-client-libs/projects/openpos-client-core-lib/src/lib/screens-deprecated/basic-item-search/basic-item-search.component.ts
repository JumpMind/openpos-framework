import { Component, AfterViewInit, OnDestroy, ViewChild } from '@angular/core';
import { MatInput } from '@angular/material';
import { PosScreen } from '../../screens-with-parts/pos-screen.component';
import { IForm } from '../../core/interfaces/form.interface';
import { ScreenComponent } from '../../shared/decorators/screen-component.decorator';

/**
 * @ignore
 */
@ScreenComponent({
  name: 'BasicItemSearch'
})
@Component({
  selector: 'app-basic-item-search',
  templateUrl: './basic-item-search.component.html'
})
export class BasicItemSearchComponent extends PosScreen<any> implements AfterViewInit, OnDestroy {

  @ViewChild(MatInput) firstInput: MatInput;

  searchCategories: ISearchCategory[];
  searchCategoryStructure: SearchCategoryStructure;
  searchCategoryValues: ISearchCategoryValue[];
  searchFieldForm: IForm;
  searchCategoriesText: string;
  public displayCategoryIndex = 0;

  constructor() {
      super();
  }

  buildScreen() {
    if (this.screen.template && this.screen.template.localMenuItems && this.screen.template.localMenuItems[0]) {
        this.session.registerActionPayload(this.screen.template.localMenuItems[0].action, () => {
            return this.getSearchPayload();
          });
    }

    this.searchCategories = this.screen.searchCategories;
    this.searchCategoryStructure = this.screen.searchCategoryStructure;
    this.searchCategoryValues = this.screen.searchCategoryValues;
    this.searchFieldForm = this.screen.searchFieldForm;
    this.searchCategoriesText = this.screen.searchCategoriesText;

    this.refreshContent();
  }

  ngOnDestroy(): void {
    this.session.unregisterActionPayloads();
  }

  ngAfterViewInit(): void {
    setTimeout( () => this.firstInput.focus(), 0);
  }

  onValueSelected(value: ISearchCategoryValue, categoryName: string): void {
    value.selected = true;
    this.session.onAction(`on${categoryName}Selected`, {
        'selectedCategoryValue': value,
        'searchFieldForm': this.searchFieldForm
      });
  }

  getSearchPayload() {
    return {
      'searchCategories': this.searchCategories,
      'searchCategoryStructure': this.searchCategoryStructure,
      'searchCategoryValues': this.searchCategoryValues,
      'searchFieldForm': this.searchFieldForm
    };
  }

  onSubmitAction(submitAction: string): void {
    // Collect the field values
    this.session.onAction(submitAction, this.getSearchPayload());
  }

  protected refreshContent(): void {
    // Loop over each category to determine if the values need updated
    // Screen changed, re-init
    for (let catIdx = 0; catIdx < this.screen.searchCategoryValues.length; catIdx++) {
      const srcSearchCategoryValues = this.screen.searchCategoryValues[catIdx];
      for (let valueIdx = 0; valueIdx < srcSearchCategoryValues.values.length; valueIdx++) {
        const targetValue = srcSearchCategoryValues.values[valueIdx];
        if (targetValue.selected && targetValue.attributes['name'] !== '<All>') {
          this.displayCategoryIndex = catIdx + 1;
          this.log.info('setting display to ' + (catIdx + 1) + ' because ' + targetValue.attributes['name'] + ' was selected');
        }
      }
    }
  }
}


export interface ISearchCategory {
  attributes: Map<string, any>;
  searchCategoryType: SearchCategoryType;

}

export interface ISearchCategoryValue {
  attributes: Map<string, any>;
  values: ISearchCategoryValue[];
  selected: boolean;
  index: number;
}

export enum SearchCategoryType {
  ROOT,
  SUBCATEGORY
}

export enum SearchCategoryStructure {
  FLAT,
  HIERARCHICAL
}
