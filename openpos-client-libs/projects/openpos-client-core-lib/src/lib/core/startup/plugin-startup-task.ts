import { Observable, merge, concat, of, Subject, iif, defer, observable } from 'rxjs';
import { IStartupTask } from './startup-task.interface';
import { StartupTaskNames } from './startup-task-names';
import { InjectionToken, Optional, Inject } from '@angular/core';
import { IPlatformPlugin } from '../platform-plugins/platform-plugin.interface';
import { ImageScanner, SCANNERS, IMAGE_SCANNERS, Scanner } from '../platform-plugins/barcode-scanners/scanner';

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
        @Optional() @Inject(SCANNERS) private scanners: Array<Scanner>,
        @Optional() @Inject(IMAGE_SCANNERS) private imageScanners: Array<ImageScanner>) {
    }

    execute(): Observable<string> {
        return concat(
            of(`found ${this.plugins.length} plugin(s)`),
            new Observable<string>(observer => {
                this.plugins.filter(p => !p.pluginPresent()).forEach(p => {
                    observer.next(`removing plugin ${p.name()}`);
                    this.plugins.splice(this.plugins.indexOf(p), 1);

                    const scanner = p as unknown as Scanner;

                    if (scanner && this.scanners && this.scanners.includes(scanner)) {
                        observer.next(`removing scanner: ${p.name()}   index: ${this.scanners.indexOf(scanner)}`)
                        this.scanners.splice(this.scanners.indexOf(scanner), 1);
                    }

                    const imageScanner = p as unknown as ImageScanner;

                    if (imageScanner && this.imageScanners && this.imageScanners.includes(imageScanner)) {
                        observer.next(`removing image scanner: ${p.name()}   index: ${this.imageScanners.indexOf(imageScanner)}`);
                        this.imageScanners.splice(this.imageScanners.indexOf(imageScanner), 1);
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
