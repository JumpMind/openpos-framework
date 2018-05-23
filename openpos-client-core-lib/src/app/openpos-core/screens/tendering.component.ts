import { IMenuItem } from './../common/imenuitem';
import { IItem } from './../common/iitem';
import { Component, ViewChild, DoCheck, OnInit, OnDestroy, AfterViewInit } from '@angular/core';
import { MatInput } from '@angular/material';
import { SessionService } from '../services/session.service';
import { IScreen } from '../common/iscreen';
import createNumberMask from 'text-mask-addons/dist/createNumberMask';
import { IFormElement } from '../common/iformfield';

@Component({
    selector: 'app-tendering',
    templateUrl: './tendering.component.html'
  })
  export class TenderingComponent implements IScreen, AfterViewInit, OnDestroy{

    @ViewChild('tenderAmountField') tenderAmountField: MatInput;

    screen: any;
    text: string;
    tenderItems: IItem[];
    tenderAmount: IFormElement;
    balanceDue: string;
    itemActions: IMenuItem[] = [];
    numberMask = createNumberMask({
        prefix: '',
        includeThousandsSeparator: false,
        allowDecimal: true,
        integerLimit: 9,
        requireDecimal: true,
        allowNegative: false
    });

    constructor(public session: SessionService) {
    }

    ngAfterViewInit(): void {
        setTimeout(() => this.tenderAmountField.focus(), 0); 
    }

    ngOnDestroy(): void {
        this.session.unregisterActionPayloads();
    }

    show(screen: any): void {
        this.screen = screen;

        this.text = this.screen.text;
        this.tenderItems = this.screen.tenderItems;
        this.tenderAmount = this.screen.tenderAmount;
        this.balanceDue = this.screen.balanceDue;
        this.itemActions = this.screen.itemActions;

        if (this.screen.template.localMenuItems) {
            this.screen.template.localMenuItems.forEach(element => {
                this.session.registerActionPayload( element.action, () => this.tenderAmount.value );
            });
        }
    }

}
