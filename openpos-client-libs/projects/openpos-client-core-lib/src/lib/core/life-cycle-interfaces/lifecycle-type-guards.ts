import { OnBecomingActive } from './becoming-active.interface';
import { OnLeavingActive } from './leaving-active.interface';

export class LifeCycleTypeGuards {

    static handlesBecomingActive(screen: any): screen is OnBecomingActive {
        return (screen as OnBecomingActive).onBecomingActive !== undefined;
    }

    static handlesLeavingActive(screen: any): screen is OnLeavingActive {
        return (screen as OnLeavingActive).onLeavingActive !== undefined;
    }

}
