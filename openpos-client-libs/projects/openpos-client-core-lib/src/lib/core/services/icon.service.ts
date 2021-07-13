import { DomSanitizer, SafeHtml } from '@angular/platform-browser';
import { Injectable } from '@angular/core';
import { MatIconRegistry } from '@angular/material';
import { ImageService } from './image.service';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

@Injectable({
    providedIn: 'root',
})
export class IconService {
    private icons = new Map<string, [string, string]>();

    private iconBasePath = '${apiServerBaseUrl}/appId/${appId}/deviceId/${deviceId}/content?contentPath=icons/';
    private providerParam = '&provider=';
    private defaultIconProvider = 'classPathContentProvider';

    constructor(
        private sanitizer: DomSanitizer,
        private imageService: ImageService,
        private iconRegistry: MatIconRegistry) {

        this.icons.set('Account', ['account_balance.svg', this.defaultIconProvider]);
        this.icons.set('Add', ['add_circle.svg', this.defaultIconProvider]);
        this.icons.set('AddCustomer', ['person_add.svg', this.defaultIconProvider]);
        this.icons.set('AddEmployee', ['person_add.svg', this.defaultIconProvider]);
        this.icons.set('AddToCart', ['add_shopping_cart.svg', this.defaultIconProvider]);
        this.icons.set('Back', ['keyboard_arrow_left.svg', this.defaultIconProvider]);
        this.icons.set('BankIn', ['bank_transfer_in.svg', this.defaultIconProvider]);
        this.icons.set('Barcode', ['barcode.svg', this.defaultIconProvider]);
        this.icons.set('BusinessCustomer', ['business.svg', this.defaultIconProvider]);
        this.icons.set('BypassAction', ['low_priority.svg', this.defaultIconProvider]);
        this.icons.set('Calendar', ['today.svg', this.defaultIconProvider]);
        this.icons.set('CalendarError', ['event_busy.svg', this.defaultIconProvider]);
        this.icons.set('CalendarOk', ['event_available.svg', this.defaultIconProvider]);
        this.icons.set('Canada', ['canada.svg', this.defaultIconProvider]);
        this.icons.set('Cancel', ['cancel.svg', this.defaultIconProvider]);
        this.icons.set('CancelAction', ['block.svg', this.defaultIconProvider]);
        this.icons.set('Cash', ['local_atm.svg', this.defaultIconProvider]);
        this.icons.set('CashDown', ['cash_down.svg', this.defaultIconProvider]);
        this.icons.set('CashUp', ['cash_up.svg', this.defaultIconProvider]);
        this.icons.set('Check', ['money.svg', this.defaultIconProvider]);
        this.icons.set('Clock', ['watch_later.svg', this.defaultIconProvider]);
        this.icons.set('Close', ['close.svg', this.defaultIconProvider]);
        this.icons.set('Coin', ['monetization_on.svg', this.defaultIconProvider]);
        this.icons.set('Completed', ['check.svg', this.defaultIconProvider]);
        this.icons.set('CompletedCircle', ['check_circle.svg', this.defaultIconProvider]);
        this.icons.set('CreditCard', ['credit_card.svg', this.defaultIconProvider]);
        this.icons.set('Customer', ['person.svg', this.defaultIconProvider]);
        this.icons.set('CustomerEnter', ['dialpad.svg', this.defaultIconProvider]);
        this.icons.set('CustomerLink', ['link.svg', this.defaultIconProvider]);
        this.icons.set('DayEnd', ['brightness_3.svg', this.defaultIconProvider]);
        this.icons.set('DayStart', ['brightness_5.svg', this.defaultIconProvider]);
        this.icons.set('LightOn', ['lightbulb_on_outline.svg', this.defaultIconProvider]);
        this.icons.set('LightOff', ['lightbulb_outline.svg', this.defaultIconProvider]);
        this.icons.set('Decrement', ['remove.svg', this.defaultIconProvider]);
        this.icons.set('DefaultPrompt', ['question_answer.svg', this.defaultIconProvider]);
        this.icons.set('Delivery', ['local_shipping.svg', this.defaultIconProvider]);
        this.icons.set('DeviceUnknown', ['device_unknown.svg', this.defaultIconProvider]);
        this.icons.set('Device', ['device.svg', this.defaultIconProvider]);
        this.icons.set('Discount', ['local_offer.svg', this.defaultIconProvider]);
        this.icons.set('DollarSign', ['attach_money.svg', this.defaultIconProvider]);
        this.icons.set('Email', ['email.svg', this.defaultIconProvider]);
        this.icons.set('Employee', ['person.svg', this.defaultIconProvider]);
        this.icons.set('EmployeeEnter', ['keyboard.svg', this.defaultIconProvider]);
        this.icons.set('Employees', ['group.svg', this.defaultIconProvider]);
        this.icons.set('Error', ['error.svg', this.defaultIconProvider]);
        this.icons.set('ExternalLink', ['language.svg', this.defaultIconProvider]);
        this.icons.set('Finance', ['finance.svg', this.defaultIconProvider]);
        this.icons.set('Fingerprint', ['fingerprint.svg', this.defaultIconProvider]);
        this.icons.set('Flag', ['flag.svg', this.defaultIconProvider]);
        this.icons.set('Forward', ['keyboard_arrow_right.svg', this.defaultIconProvider]);
        this.icons.set('France', ['france.svg', this.defaultIconProvider]);
        this.icons.set('Mexico', ['mexico.svg', this.defaultIconProvider]);
        this.icons.set('Gift', ['gift_outline.svg', this.defaultIconProvider]);
        this.icons.set('GiftCard', ['card_giftcard.svg', this.defaultIconProvider]);
        this.icons.set('HamburgerMenu', ['menu.svg', this.defaultIconProvider]);
        this.icons.set('Help', ['help.svg', this.defaultIconProvider]);
        this.icons.set('Home', ['home.svg', this.defaultIconProvider]);
        this.icons.set('Increment', ['add.svg', this.defaultIconProvider]);
        this.icons.set('ItemList', ['list.svg', this.defaultIconProvider]);
        this.icons.set('Journal', ['book.svg', this.defaultIconProvider]);
        this.icons.set('KebabMenu', ['more_vert.svg', this.defaultIconProvider]);
        this.icons.set('Lease', ['account_balance.svg', this.defaultIconProvider]);
        this.icons.set('Links', ['link.svg', this.defaultIconProvider]);
        this.icons.set('Light', ['lightbulb_on_outline.svg', this.defaultIconProvider]);
        this.icons.set('Location', ['location_on.svg', this.defaultIconProvider]);
        this.icons.set('Login', ['lock.svg', this.defaultIconProvider]);
        this.icons.set('Logout', ['exit_to_app.svg', this.defaultIconProvider]);
        this.icons.set('LoyaltyProgram', ['loyalty.svg', this.defaultIconProvider]);
        this.icons.set('NoReceipt', ['block.svg', this.defaultIconProvider]);
        this.icons.set('OrderSearch', ['shopping_cart.svg', this.defaultIconProvider]);
        this.icons.set('OrderHistory', ['assignment.svg', this.defaultIconProvider]);
        this.icons.set('Password', ['lock.svg', this.defaultIconProvider]);
        this.icons.set('Percent', ['percent.svg', this.defaultIconProvider]);
        this.icons.set('Phone', ['phone.svg', this.defaultIconProvider]);
        this.icons.set('Pickup', ['schedule.svg', this.defaultIconProvider]);
        this.icons.set('Print', ['print.svg', this.defaultIconProvider]);
        this.icons.set('Productivity', ['chart_trending.svg', this.defaultIconProvider]);
        this.icons.set('PurchaseOrder', ['local_atm.svg', this.defaultIconProvider]);
        this.icons.set('Receipt', ['receipt.svg', this.defaultIconProvider]);
        this.icons.set('Remove', ['remove_circle.svg', this.defaultIconProvider]);
        this.icons.set('Reports', ['assessment.svg', this.defaultIconProvider]);
        this.icons.set('ResumeAction', ['play_arrow.svg', this.defaultIconProvider]);
        this.icons.set('RetrieveAction', ['replay.svg', this.defaultIconProvider]);
        this.icons.set('Return', ['undo.svg', this.defaultIconProvider]);
        this.icons.set('Sales', ['local_offer.svg', this.defaultIconProvider]);
        this.icons.set('ScaleBalance', ['scale_balance.svg', this.defaultIconProvider]);
        this.icons.set('Search', ['search.svg', this.defaultIconProvider]);
        this.icons.set('Security', ['security.svg', this.defaultIconProvider]);
        this.icons.set('Store', ['storefront.svg', this.defaultIconProvider]);
        this.icons.set('StoreCard', ['card_membership.svg', this.defaultIconProvider]);
        this.icons.set('SuspendAction', ['pause.svg', this.defaultIconProvider]);
        this.icons.set('Tax', ['account_balance.svg', this.defaultIconProvider]);
        this.icons.set('Till', ['till.svg', this.defaultIconProvider]);
        this.icons.set('Training', ['school.svg', this.defaultIconProvider]);
        this.icons.set('Trash', ['delete.svg', this.defaultIconProvider]);
        this.icons.set('UnitedStates', ['united-states.svg', this.defaultIconProvider]);
        this.icons.set('User', ['person.svg', this.defaultIconProvider]);
        this.icons.set('ViewAction', ['pageview.svg', this.defaultIconProvider]);
        this.icons.set('WebOrder', ['computer.svg', this.defaultIconProvider]);
        this.icons.set('AddNote', ['note_add.svg', this.defaultIconProvider]);
    }

    public addIconMapping(name: string, icon: string, provider?: string) {
        if (!provider) {
            this.icons.set(name, [icon + '.svg', this.defaultIconProvider]);
        } else {
            this.icons.set(name, [icon + '.svg', provider]);
        }
    }

    public getIconHtml(name: string): Observable<SafeHtml> {
        if (!name) {
            return null;
        }
        let url: string;
        if (name.includes('${apiServerBaseUrl}/appId/${appId}/deviceId/${deviceId}/content?contentPath=')) {
            url = name;
        } else {
            let iconFileName = name + '.svg';
            let provider = this.defaultIconProvider;
            if (this.icons.has(name)) {
                iconFileName = this.icons.get(name)[0];
                provider = this.icons.get(name)[1];
            }
            url = this.iconBasePath + iconFileName + this.providerParam + provider;
        }

        url = this.imageService.replaceImageUrl(url);
        const safeUrl = this.sanitizer.bypassSecurityTrustResourceUrl(url);

        return this.iconRegistry.getSvgIconFromUrl(safeUrl).pipe(
            map(i => this.sanitizer.bypassSecurityTrustHtml(i.outerHTML)));
    }
}
