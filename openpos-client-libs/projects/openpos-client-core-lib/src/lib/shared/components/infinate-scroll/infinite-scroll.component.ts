import {CdkVirtualScrollViewport} from '@angular/cdk/scrolling';
import {Component, Input, ViewChild, OnInit, TemplateRef, TrackByFunction} from '@angular/core';
import {Observable} from 'rxjs';
import {UIDataMessageService} from '../../../core/ui-data-message/ui-data-message.service';

/**
 * This component uses the UIDataMessageService to implement 'Infinite Scroll'. When the viewable area gets close to the
 * bottom of the loaded data it will reach out to the server for more data.
 */
@Component({
  selector: 'app-infinite-scroll',
  templateUrl: './infinite-scroll.component.html',
  styleUrls: ['./infinite-scroll.component.scss']
})
export class InfiniteScrollComponent<T> implements OnInit {

  /**
   * Key to use to fetch the data from the server
   */
  @Input()
  dataKey: string;

  /**
   * How tall is the template when rendered. Needs to be fixed so we can calculate when to load more items.
   */
  @Input()
  itemHeightPx: number;

  /**
   * How how many items do we want to make sure to have pre-loaded out of view. When the non-viewable items drops below
   * the buffer we fetch more
   */
  @Input()
  dataLoadBuffer: number = 1;

  /**
   * Template to apply to each item
   */
  @Input()
  itemTemplate: TemplateRef<T>;

  /**
   * How close to the edge of the rendered content do let the viewable area get before starting to render more.
   */
  @Input()
  virtualScrollMinBufferPx: number;

  /**
   * How far away from the edge of the viewable area do we render content.
    */
  @Input()
  virtualScrollMaxBufferPx: number;

  /**
   * Class to apply to each item
   */
  @Input()
  itemClass: string;

  /**
   * Class to apply to the list element
   */
  @Input()
  listClass: string;


  /**
   * Optionally provide a track by function to improve performance of updating the list
   * This method should return the value to use to uniquely identify an element.
   */
  @Input()
  itemTrackByFunction : TrackByFunction<T> = ( index: number, item: T ) => {};

  @ViewChild(CdkVirtualScrollViewport)
  scrollViewPort: CdkVirtualScrollViewport;

  items$: Observable<T[]>;

  constructor( private dataMessageService: UIDataMessageService ) { }

  ngOnInit(): void {
    this.items$ = this.dataMessageService.getData$(this.dataKey);
  }

  indexChange(){
    if(this.scrollViewPort.measureScrollOffset('bottom') < this.dataLoadBuffer * this.itemHeightPx) {
      this.dataMessageService.requestMoreData(this.dataKey);
    }
  }

}
