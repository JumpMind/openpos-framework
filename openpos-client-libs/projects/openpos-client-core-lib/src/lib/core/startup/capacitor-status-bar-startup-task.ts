import { Injectable } from '@angular/core';

import { Capacitor, Plugins as CapacitorPlugins } from '@capacitor/core';

import { Observable, of, from } from 'rxjs';
import { map } from 'rxjs/operators';

import { IStartupTask } from './startup-task.interface';

@Injectable()
export class CapacitorStatusBarStartupTask implements IStartupTask {
    name = 'cap-status-bar';

    order = 0;

    execute(): Observable<string> {
        if (!Capacitor.isPluginAvailable('StatusBar')) {
            return of('capacitor status bar plugin is not available; cannot show');
        }

        return from(CapacitorPlugins.StatusBar.hide()).pipe(
            map(() => "capacitor status bar hide requested")  
        );
    }
}
