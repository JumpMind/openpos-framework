import { Component, Output, EventEmitter, Injector, Input, ViewChildren, ElementRef, QueryList, OnDestroy } from '@angular/core';
import { OptionsListInterface } from './options-list.interface';
import { ScreenPart } from '../../../shared/decorators/screen-part.decorator';
import { ScreenPartComponent } from '../../../shared/screen-parts/screen-part';
import { IActionItem } from '../../../core/actions/action-item.interface';
import { Observable, Subscription } from 'rxjs';
import { OpenposMediaService, MediaBreakpoints } from '../../../core/media/openpos-media.service';
import { FocusService } from '../../../core/focus/focus.service';
import { MatDialog } from '@angular/material';
import { KebabMenuComponent } from '../../components/kebab-menu/kebab-menu.component';
import { KeyPressProvider } from '../../providers/keypress.provider';
import { Configuration } from '../../../configuration/configuration';


@ScreenPart({
    name: 'optionsList'
})
@Component({
    selector: 'app-options-list',
    templateUrl: './options-list.component.html',
    styleUrls: ['./options-list.component.scss']
})
export class OptionsListComponent extends ScreenPartComponent<OptionsListInterface> {

    @Output()
    optionClick = new EventEmitter<IActionItem>();

    @Input()
    listSize = -1;

    @Input()
    optionListSizeClass = 'lg';

    options: IActionItem[] = [];
    overflowOptions: IActionItem[] = [];

    isMobile: Observable<boolean>;

    selectedIndex = -1;
    overflowSelected = false;

    constructor( injector: Injector, mediaService: OpenposMediaService, protected dialog: MatDialog,
                 protected focusService: FocusService, protected keyPresses: KeyPressProvider) {

        super(injector);
        this.isMobile = mediaService.observe(new Map([
            [MediaBreakpoints.MOBILE_PORTRAIT, true],
            [MediaBreakpoints.MOBILE_LANDSCAPE, true],
            [MediaBreakpoints.TABLET_PORTRAIT, true],
            [MediaBreakpoints.TABLET_LANDSCAPE, true],
            [MediaBreakpoints.DESKTOP_PORTRAIT, false],
            [MediaBreakpoints.DESKTOP_LANDSCAPE, false]
        ]));

        this.subscriptions.add(
            this.keyPresses.subscribe( 'ArrowDown', 1, (event: KeyboardEvent) => {
              // ignore repeats and check configuration
              if ( event.repeat || event.type !== 'keydown' || !Configuration.enableKeybinds) {
                return;
              }
              if ( event.type === 'keydown') {
                this.handleArrowKey(event);
              }
            })
        );

        this.subscriptions.add(
            this.keyPresses.subscribe( 'ArrowUp', 1, (event: KeyboardEvent) => {
              // ignore repeats and check configuration
              if ( event.repeat || event.type !== 'keydown' || !Configuration.enableKeybinds) {
                return;
              }
              if ( event.type === 'keydown') {
                this.handleArrowKey(event);
              }
            })
        );

        this.subscriptions.add(
            this.keyPresses.subscribe('Tab', 1, (event: KeyboardEvent) => {
              if (event.repeat || event.type !== 'keydown' || !Configuration.enableKeybinds) {
                return;
              }
              if ( event.type === 'keydown') {
                event.preventDefault();
                this.handleArrowKey(event);
              }
            })
        );

        this.subscriptions.add(
            this.keyPresses.subscribe('Enter', 1, (event: KeyboardEvent) => {
              if (event.repeat || event.type !== 'keydown' || !Configuration.enableKeybinds) {
                return;
              }
              if ( event.type === 'keydown') {
                this.selectOption();
              }
            })
        );
    }

    screenDataUpdated() {
        if (this.listSize > 0 && this.screenData.options && this.listSize < this.screenData.options.length) {
            this.options = [];
            this.overflowOptions = [];
            for (let i = 0; i < this.screenData.options.length; i++) {
                if (i < this.listSize - 1) {
                    this.options.push(this.screenData.options[i]);
                } else {
                    this.overflowOptions.push(this.screenData.options[i]);
                }
            }
        } else {
            this.options = this.screenData.options;
        }
    }

    onOptionClick(actionItem: IActionItem): void {
        if ( this.optionClick.observers.length > 0 ) {
            this.optionClick.emit(actionItem);
        } else {
            this.doAction(actionItem);
        }
    }

    public openKebabMenu() {
        if (this.dialog.openDialogs.length < 1) {
            const dialogRef = this.dialog.open(KebabMenuComponent, {
                data: {
                    menuItems: this.overflowOptions,
                    payload: null,
                    disableClose: false,
                    autoFocus: false,
                    restoreFocus: false
                },
                autoFocus: false
            });

            dialogRef.afterClosed().subscribe(result => {
                if (result) {
                    this.optionClick.emit(result);
                }
                this.focusService.restoreInitialFocus();
            });
        }
    }

    selectOption() {
        if (this.overflowSelected) {
            this.openKebabMenu();
        } else if (this.selectedIndex >= 0 && this.selectedIndex < this.options.length) {
            this.onOptionClick(this.options[this.selectedIndex]);
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

        if (this.overflowSelected) {
            if (event.key === 'ArrowUp') {
                newIndex = this.options.length - 1;
                this.overflowSelected = false;
            } else if (event.key === 'Tab') {
                newIndex = 0;
                this.overflowSelected = false;
            } else {
                // on arrow down, keep overflow button selected
                newIndex = -1;
            }
        } else if (this.selectedIndex === this.options.length - 1) {
            if (event.key === 'ArrowDown' && this.overflowOptions && this.overflowOptions.length > 0) {
                newIndex = -1;
                this.overflowSelected = true;
            } else if (event.key === 'Tab') {
                if (this.overflowOptions && this.overflowOptions.length > 0) {
                    newIndex = -1;
                    this.overflowSelected = true;
                } else {
                    newIndex = 0;
                }
            }
        }

        while (newIndex > 0 && newIndex < this.options.length - 1 && !this.options[newIndex].enabled) {
          newIndex = newIndex + direction;
        }

        if (newIndex === this.options.length - 1 && !this.options[newIndex].enabled) {
            if (this.overflowOptions && this.overflowOptions.length > 0) {
                newIndex = -1;
                this.overflowSelected = true;
            } else {
                newIndex = newIndex - direction;
                while (newIndex > 0 && newIndex < this.options.length - 1
                    && !this.options[newIndex].enabled) {
                    newIndex = newIndex - direction;
                }
            }
        }

        if (this.overflowSelected) {
            this.selectedIndex = newIndex;
        } else if (this.options && newIndex >= 0 && newIndex < this.options.length && this.options[newIndex]) {
            this.selectedIndex = newIndex;
        }
    }

}
