import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HttpModule } from '@angular/http';
import { FlexLayoutModule } from '@angular/flex-layout';
import { HttpClientModule } from '@angular/common/http';
import { TextMaskModule } from 'angular2-text-mask';
import { ToastContainerModule, ToastrModule } from 'ngx-toastr';
import { MaterialModule } from '../material/material.module';
import { MatKeyboardModule } from '../keyboard/keyboard.module';
import { TaskCheckAllBoxComponent } from './components/task-check-all-box/task-check-all-box.component';
import { TaskCheckBoxComponent } from './components/task-check-box/task-check-box.component';
import { TaskListComponent } from './components/task-list/task-list.component';
import { ResponsiveGridDirective } from './directives/responsive-grid.directive';

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
import { FabToggleButtonComponent } from './components/fab-toggle-button/fab-toggle-button.component';
import { FabToggleGroupComponent } from './components/fab-toggle-group/fab-toggle-group.component';
import { PopTartComponent } from './components/pop-tart/pop-tart.component';
import { NavListComponent } from './components/nav-list/nav-list.component';
import { FileViewerComponent } from './components/file-viewer/file-viewer.component';
import { CounterComponent } from './components/counter/counter.component';
import { DatePartChooserComponent } from './components/date-part-chooser/date-part-chooser.component';
import { DatePartChooserFieldComponent } from './components/date-part-chooser/date-part-chooser-field/date-part-chooser-field.component';
import { DatePartChooserDialogComponent } from './components/date-part-chooser/date-part-chooser-dialog/date-part-chooser-dialog.component';
import { PrimaryButtonComponent } from './components/primary-button/primary-button.component';
import { AccentButtonComponent } from './components/accent-button/accent-button.component';
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
import { NotificationsComponent } from './screen-parts/notifications/notifications.component';
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
import { BannerComponent } from './screen-parts/banner/banner.component';
import { KebabLabelButtonComponent } from './components/kebab-label-button/kebab-label-button.component';
import { SaleItemCardListComponent } from './screen-parts/sale-item-card-list/sale-item-card-list.component';
import { RoundedInputComponent } from './components/rounded-input/rounded-input.component';
import { MobileFooterComponent } from './components/mobile-footer/mobile-footer.component';
import { SaleTotalPanelComponent } from './screen-parts/sale-total-panel/sale-total-panel.component';
import { ResponsiveClassDirective } from './directives/responsive-class.directive';
import { BaconDrawerComponent } from './screen-parts/bacon-strip/bacon-drawer/bacon-drawer.component';
import { CarouselComponent } from './components/carousel/carousel.component';
import { MobileTotalsPartComponent } from './screen-parts/mobile-totals-part/mobile-totals-part.component';
import { MobileLoyaltyPartComponent } from './screen-parts/mobile-loyalty-part/mobile-loyalty-part.component';
import { MobileEmployeePartComponent } from './screen-parts/mobile-employee-part/mobile-employee-part.component';
import { MobileSaleItemListComponent } from './screen-parts/mobile-sale-item-list/mobile-sale-item-list.component';
import { MobileItemComponent } from './components/mobile-item/mobile-item.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { InfiniteScrollComponent } from './components/infinite-scroll/infinite-scroll.component';
import { ImageComponent } from './components/image/image.component';
import { SearchExpandInputComponent } from './screen-parts/search-expand-input/search-expand-input.component';
import { HelpTextPageWrapperComponent } from './components/help-text-page-wrapper/help-text-page-wrapper.component';
import { OpenposAppHelpContainerComponent } from './components/openpos-app-help-container/openpos-app-help-container.component';
import { OptionsListComponent } from './screen-parts/options-list/options-list.component';
import { ScanPartComponent } from './screen-parts/scan-part/scan-part.component';
import { InstructionsComponent } from './components/instructions/instructions.component';
import { TitleComponent } from './components/title/title.component';
import { ContentCardComponent } from './components/content-card/content-card.component';
import { OptionButtonComponent } from './components/option-button/option-button.component';
import { TenderPartComponent } from './screen-parts/tender-part/tender-part.component';
import { OrderCardComponent } from './components/order-card/order-card.component';
import { LocalizedDateNoTimePipe } from './pipes/localized-date-no-time.pipe';
import { BasicBaconStripComponent } from './screen-parts/basic-bacon-strip/basic-bacon-strip.component';
import { ButtonActionTimerComponent } from './components/button-action-timer/button-action-timer.component';
import { ImageTextPanelComponent } from './screen-parts/image-text-panel/image-text-panel.component';
import { TransactionSummaryComponent } from './components/transaction-summary/transaction-summary.component';
import { TransactionHistoryPartComponent } from './screen-parts/transaction-history-part/transaction-history-part.component';
import { StampComponent } from './components/stamp/stamp.component';
import { FitTextDirective } from './directives/fit-text.directive';
import { PromptButtonRowComponent } from './screen-parts/prompt-button-row/prompt-button-row.component';
import { WarnButtonComponent } from './components/warn-button/warn-button.component';
import { StayFocusedDirective } from './directives/stay-focused.directive';
import { AudioLicenseComponent } from './components/audio-license/audio-license.component';
import { ToastComponent } from './components/toast/toast.component';
import { WatermarkComponent } from './components/watermark/watermark.component';
import { OverlayContainer } from '@angular/cdk/overlay';
import { ModalOverlayContainer } from './modal-overlay-container/modal-overlay-container';
import { StatusDetailsComponent } from './status/status-details/status-details.component';
import { PeripheralSelectorComponent } from './status/status-details/selector/peripheral-selector.component';
import { StatusDetailsItemComponent } from './status/status-details/item/status-details-item.component';
import { StatusBarComponent } from './status/status-bar/status-bar.component';
import { LocalizePipe } from './pipes/localize.pipe';
import { ModalOverlayContainerDirective } from './modal-overlay-container/modal-overlay-container.directive';
import { DisplayCustomerLookupComponent } from './screen-parts/display-customer-lookup/display-customer-lookup.component';
import { MembershipDisplayComponent } from './screen-parts/membership-display/membership-display.component';
import { ImageScannerComponent } from '../core/platform-plugins/barcode-scanners/image-scanner.component';
import { RewardsLineItemComponent } from './screen-parts/rewards-line-item/rewards-line-item.component';
import { CustomerInformationComponent } from './screen-parts/customer-information/customer-information.component';
import { MutableListItemWithLabelComponent } from "./screen-parts/mutable-list-item-with-label-component/mutable-list-item-with-label.component";
import { RewardsHistoryLineItemComponent } from './screen-parts/rewards-history-line-item/rewards-history-line-item.component';
import { MembershipPointsDisplayComponent } from "./screen-parts/membership-points-display/membership-points-display.component";
import { AnimationDirective } from './directives/animation.directive';

const screenParts = [
    TenderPartComponent,
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
    MembershipDisplayComponent,
    MembershipPointsDisplayComponent,
    CustomerInformationComponent,
    MutableListItemWithLabelComponent,
    RewardsLineItemComponent,
    RewardsHistoryLineItemComponent,
    ProgressBarPartComponent,
    WebcamViewerComponent,
    SystemStatusComponent,
    SaleItemCardListComponent,
    BannerComponent,
    NotificationsComponent,
    SaleTotalPanelComponent,
    MobileTotalsPartComponent,
    MobileLoyaltyPartComponent,
    MobileEmployeePartComponent,
    MobileSaleItemListComponent,
    OptionsListComponent,
    ScanPartComponent,
    SearchExpandInputComponent,
    BasicBaconStripComponent,
    ImageTextPanelComponent,
    TransactionHistoryPartComponent,
    PromptButtonRowComponent
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
    FabToggleButtonComponent,
    FabToggleGroupComponent,
    PopTartComponent,
    NavListComponent,
    FileViewerComponent,
    CounterComponent,
    DatePartChooserComponent,
    DatePartChooserFieldComponent,
    DatePartChooserDialogComponent,
    AccentButtonComponent,
    PrimaryButtonComponent,
    SecondaryButtonComponent,
    WarnButtonComponent,
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
    KebabLabelButtonComponent,
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
    ItemCardComponent,
    TaskCheckAllBoxComponent,
    TaskCheckBoxComponent,
    TaskListComponent,
    RoundedInputComponent,
    MobileFooterComponent,
    CarouselComponent,
    MobileItemComponent,
    InfiniteScrollComponent,
    ImageComponent,
    HelpTextPageWrapperComponent,
    OpenposAppHelpContainerComponent,
    InstructionsComponent,
    TitleComponent,
    ContentCardComponent,
    AudioLicenseComponent,
    OptionButtonComponent,
    OrderCardComponent,
    ButtonActionTimerComponent,
    TransactionSummaryComponent,
    StampComponent,
    ToastComponent,
    ImageScannerComponent,
    DisplayCustomerLookupComponent,
    WatermarkComponent
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
    ArrowTabItemDirective,
    ResponsiveClassDirective,
    ResponsiveGridDirective,
    FitTextDirective,
    StayFocusedDirective,
    AnimationDirective
];

const pipes = [
    SafePipe,
    PhonePipe,
    HourMinSecPipe,
    LocalizedDatePipe,
    LocalizedDateNoTimePipe,
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
        ...pipes,
        BaconDrawerComponent,
        StatusBarComponent,
        StatusDetailsComponent,
        StatusDetailsItemComponent,
        PeripheralSelectorComponent,
        LocalizePipe,
        ModalOverlayContainerDirective,
    ],
    entryComponents: [
        KebabMenuComponent,
        PopTartComponent,
        DatePartChooserDialogComponent,
        LocationOverrideDialogComponent,
        NavListComponent,
        SystemStatusDialogComponent,
        BaconDrawerComponent,
        HelpTextPageWrapperComponent,
        ToastComponent,
        StatusDetailsComponent,
        PeripheralSelectorComponent
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
        BrowserAnimationsModule,
        ToastrModule.forRoot({
            toastComponent: ToastComponent
        }),
        ToastContainerModule
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
        LocalizePipe,
        ...directives,
        ...components,
        ...screenParts,
        ...pipes
    ],
    providers: [
        { provide: OverlayContainer, useClass: ModalOverlayContainer }
    ]
})
export class SharedModule { }
