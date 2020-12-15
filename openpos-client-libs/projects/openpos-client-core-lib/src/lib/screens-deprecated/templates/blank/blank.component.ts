import { OverlayContainer } from '@angular/cdk/overlay';
import { Component, Injector, OnInit } from '@angular/core';
import { AbstractTemplate } from '../../../core/components/abstract-template';
import { ScreenComponent } from '../../../shared/decorators/screen-component.decorator';

/**
 * @ignore
 */
@ScreenComponent({
    name: 'Blank'
})
@Component({
  selector: 'app-blank',
  templateUrl: './blank.component.html',
  styleUrls: ['./blank.component.scss']
})
export class BlankComponent extends AbstractTemplate<any> implements OnInit {

  template: any;

  constructor(public overlayContainer: OverlayContainer, injector: Injector) {
      super(injector);
  }

  ngOnInit() {
  }

  show(template: any) {
    this.template = template;
  }

  buildTemplate() {}
}
