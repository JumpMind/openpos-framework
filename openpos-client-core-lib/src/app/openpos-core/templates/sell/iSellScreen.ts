import { IMenuItem } from '../../common/imenuitem';
import { StatusBarData } from '../../common/screen-interfaces/statusBarData';
import { SelfCheckoutStatusBarData } from '../../common/screen-interfaces/selfCheckoutStatusBarData';
import { ISellTemplate } from './isell-template';

export interface ISellScreen {
    name: string;
    type: string;
    backButton: IMenuItem;
    logoutButton: IMenuItem;
    template: ISellTemplate;
    locale: string;
    prompt: string;
    icon: string;
    theme: string;
    placeholderText: string;
    hideCustomer: boolean;
}



export class SellScreenUtils {
    public static getStatusBar(screen: ISellScreen): StatusBarData {
        const statusBar = new StatusBarData();

        statusBar.backButton = screen.backButton;
        statusBar.logoutButton = screen.logoutButton;
        statusBar.operatorText = screen.template.operatorText;
        statusBar.screenIcon = screen.icon;
        statusBar.screenName = screen.name;
        statusBar.screenType = screen.type;
        if (screen.template.workstation) {
            statusBar.workstationId = screen.template.workstation.workstationId;
        }

        return statusBar;
    }

    public static getSelfCheckoutStatusBar(screen: ISellScreen): SelfCheckoutStatusBarData {
        const statusBar = new SelfCheckoutStatusBarData();

        statusBar.backButton = screen.backButton;
        statusBar.showAdmin = screen.template.statusBar.showAdmin;
        statusBar.showScan = screen.template.statusBar.showScan;
        statusBar.showSkip = screen.template.statusBar.showSkip;
        statusBar.scanSomethingData = screen.template.scan;
        return statusBar;
    }

}
