import { Observable, merge, concat, of, Subject, iif, defer } from 'rxjs';
import { IStartupTask } from './startup-task.interface';
import { StartupTaskNames } from './startup-task-names';
import { InjectionToken, Optional, Inject } from '@angular/core';
import { IPlatformPlugin } from '../platform-plugins/platform-plugin.interface';
import { SCANNERS } from '../platform-plugins/scanners/scanner.service';
import { IScanner } from '../platform-plugins/scanners/scanner.interface';

export const PLUGINS = new InjectionToken<IPlatformPlugin[]>('Plugins');

/**
 * This startup task will find all registered plugins an check to see if they are loaded.
 * If the plug in is not loaded it will be removed from the PLATFORMS token otherwise this
 * task will wait for all plugins to initialize.
 */
export class PluginStartupTask implements IStartupTask {
    name =  StartupTaskNames.PLUGIN_INIT;
    order = 800;

    constructor(
        @Optional() @Inject(PLUGINS) private plugins: Array<IPlatformPlugin>,
        @Optional() @Inject(SCANNERS) private scanners: Array<IScanner> ) {
    }

    execute(): Observable<string> {
        return concat(
            of(`found ${this.plugins.length} plugin(s)`),
            new Observable<string>(observer => {
                this.plugins.filter(p => !p.pluginPresent()).forEach(p => {
                    observer.next(`removing plugin ${p.name()}`);
                    this.plugins.splice(this.plugins.indexOf(p), 1);

                    const scanner = p as unknown as IScanner;

                    if (scanner && this.scanners && this.scanners.includes(scanner)) {
                        observer.next(`removing scanner: ${p.name()}   index: ${this.scanners.indexOf(scanner)}`)
                        this.scanners.splice(this.scanners.indexOf(scanner), 1);
                    }
                });

                observer.complete();
            }),
            defer(() => merge(...this.plugins.map(p => concat(
                of(`initializing ${p.name()}`),
                p.initialize()
            )))),
            of('done initializing plugins')
        );
    }
}
