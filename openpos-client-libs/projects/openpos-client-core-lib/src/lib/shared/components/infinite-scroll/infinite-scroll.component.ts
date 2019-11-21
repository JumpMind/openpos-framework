import {Component, ElementRef, Input, OnInit, Renderer2, TemplateRef} from '@angular/core';
import {UIDataMessageService} from '../../../core/ui-data-message/ui-data-message.service';
import {InfiniteScrollDatasource} from './infinite-scroll-datasource';

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
   * Template to use when there are no items
    */
  @Input()
  noItemsTemplate: TemplateRef<T>;

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


  dataSource: InfiniteScrollDatasource<T>;

  constructor( private dataMessageService: UIDataMessageService, private el: ElementRef, private renderer: Renderer2 ) {

  }

  ngOnInit(): void {
    this.dataSource = new InfiniteScrollDatasource<T>(this.dataMessageService.getData$(this.dataKey), () => this.dataMessageService.requestMoreData(this.dataKey), this.dataLoadBuffer);
    this.dataSource.dataLoaded.subscribe( loaded => {
      if( loaded ){
        this.renderer.addClass(this.el.nativeElement, 'data-loaded');
      } else {
        this.renderer.removeClass(this.el.nativeElement, 'data-loaded');
      }
    })
  }
}
