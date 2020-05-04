import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material';
import { ActionService } from '../../../core/actions/action.service';

@Component({
  selector: 'app-kebab-menu',
  templateUrl: './kebab-menu.component.html',
  styleUrls: ['./kebab-menu.component.scss'],

// I don't want this providers declaration here, but I have not been able to figure out
// how to resolve the following error otherwise:
//
// KebabMenuComponent.html:5 ERROR Error: StaticInjectorError(AppModule)[ActionItemKeyMappingDirective -> ActionService]:
//   StaticInjectorError(Platform: core)[ActionItemKeyMappingDirective -> ActionService]:
//     NullInjectorError: No provider for ActionService!
  providers: [ActionService]
})
export class KebabMenuComponent {

  constructor( @Inject(MAT_DIALOG_DATA) public data: any, public dialogRef: MatDialogRef<KebabMenuComponent>) {
  }

  closeMenu(option: any) {
    this.dialogRef.close(option);
  }

}
