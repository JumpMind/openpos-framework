import { forkJoin, Observable, Subject } from 'rxjs';
import { Injectable } from '@angular/core';
import { AudioService } from './audio.service';
import { AudioInteractionService } from './audio-interaction.service';
import { IStartupTask } from '../startup/startup-task.interface';
import { StartupTaskNames } from '../startup/startup-task-names';
import { AudioRepositoryService } from './audio-repository.service';

@Injectable({
    providedIn: 'root',
})
export class AudioStartupTask implements IStartupTask {
    name = StartupTaskNames.AUDIO_STARTUP;
    // This startup task needs to happen late to ensure the session service is ready to publish requests
    order = 950;

    constructor(private audioRepositoryService: AudioRepositoryService,
                private audioService: AudioService,
                private audioInteractionService: AudioInteractionService) {
    }

    execute(): Observable<string> {
        return Observable.create((message: Subject<string>) => {
            message.next('[AudioStartupTask]: Loading audio configuration...');
            message.next('[AudioStartupTask]: Preloading audio files...');

            forkJoin(
                this.audioRepositoryService.loadConfig(),
                this.audioRepositoryService.preloadAudio()
            ).subscribe(() => {
                message.next('[AudioStartupTask]: Completed loading configuration and preloading audio')

                message.next('[AudioStartupTask]: Starting AudioService...');
                this.audioService.listen();

                message.next('[AudioStartupTask]: Starting AudioInteractionService');
                this.audioInteractionService.listen();

                message.complete();
            });
        });
    }
}
