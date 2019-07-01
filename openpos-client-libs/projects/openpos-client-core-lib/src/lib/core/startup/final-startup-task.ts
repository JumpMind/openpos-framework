import { Observable, Subject } from 'rxjs';
import { IStartupTask } from './startup-task.interface';
import { SessionService } from '../services/session.service';

export class FinalStartupTask implements IStartupTask {
    name = 'FinalStartup';
    order = 1000;

    constructor(private sessionService: SessionService) {
    }

    execute(): Observable<string> {
        return Observable.create( (message: Subject<string>) => {
            message.complete();
        });
    }
}
