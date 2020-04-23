import {Subject} from 'rxjs';
import { takeUntil, tap} from 'rxjs/operators';
import {Component, OnDestroy} from '@angular/core';
import {IScreen} from '../../../shared/components/dynamic-screen/screen.interface';
import {ScreenComponent} from '../../../shared/decorators/screen-component.decorator';
import {StartupService} from '../../services/startup.service';

@ScreenComponent({
    name: 'SplashScreen'
})
@Component({
    templateUrl: './splash-screen.component.html',
    styleUrls: ['./splash-screen.component.scss']
})
export class SplashScreenComponent implements IScreen, OnDestroy {

    message: string;
    private destroy$ = new Subject();

    constructor(startupService: StartupService) {
        startupService.startupTaskMessages$.pipe(
            tap( m => this.message = m),
            takeUntil(this.destroy$)
        ).subscribe();
    }

    show(screen: any ): void {
    }

    ngOnDestroy(): void {
        this.destroy$.next();
    }

}
