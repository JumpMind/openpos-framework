import { NavListComponent } from './../../dialogs/nav-list/nav-list.component';
import { MatDialog } from '@angular/material';
import { DeviceService } from '../../services/device.service';
import { ISellItem } from '../../common/isellitem';
import { IScreen } from '../../common/iscreen';
import { IMenuItem } from '../../common/imenuitem';
import { Component, ViewChild, AfterViewInit, AfterContentInit, DoCheck, OnInit, AfterViewChecked, ElementRef} from '@angular/core';
import { SessionService } from '../../services/session.service';
import { ObservableMedia } from '@angular/flex-layout';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { Observable } from 'rxjs/Observable';

@Component({
  selector: 'app-transaction',
  templateUrl: './transaction.component.html',
  styleUrls: ['./transaction.component.scss']
})
export class TransactionComponent implements AfterViewInit, AfterViewChecked, IScreen, OnInit {

  screen: any;
  @ViewChild('box') vc;
  @ViewChild('scrollList') private scrollList: ElementRef;
  public size = -1;
  initialized = false;

  public overFlowListSize: Observable<number>;

  public items: ISellItem[];

  constructor(public session: SessionService, devices: DeviceService, 
    private observableMedia: ObservableMedia, protected dialog: MatDialog) {

    }

  show(screen: any) {
    this.screen = screen;
    this.items = this.screen.items;
  }

  ngOnInit(): void {
    const sizeMap = new Map([
      ['xs', 3],
      ['sm', 3],
      ['md', 4],
      ['lg', 5],
      ['xl', 5]
    ]);

    let startSize = 3;
    sizeMap.forEach((size, mqAlias) => {
      if( this.observableMedia.isActive(mqAlias)){
        startSize = size;
      }
    });
    this.overFlowListSize = this.observableMedia.asObservable().map(
      change => {
        return sizeMap.get(change.mqAlias);
      }
    ).startWith(startSize);
  }

  ngAfterViewInit(): void {
    this.initialized = true;
  }

  onEnter(value: string) {
    this.session.response = value;
    this.session.onAction('Next');
  }

  openItemDialog(item: ISellItem) {
    this.session.response = item.index;
    const dialogRef = this.dialog.open(NavListComponent, {
      width: '70%',
      data: {
        optionItems: item.menuItems,
        disableClose: false,
        autoFocus: false
     }
    });

    dialogRef.afterClosed().subscribe(result => {
      console.log('The dialog was closed');
    });
  }

  ngAfterViewChecked() {
    if (this.items && this.size !== this.items.length) {
      this.scrollToBottom();
      this.size = this.items.length;
    }
  }

  scrollToBottom(): void {
    try {
      this.scrollList.nativeElement.scrollTop = this.scrollList.nativeElement.scrollHeight;
    } catch (err) { }
  }

}
