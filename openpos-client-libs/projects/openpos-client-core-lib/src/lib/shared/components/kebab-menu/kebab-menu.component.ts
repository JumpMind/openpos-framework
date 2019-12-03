import { Component, Inject, OnDestroy, ViewChildren, QueryList, ElementRef } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material';
import { Subscription } from 'rxjs/internal/Subscription';
import { KeyPressProvider } from '../../providers/keypress.provider';

@Component({
  selector: 'app-kebab-menu',
  templateUrl: './kebab-menu.component.html',
  styleUrls: ['./kebab-menu.component.scss']
})
export class KebabMenuComponent implements OnDestroy {

  @ViewChildren('menuItem', {read: ElementRef}) private menuItems: QueryList<ElementRef>;
  selectedIndex = 0;
  private menuItemSubscription: Subscription;

  constructor( @Inject(MAT_DIALOG_DATA) public data: any, public dialogRef: MatDialogRef<KebabMenuComponent>,
               protected keyPresses: KeyPressProvider) {
    this.menuItemSubscription = this.keyPresses.subscribe( 'ArrowDown', 1, (event: KeyboardEvent) => {
      // ignore repeats and check configuration
      if ( event.repeat || event.type !== 'keydown') {
        return;
      }
      if ( event.type === 'keydown') {
        this.handleArrowKey(event);
      }
    });

    this.menuItemSubscription.add(
      this.keyPresses.subscribe( 'ArrowUp', 1, (event: KeyboardEvent) => {
        // ignore repeats and check configuration
        if ( event.repeat || event.type !== 'keydown') {
          return;
        }
        if ( event.type === 'keydown') {
          this.handleArrowKey(event);
        }
      })
    );

    this.menuItemSubscription.add(
      this.keyPresses.subscribe('Tab', 1, (event: KeyboardEvent) => {
        if (event.repeat || event.type !== 'keydown') {
          return;
        }
        if ( event.type === 'keydown') {
          event.preventDefault();
          this.handleArrowKey(event);
        }
      })
    );

    this.menuItemSubscription.add(
      this.keyPresses.subscribe('Enter', 1, (event: KeyboardEvent) => {
        if (event.repeat || event.type !== 'keydown') {
          return;
        }
        if ( event.type === 'keydown') {
          this.selectMenuItem();
        }
      })
    );
  }

  ngOnDestroy(): void {
    if (this.menuItemSubscription) {
      this.menuItemSubscription.unsubscribe();
    }
  }

  handleArrowKey(event: KeyboardEvent) {
    let direction = 1;
    if (event.key === 'ArrowDown' || event.key === 'Tab') {
      direction = 1;
    } else if (event.key === 'ArrowUp') {
      direction = -1;
    } else {
      return;
    }

    let newIndex = this.selectedIndex + direction;
    if (this.selectedIndex === this.menuItems.length - 1 && event.key === 'Tab') {
      newIndex = 0;
    }

    while (newIndex > 0 && newIndex < this.menuItems.length - 1 && this.menuItems.toArray()[newIndex].nativeElement.disabled) {
      newIndex = newIndex + direction;
    }

    if ( this.menuItems && newIndex >= 0 && newIndex < this.menuItems.length && this.menuItems.toArray()[newIndex]) {
        this.selectedIndex = newIndex;
    }

  }

  selectMenuItem() {
    if (this.selectedIndex >= 0 && this.selectedIndex < this.menuItems.length) {
      this.closeMenu(this.data.menuItems[this.selectedIndex]);
    }
  }

  closeMenu(option: any) {
    this.dialogRef.close(option);
  }

}
