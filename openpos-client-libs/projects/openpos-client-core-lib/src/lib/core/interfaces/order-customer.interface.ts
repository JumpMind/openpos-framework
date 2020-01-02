import { IAddress } from './address.interface';

export interface IOrderCustomer {
    fullName: string;
    phone: string;
    email: string;
    iconName: string;
    address: IAddress;
}
