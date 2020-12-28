import {Component, Input, OnInit} from '@angular/core';
import {Toast, ToastPackage, ToastrService} from "ngx-toastr";
import {animate, state, style, transition, trigger} from "@angular/animations";

@Component({
  selector: 'app-toast',
  templateUrl: './toast.component.html',
  styles: [':host { display: block; }'],
  animations: [
    trigger('flyInOut', [
      state('inactive', style({ opacity: 0 })),
      state('active', style({ opacity: 1 })),
      state('removed', style({ opacity: 0 })),
      transition(
          'inactive => active',
          animate('{{ easeTime }}ms {{ easing }}')
      ),
      transition(
          'active => removed',
          animate('{{ easeTime }}ms {{ easing }}')
      )
    ])
  ],
})
export class ToastComponent extends Toast {
  @Input()
  iconName: string;

  constructor(
      protected toastrService: ToastrService,
      public toastPackage: ToastPackage,
  ) {
    super(toastrService, toastPackage);
  }
}
