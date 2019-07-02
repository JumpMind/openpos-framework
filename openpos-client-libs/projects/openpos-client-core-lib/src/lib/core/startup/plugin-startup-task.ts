import { Observable, merge, concat, of, Subject, iif } from 'rxjs';
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

        const checkPlugins = Observable.create( (messages: Subject<string>) => {
            // remove plugins that are not loaded
            const pluginsToRemove = [];

            messages.next(`${this.plugins.length} plugins found to try`);
            this.plugins.forEach( p => {
                if ( !p.pluginPresent() ) {
                    pluginsToRemove.push(p);
                    messages.next(`Removing ${p.name()}`);
                } else {
                    messages.next(`Found ${p.name()}`);
                }
            });

            pluginsToRemove.forEach( p => {
                this.plugins.splice( this.plugins.indexOf(p), 1);
                if ( !!this.scanners && this.scanners.includes(p)) {
                    this.scanners.splice( this.scanners.indexOf(p), 1);
                }
            });
            messages.complete();
        }) as Observable<string>;

        const initializePlugins = Observable.create( (message: Subject<string>) => {
            const inits = this.plugins.map( p => {
                return concat(
                    of(`Initializing ${p.name()}`),
                    p.initialize()
                );
            });

            merge( ...inits ).subscribe( {
                next: m => message.next(m),
                error: e => message.error(e),
                complete: () => {
                    message.next('Done initializing plugins');
                    message.complete();
                }
            });
        });

        return concat(
            checkPlugins,
            iif( () => !!this.plugins && this.plugins.length > 0,
                concat (
                    of('Initializing plugins'),
                    initializePlugins
                ),
                of('No Plugins found')
            )
        );
    }
}
