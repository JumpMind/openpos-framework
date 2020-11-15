import { Observable, Subject } from 'rxjs';
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
    order = 1000;

    constructor(private audioRepositoryService: AudioRepositoryService,
                private audioService: AudioService,
                private audioInteractionService: AudioInteractionService) {
    }

    execute(): Observable<string> {
        return Observable.create((message: Subject<string>) => {
            message.next('[AudioStartupTask]: Starting AudioService...');
            this.audioService.listen();
            message.next('[AudioStartupTask]: AudioService started');

            message.next('[AudioStartupTask]: Starting AudioInteractionService...');
            this.audioInteractionService.listen();
            message.next('[AudioStartupTask]: AudioInteractionService started');

            console.log('[AudioStartupTask]: Loading audio configuration...');
            this.audioRepositoryService.loadConfig();
            console.log('[AudioStartupTask]: Preloading audio files...');
            this.audioRepositoryService.preloadAudio();

            message.complete();

            // This allows playing sounds by key name (instead of URL), which helps when experimenting to get
            // your setting just right when configuring a new sound.
            //
            // Example:
            // sessionService.publish('Play', 'Audio', {sound: 'click-in-out-thin', startTime: .1, endTime: .25})
            // window['sessionService'] = this.audioService['sessionService'];
        });
    }
}
