import { ScreenComponent } from '../../../shared/decorators/screen-component.decorator';
import { Component } from '@angular/core';
import { IScreen } from '../dynamic-screen/screen.interface';

@ScreenComponent({
    name: 'SplashScreen'
})
@Component({
    templateUrl: './splash-screen.component.html'
})
export class SplashScreenComponent implements IScreen {
    show(screen: any ): void {
    }

}
