
import { TimeUnitLabels } from '../../utilites/time-utils';
import { IOrderCustomer } from './order-customer.interface';

export interface IOrderSummary {
    number: string;
    title: string;
    iconName: string;
    customer: IOrderCustomer;
    itemCount: string;
    status: string;
    statusLabel: string;
    statusIcon: string;
    orderDue: Date;
    orderTotal: string;
    itemCountIcon: string;
    customerIcon: string;
    orderTotalLabel: string;
    orderDueLabel: string;
    orderDueIcon: string;
    orderLabel: string;
    orderIcon: string;
    itemCountLabel: string;
    timeUnitLabels: TimeUnitLabels;
}
