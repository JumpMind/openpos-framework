import { Logger } from './logger.service';
import { concat, throwError, Observable, Subject, ReplaySubject, of } from 'rxjs';
import { Injectable, InjectionToken, Inject, Optional } from '@angular/core';
import { IStartupTask } from '../interfaces/startup-task.interface';
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

    private allMessages = [];
    private startupDialogRef: MatDialogRef<any>;

    constructor(
        @Optional() @Inject(STARTUP_TASKS) tasks: Array<IStartupTask>,
        @Optional() @Inject(STARTUP_FAILED_TASK) private failedTask: IStartupTask,
        @Optional() @Inject(STARTUP_COMPONENT) private startupComponent: ComponentType<any>,
        @Optional() @Inject(STARTUP_FAILED_COMPONENT) private startupFailedComponent: ComponentType<any>,
        private matDialog: MatDialog,
        private log: Logger) {

        // This might not be the best way but it's the best I could come up with for now.
        // This allows task defined in the core module to be overriden by vendor specific modules
        // for example overriding the personalization task
        if ( tasks ) {
            tasks.forEach( task => this.dedupedTasks.set(task.name, task));
        }
    }

    canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean> {
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

        // Get an array of task observables and attach task name to messages and errors
        const tasks = list.map( task => task.execute( { route, state }).pipe(
            map( message => `${task.name}: ${message}`),
            catchError( error => throwError(`${task.name}: ${error}`))) );



        // Run all tasks in order
        return Observable.create( (result: Subject<boolean>) => {
            concat(...tasks).subscribe(
                {
                    next: (message) => { this.handleMessage(message); },
                    error: (error) => {
                        result.next(false);
                        this.log.error('Startup failed');
                        this.handleError( error );
                    },
                    complete: () => {
                        this.log.info('All Startup Tasks completed successfully');
                        result.next(true);
                        if ( this.startupDialogRef ) {
                            this.startupDialogRef.close();
                        }
                    }
                }
            );
        });
    }

    private handleMessage( message: string ) {
        this.startupTaskMessages$.next(message);
        this.log.info(message);
        this.allMessages.push(message);
    }

    private handleError( error: string ) {
        this.log.error(error);
        if ( this.failedTask ) {
            this.failedTask.execute().subscribe(
                {
                    next: (message) => this.handleMessage(message),
                    complete: () => this.showFailure(error),
                    error: (e) => this.log.error(`Error while running failure task: ${e}`)
                }
            );
        } else {
            this.showFailure( error );
        }
    }

    private logTaskOrder(tasks: IStartupTask[]) {
        this.log.info(`The following startup tasks will run: ${tasks.map(t => `${t.name}(${t.order})`).join(', ')}`);
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
                        location.reload();
                    }
                } else {
                    location.reload();
                }
            });
        }
    }
}
