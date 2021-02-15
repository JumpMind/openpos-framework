import { Injectable } from '@angular/core';

import { Capacitor, Plugins as CapacitorPlugins } from '@capacitor/core';

import { Observable, from } from 'rxjs';
import { map } from 'rxjs/operators';
import { IPlatformPlugin } from '../platform-plugins/platform-plugin.interface';

@Injectable({
    providedIn: 'root'
})
export class CapacitorStatusBarPlatformPlugin implements IPlatformPlugin {
    name(): string {
        return 'cap-status-bar';
    }
    
    pluginPresent(): boolean {
        return Capacitor.isPluginAvailable('StatusBar');
    }

    initialize(): Observable<string> {
        return from(CapacitorPlugins.StatusBar.hide()).pipe(
            map(() => 'capacitor status bar hide requested')
        );
    }
}
