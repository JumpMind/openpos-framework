import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HttpModule } from '@angular/http';
import { FlexLayoutModule } from '@angular/flex-layout';
import { HttpClientModule } from '@angular/common/http';
import { TextMaskModule } from 'angular2-text-mask';
import { MaterialModule } from '../material/material.module';
import { MatKeyboardModule } from '../keyboard/keyboard.module';

import { SafePipe } from './pipes/safe.pipe';
import { DynamicListControlComponent } from './components/dynamic-list-control/dynamic-list-control.component';
import { KeyPressSourceDirective } from './directives/keypress-source.directive';
import { SystemStatusDialogComponent } from './components/system-status/system-status-dialog.component';
import { SaleFooterComponent } from '../screens-with-parts/sale/sale-footer/sale-footer.component';
import { SaleItemListComponent } from '../screens-with-parts/sale/sale-item-list/sale-item-list.component';
import { KebabMenuComponent } from './components/kebab-menu/kebab-menu.component';
import { MarkDirtyOnSubmitDirective } from './directives/mark-dirty-on-submit.directive';
import { InputFormatterDirective } from './directives/input-formatter.directive';
import { ScreenOrientationDirective } from './directives/screen-orientation.directive';
import { MimicScrollDirective } from './directives/mimic-scroll.directive';
import { MatExclusiveSelectionListDirective } from './directives/mat-exclusive-selection-list.directive';
import { InactivityMonitorDirective } from './directives/inactivity-monitor.directive';
import { BarcodeScanPublisherDirective } from './directives/barcode-scan-publisher.directive';
import { AutoSelectOnFocusDirective } from './directives/auto-select-on-focus.directive';
import { RequireAtleastOneValidatorDirective } from './directives/require-at-least-one.directive';
import { PhoneUSValidatorDirective } from './directives/phone.directive';
import { OpenposScreenOutletDirective } from './directives/screen-outlet.directive';
import { ScreenDirective } from './directives/screen.directive';
import { AutocompleteDirective } from './directives/autocomplete.directive';
import { DefaultImageDirective } from './directives/default-image.directive';
import { KlassDirective } from './directives/klass.directive';
import { HideFormAccessoryBarDirective } from './directives/hide-form-accessory-bar.directive';
import { FindFloatingElementDirective } from './directives/find-floating-element.directive';
import { FixediOsScrollDirective } from './directives/fixed-ios-scroll.directive';
import { ActionItemKeyMappingDirective } from './directives/action-item-key-mapping.directive';
import { AutoCompleteAddressDirective } from './directives/auto-complete-address.directive';
import { ArrowTabDirective } from './directives/arrow-tab.directive';
import { ArrowTabItemDirective } from './directives/arrow-tab-item.directive';
import { PhonePipe } from './pipes/phone.pipe';
import { HourMinSecPipe } from './pipes/hour-min-sec.pipe';
import { LocalizedDatePipe } from './pipes/localized-date.pipe';
import { ValueFormatterPipe } from './pipes/value-formatter.pipe';
import { POSCurrencyPipe } from './pipes/pos-currency.pipe';
import { StringListFilterPipe } from './pipes/string-list-filter.pipe';
import { ListLimitPipe } from './pipes/list-limit.pipe';
import { MarkdownFormatterPipe } from './pipes/markdown-formatter.pipe';
import { ImageUrlPipe } from './pipes/image-url.pipe';
import { BackgroundImageUrlPipe } from './pipes/background-image-url.pipe';
import { PromptInputComponent } from './components/prompt-input/prompt-input.component';
import { IconComponent } from './components/icon/icon.component';
import { ShowErrorsComponent } from './components/show-errors/show-errors.component';
import { DynamicDateFormFieldComponent } from './components/dynamic-date-form-field/dynamic-date-form-field.component';
import { DynamicFormFieldComponent } from './components/dynamic-form-field/dynamic-form-field.component';
import { DynamicFormControlComponent } from './components/dynamic-form-control/dynamic-form-control.component';
import { ProductListComponent } from './components/product-list/product-list.component';
import { SelectableItemListComponent } from './components/selectable-item-list/selectable-item-list.component';
import { OverFlowListComponent } from './components/overflow-list/overflow-list.component';
import { ScanSomethingComponent } from './components/scan-something/scan-something.component';
import { FabToggleButtonComponent } from './components/fab-toggle-button/fab-toggle-button.component';
import { FabToggleGroupComponent } from './components/fab-toggle-group/fab-toggle-group.component';
import { PopTartComponent } from './components/pop-tart/pop-tart.component';
import { SearchablePopTartComponent } from './components/searchable-pop-tart/searchable-pop-tart.component';
import { NavListComponent } from './components/nav-list/nav-list.component';
import { FileViewerComponent } from './components/file-viewer/file-viewer.component';
import { CounterComponent } from './components/counter/counter.component';
import { DatePartChooserComponent } from './components/date-part-chooser/date-part-chooser.component';
import { DatePartChooserFieldComponent } from './components/date-part-chooser/date-part-chooser-field/date-part-chooser-field.component';
import { DatePartChooserDialogComponent } from './components/date-part-chooser/date-part-chooser-dialog/date-part-chooser-dialog.component';
import { PrimaryButtonComponent } from './components/primary-button/primary-button.component';
import { SecondaryButtonComponent } from './components/secondary-button/secondary-button.component';
import { TrainingDialogComponent } from './components/training-dialog/training-dialog.component';
import { TrainingWrapperComponent } from './components/training-dialog/training-wrapper.component';
import { MessageDialogComponent } from './components/message-dialog/message-dialog.component';
import { MenuComponent } from './components/menu/menu.component';
import { TimeChooserComponent } from './components/time-chooser/time-chooser.component';
import { PagerComponent } from './components/pager/pager.component';
import { CatalogBrowserItemComponent } from './components/catalog-browser-item/catalog-browser-item.component';
import { CurrencyTextComponent } from './components/currency-text/currency-text.component';
import { IconButtonComponent } from './components/icon-button/icon-button.component';
import { ReceiptCardComponent } from './components/receipt-card/receipt-card.component';
import { IconFabButtonComponent } from './components/icon-fab-button/icon-fab-button.component';
import { KebabButtonComponent } from './components/kebab-button/kebab-button.component';
import { IconSquareButtonComponent } from './components/icon-square-button/icon-square-button.component';
import { DisplayPropertyComponent } from './components/display-property/display-property.component';
import { TransactionItemListComponent } from './components/transaction-item-list/transaction-item-list.component';
import { GridTableComponent } from './components/grid-table/grid-table.component';
import { DialogHeaderComponent } from './screen-parts/dialog-header/dialog-header.component';
import { BaconStripComponent } from './screen-parts/bacon-strip/bacon-strip.component';
import { ScanOrSearchComponent } from './screen-parts/scan-or-search/scan-or-search.component';
import { SausageLinksComponent } from './screen-parts/sausage-links/sausage-links.component';
import { StatusStripComponent } from './screen-parts/status-strip/status-strip.component';
import { DynamicFormPartComponent } from './screen-parts/dynamic-form-part/dynamic-form-part.component';
import { SideNavComponent } from './layout-components/side-nav/side-nav.component';
import { WaffleComponent } from './layout-components/waffle/waffle.component';
import { CommonModule } from '@angular/common';
import { PromptFormPartComponent } from './screen-parts/prompt-form-part/prompt-form-part.component';
import { FormComponent } from './components/form/form.component';
import { AutoCompleteAddressPartComponent } from './screen-parts/auto-complete-address-part/auto-complete-address-part.component';
import { SellItemLineComponent } from './components/sell-item-line/sell-item-line.component';
import { ProgressBarPartComponent } from './screen-parts/progress-bar-part/progress-bar-part.component';
import { ToggleGroupComponent } from './components/toggle-button-group/toggle-group.component';
import { ToggleButtonComponent } from './components/toggle-button/toggle-button.component';
import { ChooseOptionsPartComponent } from './screen-parts/choose-options-part/choose-options-part.component';
import { WebcamViewerComponent } from './screen-parts/webcam-viewer/webcam-viewer.component';
import { LanguageSelectorComponent } from './components/language-selector/language-selector.component';
import { OpenposAppComponent } from './components/openpos-app/openpos-app.component';
import { LoaderComponent } from './components/loader/loader.component';
import { DevMenuComponent } from './components/dev-menu/dev-menu.component';
import { RouterModule } from '@angular/router';
import { DynamicScreenComponent } from './components/dynamic-screen/dynamic-screen.component';
import { LocationDetailsComponent } from './components/location-details/location-details.component';
import { LocationOverrideDialogComponent } from './components/location-details/location-override-dialog/location-override-dialog.component';
import { SellLinkedCustomerComponent } from './components/sell-linked-customer/sell-linked-customer.component';
import { ItemCardComponent } from './components/item-card/item-card.component';
import { SystemStatusComponent } from './screen-parts/system-status/system-status.component';

const screenParts = [
    ChooseOptionsPartComponent,
    StatusStripComponent,
    SausageLinksComponent,
    ScanOrSearchComponent,
    BaconStripComponent,
    DialogHeaderComponent,
    SaleFooterComponent,
    SaleItemListComponent,
    PromptFormPartComponent,
    DynamicFormPartComponent,
    AutoCompleteAddressPartComponent,
    ProgressBarPartComponent,
    WebcamViewerComponent,
    SystemStatusComponent
];

const components = [
    DynamicScreenComponent,
    OpenposAppComponent,
    LoaderComponent,
    DevMenuComponent,
    PromptInputComponent,
    IconComponent,
    ShowErrorsComponent,
    DynamicDateFormFieldComponent,
    DynamicFormFieldComponent,
    DynamicFormControlComponent,
    DynamicListControlComponent,
    ProductListComponent,
    SelectableItemListComponent,
    OverFlowListComponent,
    ScanSomethingComponent,
    FabToggleButtonComponent,
    FabToggleGroupComponent,
    PopTartComponent,
    SearchablePopTartComponent,
    NavListComponent,
    FileViewerComponent,
    CounterComponent,
    DatePartChooserComponent,
    DatePartChooserFieldComponent,
    DatePartChooserDialogComponent,
    PrimaryButtonComponent,
    SecondaryButtonComponent,
    SideNavComponent,
    TrainingDialogComponent,
    TrainingWrapperComponent,
    MessageDialogComponent,
    MenuComponent,
    TimeChooserComponent,
    PagerComponent,
    CatalogBrowserItemComponent,
    CurrencyTextComponent,
    WaffleComponent,
    IconButtonComponent,
    IconFabButtonComponent,
    ReceiptCardComponent,
    KebabButtonComponent,
    KebabMenuComponent,
    IconSquareButtonComponent,
    DisplayPropertyComponent,
    TransactionItemListComponent,
    SellItemLineComponent,
    GridTableComponent,
    SystemStatusDialogComponent,
    FormComponent,
    ToggleGroupComponent,
    ToggleButtonComponent,
    LanguageSelectorComponent,
    LocationDetailsComponent,
    LocationOverrideDialogComponent,
    SellLinkedCustomerComponent,
    ItemCardComponent
];

const directives = [
    MarkDirtyOnSubmitDirective,
    InputFormatterDirective,
    ScreenOrientationDirective,
    MimicScrollDirective,
    MatExclusiveSelectionListDirective,
    InactivityMonitorDirective,
    BarcodeScanPublisherDirective,
    AutoSelectOnFocusDirective,
    RequireAtleastOneValidatorDirective,
    PhoneUSValidatorDirective,
    OpenposScreenOutletDirective,
    ScreenDirective,
    AutocompleteDirective,
    DefaultImageDirective,
    KlassDirective,
    HideFormAccessoryBarDirective,
    FindFloatingElementDirective,
    FixediOsScrollDirective,
    ActionItemKeyMappingDirective,
    AutoCompleteAddressDirective,
    KeyPressSourceDirective,
    ArrowTabDirective,
    ArrowTabItemDirective
];

const pipes = [
    SafePipe,
    PhonePipe,
    HourMinSecPipe,
    LocalizedDatePipe,
    ValueFormatterPipe,
    POSCurrencyPipe,
    StringListFilterPipe,
    ListLimitPipe,
    MarkdownFormatterPipe,
    ImageUrlPipe,
    BackgroundImageUrlPipe
];

@NgModule({
    declarations: [
        ...directives,
        ...components,
        ...screenParts,
        ...pipes
    ],
    entryComponents: [
        KebabMenuComponent,
        ScanSomethingComponent,
        PopTartComponent,
        SearchablePopTartComponent,
        DatePartChooserDialogComponent,
        LocationOverrideDialogComponent,
        NavListComponent,
        SystemStatusDialogComponent
    ],
    imports: [
        FormsModule,
        ReactiveFormsModule,
        RouterModule,
        HttpModule,
        HttpClientModule,
        FlexLayoutModule,
        CommonModule,
        MaterialModule,
        MatKeyboardModule,
        TextMaskModule,
    ],
    exports: [
        FormsModule,
        ReactiveFormsModule,
        HttpModule,
        HttpClientModule,
        FlexLayoutModule,
        CommonModule,
        MaterialModule,
        MatKeyboardModule,
        TextMaskModule,

        ...directives,
        ...components,
        ...screenParts,
        ...pipes
    ]
})
export class SharedModule { }
