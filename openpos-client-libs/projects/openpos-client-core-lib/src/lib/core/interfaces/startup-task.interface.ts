import { Observable } from 'rxjs';
import { StartupTaskData } from '../components/startup/startup-task-data';

export interface IStartupTask {
    name: string;
    order: number;
    execute( data?: StartupTaskData): Observable<string>;
}
