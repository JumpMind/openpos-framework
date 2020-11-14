import { Observable, Subject } from 'rxjs';
import { Injectable } from '@angular/core';
import { AudioService } from './audio.service';
import { AudioInteractionService } from './audio-interaction.service';
import { IStartupTask } from '../core/startup/startup-task.interface';
import { StartupTaskNames } from '../core/startup/startup-task-names';
import { SessionService } from '../core/services/session.service';

@Injectable({
    providedIn: 'root',
})
export class AudioStartupTask implements IStartupTask {
    name = StartupTaskNames.AUDIO_STARTUP;
    // This startup task needs to happen late to ensure the session service is ready to publish requests
    order = 1000;

    constructor(private sessionService: SessionService, private audioService: AudioService, private audioInteractionService: AudioInteractionService) {

    }

    execute(): Observable<string> {
        return Observable.create((message: Subject<string>) => {
            message.next('Starting AudioService...');
            this.audioService.listen();
            message.next('AudioService started');

            message.next('Starting AudioInteractionService...');
            this.audioInteractionService.listen();
            message.next('AudioInteractionService started');

            this.sessionService.publish('GetConfig', 'Audio');
            message.complete();
        });
    }
}
