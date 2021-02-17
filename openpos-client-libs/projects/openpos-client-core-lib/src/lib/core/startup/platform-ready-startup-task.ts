import { IStartupTask } from './startup-task.interface';
import { InjectionToken, Optional, Inject } from '@angular/core';
import { StartupTaskData } from './startup-task-data';
import { Observable, merge, of, concat, iif } from 'rxjs';
import { IPlatformInterface } from '../platforms/platform.interface';
import { StartupTaskNames } from './startup-task-names';

export const PLATFORMS = new InjectionToken<IPlatformInterface[]>('Platforms');

/**
 * This startup task checks for any loaded platforms and waits for them to be ready before preceeding
 */
export class PlatformReadyStartupTask implements IStartupTask {

    name =  StartupTaskNames.PLATFORM_READY;
    order = 200;

    constructor( @Optional() @Inject(PLATFORMS) private platforms: Array<IPlatformInterface> ) {}

    execute(): Observable<string> {
        return concat(
            of(`${this.platforms.length} platform(s) to try`),
            new Observable<string>(observer => {
                console.log("platform plugin", this.platforms);
                const platformsToRemove = this.platforms.filter(p => !p.platformPresent());
                platformsToRemove.forEach(p => {
                    observer.next(`removing ${p.getName()}`);
                    this.platforms.splice(this.platforms.indexOf(p), 1);
                });

                observer.complete();
            }),
            iif(
                () => this.platforms && this.platforms.length > 0,
                concat(
                    of('wating for platforms to be ready'),
                    merge(...this.platforms.map(p => p.platformReady()))
                ),
                of('no platforms found to wait for')
            )
        );
    }
}
