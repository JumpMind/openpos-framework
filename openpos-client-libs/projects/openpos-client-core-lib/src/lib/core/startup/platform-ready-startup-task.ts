import { IStartupTask } from './startup-task.interface';
import { InjectionToken, Optional, Inject } from '@angular/core';
import { StartupTaskData } from './startup-task-data';
import { Observable, merge, of, concat, Subject } from 'rxjs';
import { IPlatformInterface } from '../platforms/platform.interface';
import { StartupTaskNames } from './startup-task-names';

export const PLATFORMS = new InjectionToken<IPlatformInterface[]>('Platforms');

/**
 * This startup task checks for any loaded platforms and waits for them to be ready before preceeding
 */
export class PlatformReadyStartupTask implements IStartupTask {

    name =  StartupTaskNames.PLATFORM_READY;
    order = 200;

    constructor( @Optional() @Inject(PLATFORMS) private platforms: Array<IPlatformInterface> ) {
    }

    execute(data?: StartupTaskData): Observable<string> {
        const messages = [];
        // Remove devices from the token that are not present
        messages.push(`${this.platforms.length} platforms found to try`);
        const platformsToRemove = this.platforms.filter( d => !d.platformPresent());
        platformsToRemove.forEach( d => {
            messages.push(`Removing ${d.getName()}`);
            this.platforms.splice(this.platforms.indexOf(d), 1);
        });

        if ( !!this.platforms && this.platforms.length > 0) {
            const readyObservables = this.platforms.map( d => d.platformReady() );
            return concat(
                ...messages.map( m => of(m)),
                of('Waiting for platforms to be ready'),
                merge( ...readyObservables )
            );
        } else {
            return concat(
                ...messages.map( m => of(m)),
                of('No platforms found to wait for')
            );
        }

    }


}
