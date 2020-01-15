import { Component, Injector } from '@angular/core';
import { BasicBaconStripInterface } from './basic-bacon-strip.interface';
import { ScreenPart } from '../../decorators/screen-part.decorator';
import { ScreenPartComponent } from '../screen-part';
import { Observable } from 'rxjs';
import { OpenposMediaService, MediaBreakpoints } from '../../../core/media/openpos-media.service';


@ScreenPart({
  name: 'basicBaconStrip'
})
@Component({
  selector: 'app-basic-bacon-strip',
  templateUrl: './basic-bacon-strip.component.html',
  styleUrls: ['./basic-bacon-strip.component.scss']
})
export class BasicBaconStripComponent extends ScreenPartComponent<BasicBaconStripInterface> {

  isMobile: Observable<boolean>;

  constructor(injector: Injector, private media: OpenposMediaService) {
    super(injector);

    this.isMobile = media.observe(new Map([
      [MediaBreakpoints.MOBILE_PORTRAIT, true],
      [MediaBreakpoints.MOBILE_LANDSCAPE, true],
      [MediaBreakpoints.TABLET_PORTRAIT, true],
      [MediaBreakpoints.TABLET_LANDSCAPE, false],
      [MediaBreakpoints.DESKTOP_PORTRAIT, false],
      [MediaBreakpoints.DESKTOP_LANDSCAPE, false]
    ]));
  }
  screenDataUpdated() { }

}
