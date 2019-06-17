import { DialogService } from '../../core/services/dialog.service';


export interface DialogProps {
    name: string;
}

export function DialogComponent( config: DialogProps ) {
    return function(target) {
        DialogService.dialogs.set(config.name, target);
    };
}
