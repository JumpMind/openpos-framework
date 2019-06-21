import { IActionItem } from '../../core/interfaces/action-item.interface';
import { IScan } from '../../screens-deprecated/templates/sell-template/sell/scan.interface';


export class SelfCheckoutStatusBarData {
    showScan: boolean;
    backButton: IActionItem;
    showSkip: boolean;
    showAdmin: boolean;
    showLanguageSelector: boolean;
    scanSomethingData: IScan;
    type: any;
}
