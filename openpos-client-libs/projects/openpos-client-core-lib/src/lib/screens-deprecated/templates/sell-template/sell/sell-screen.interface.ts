import { SelfCheckoutStatusBarData } from '../../../../self-checkout/self-checkout-statusbar/self-checkout-status-bar-data';
import { ISellTemplate } from './sell-template.interface';
import { SellStatusSectionData } from '../sell-status-section/sell-status-section.data';
import { IAbstractScreen } from '../../../../core/interfaces/abstract-screen.interface';
import { IActionItem } from '../../../../core/interfaces/action-item.interface';
import { ISellItem } from '../../../../core/interfaces/sell-item.interface';
import { StatusBarData } from '../../../../shared/components/status-bar/status-bar-data';

/**
 * @ignore
 */
export interface ISellScreen extends IAbstractScreen {
    name: string;
    screenType: string;
    backButton: IActionItem;
    logoutButton: IActionItem;
    template: ISellTemplate;
    locale: string;
    prompt: string;
    icon: string;
    theme: string;
    placeholderText: string;
    hideCustomer: boolean;
    subtitle: string[];
    items: ISellItem[];
    selectedItems: ISellItem[];
    multiSelectedMenuItems: IActionItem[];
    transaction: any;
    trainingInstructions: {[key: string]: string};
    customerName: string;
    noCustomerText: string;
    loyaltyButton: string;
}



export class SellScreenUtils {
    public static getStatusBar(screen: ISellScreen): StatusBarData {
        const statusBar = new StatusBarData();

        statusBar.backButton = screen.backButton;
        statusBar.logoutButton = screen.logoutButton;
        statusBar.operatorText = screen.template.operatorText;
        statusBar.screenIcon = screen.icon;
        statusBar.screenName = screen.name;
        statusBar.screenType = screen.screenType;
        if (screen.template.workstation) {
            statusBar.workstationId = screen.template.workstation.workstationId;
            statusBar.storeId = screen.template.workstation.storeId;
            statusBar.tillThresholdStatus = screen.template.workstation.tillThresholdStatus;
        }
        if (screen.trainingInstructions && Object.keys(screen.trainingInstructions).length > 0) {
            statusBar.trainingEnabled = true;
        } else {
            statusBar.trainingEnabled = false;
        }

        if (screen.template.statusBar && typeof screen.template.statusBar.enableHomeAction !== 'undefined') {
            statusBar.enableHomeAction = screen.template.statusBar.enableHomeAction;
        }

        return statusBar;
    }

    public static getStatusSection(template: ISellTemplate): SellStatusSectionData {
        const statusSection = new SellStatusSectionData();
        statusSection.systemStatus = template.systemStatus;
        statusSection.timestampBegin = template.timestampBegin;
        return statusSection;
    }

    public static getSelfCheckoutStatusBar(screen: ISellScreen): SelfCheckoutStatusBarData {
        const statusBar = new SelfCheckoutStatusBarData();

        statusBar.backButton = screen.backButton;
        statusBar.showAdmin = screen.template.statusBar.showAdmin;
        statusBar.showScan = screen.template.statusBar.showScan;
        statusBar.showSkip = screen.template.statusBar.showSkip;
        statusBar.showLanguageSelector = screen.template.statusBar.showLanguageSelector;
        statusBar.scanSomethingData = screen.template.scan;
        return statusBar;
    }

}
