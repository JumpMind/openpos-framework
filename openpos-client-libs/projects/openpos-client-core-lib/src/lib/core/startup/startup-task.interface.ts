import { Observable } from 'rxjs';
import { StartupTaskData } from './startup-task-data';

export interface IStartupTask {
    name: string;
    order: number;
    execute( data?: StartupTaskData): Observable<string>;
}
