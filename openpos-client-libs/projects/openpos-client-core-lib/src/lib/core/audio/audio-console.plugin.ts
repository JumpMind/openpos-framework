import { Injectable } from '@angular/core';
import { AudioService } from './audio.service';
import { IPlatformPlugin } from '../platform-plugins/platform-plugin.interface';
import { Observable, of } from 'rxjs';
import { AudioRequest } from './audio-request.interface';

@Injectable({
    providedIn: 'root'
})
export class AudioConsolePlugin implements IPlatformPlugin {
    constructor(private audioService: AudioService) {
    }

    initialize(): Observable<string> {
        console['playAudio'] = request => this.playSound(request);
        return of('[AudioConsolePlugin]: Added "playAudio" method to console. Usage: console.playSound("sad-trombone") or console.playSound({sound: "sad-trombone", loop: true})');
    }

    name(): string {
        return 'AudioConsolePlugin';
    }

    pluginPresent(): boolean {
        return true;
    }

    playSound(request: string | AudioRequest): void {
        const actualRequest = typeof request === 'string' ? {sound: request} : request;
        this.audioService.play(actualRequest);
    }
}