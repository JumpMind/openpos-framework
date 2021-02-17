import { concat, throwError, Observable, Subject, ReplaySubject, of } from 'rxjs';
import { Injectable, InjectionToken, Inject, Optional } from '@angular/core';
import { IStartupTask } from '../startup/startup-task.interface';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { map, catchError } from 'rxjs/operators';
import { MatDialog, MatDialogRef } from '@angular/material';
import { ComponentType } from '@angular/cdk/overlay/index';

export const STARTUP_TASKS = new InjectionToken<IStartupTask[]>('Startup Tasks');
export const STARTUP_FAILED_TASK = new InjectionToken<IStartupTask>('Startup Failed Task');
export const STARTUP_COMPONENT = new InjectionToken<ComponentType<any>>('Startup Component');
export const STARTUP_FAILED_COMPONENT = new InjectionToken<ComponentType<any>>('Startup Failed Component');

@Injectable({
    providedIn: 'root',
})
export class StartupService implements CanActivate {

    private dedupedTasks = new Map<string, IStartupTask>();

    public startupTaskMessages$ = new ReplaySubject<string>();
    public startupComplete = false;

    private allMessages = [];
    private startupDialogRef: MatDialogRef<any>;
    private startupRouteUrl: string;

    constructor(
        @Optional() @Inject(STARTUP_TASKS) tasks: Array<IStartupTask>,
        @Optional() @Inject(STARTUP_FAILED_TASK) private failedTask: IStartupTask,
        @Optional() @Inject(STARTUP_COMPONENT) private startupComponent: ComponentType<any>,
        @Optional() @Inject(STARTUP_FAILED_COMPONENT) private startupFailedComponent: ComponentType<any>,
        private matDialog: MatDialog) {

        // This might not be the best way but it's the best I could come up with for now.
        // This allows task defined in the core module to be overridden by vendor specific modules
        // for example overriding the personalization task
        if ( tasks ) {
            tasks.forEach( task => this.dedupedTasks.set(task.name, task));
        }
    }

    canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean> {
        // Save off the url so that it can be used if startup fails and the user does a 'Retry'.
        // If we don't save it, the route will have changed to the startup failed route.
        this.startupRouteUrl = location.href;

        // The reality is that we will probably always have atleast 2 tasks (personalize and subscribe to stomp)
        // But to be safe we will check first
        if ( this.dedupedTasks.size < 1 ) {
            return of(true);
        }

        if ( this.startupComponent ) {
            this.startupDialogRef = this.matDialog.open(
                this.startupComponent,
                {
                    width: '80%',
                    height: '80%',
                    disableClose: true,
                    hasBackdrop: false,
                    panelClass: 'openpos-default-theme'
                });
        }

        const list = Array.from(this.dedupedTasks.values());
        list.sort((a, b) => a.order - b.order );
        this.logTaskOrder(list);

        this.dedupedTasks.forEach(t => console.log(`found task ${t.name}`));

        // Get an array of task observables and attach task name to messages and errors
        const tasks = list.map(task => concat(
                //of(`running task - ${task.name}`),
                task.execute({ route, state }).pipe(
                    map(message => `${task.name}: ${message}`),
                    catchError(error => throwError(`${task.name}: ${error}`))
                ),
                //of(`finished running task - ${task.name}`)
            )
        );

        // Run all tasks in order
        return new Observable<boolean>(observer => {
            const sub = concat(...tasks).subscribe({
                next: (message) => { this.handleMessage(message); },
                error: (error) => {
                    observer.next(false);
                    console.error('Startup failed');
                    this.handleError( error );
                },
                complete: () => {
                    console.info('All Startup Tasks completed successfully');
                    observer.next(true);
                    observer.complete();

                    this.startupComplete = true;

                    if (this.startupDialogRef) {
                        this.startupDialogRef.close();
                    }
                }
            });

            return () => sub.unsubscribe();
        });
    }

    private handleMessage( message: string ) {
        this.startupTaskMessages$.next(message);
        console.info(message);
        this.allMessages.push(message);
    }

    private handleError( error: string ) {
        console.error(error);
        if ( this.failedTask ) {
            this.failedTask.execute().subscribe(
                {
                    next: (message) => this.handleMessage(message),
                    complete: () => this.showFailure(error),
                    error: (e) => console.error(`Error while running failure task: ${e}`)
                }
            );
        } else {
            this.showFailure( error );
        }
    }

    private logTaskOrder(tasks: IStartupTask[]) {
        console.info(`The following startup tasks will run: ${tasks.map(t => `${t.name}(${t.order})`).join(', ')}`);
    }

    private showFailure( error: string ) {
        if ( this.startupDialogRef ) {
            this.startupDialogRef.close();
        }

        if ( this.startupFailedComponent ) {
            const startupFailedRef = this.matDialog.open(
                this.startupFailedComponent, {
                    disableClose: true,
                    hasBackdrop: false,
                    width: '80%',
                    height: '80%',
                    data: {
                        error: error,
                        messages: this.allMessages
                    },
                    panelClass: 'openpos-default-theme'
                }
            );
            const startupFailedCompInst = startupFailedRef.componentInstance;
            startupFailedRef.afterClosed().subscribe( () => {
                if (startupFailedCompInst) {
                    if (startupFailedCompInst.appReloadOnCloseEnabled) {
                        location.href = this.startupRouteUrl;
                        location.reload();
                    }
                } else {
                    location.href = this.startupRouteUrl;
                    location.reload();
                }
            });
        }
    }
}
