import { IMenuItem } from '..';
import { IScan } from '../../templates/sell/isell-template';

export class SelfCheckoutStatusBarData {
    showScan: boolean;
    backButton: IMenuItem;
    showSkip: boolean;
    showAdmin: boolean;
    scanSomethingData: IScan;
}
