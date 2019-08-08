import { IAbstractScreen } from '../../core/interfaces/abstract-screen.interface';
import { IActionItem } from '../../core/actions/action-item.interface';

export interface SignatureCaptureInterface extends IAbstractScreen {
    title: string;
    text: string;
    textIcon: string;
    signatureData: string;
    signatureMediaType: string;
    saveAction: IActionItem;
}
