import { Component, Input} from '@angular/core';
import { OpenposMediaService } from '../../../core/services/openpos-media.service';
import { Observable } from 'rxjs';

@Component({
    selector: 'app-overflow-list',
    templateUrl: './overflow-list.component.html',
    styleUrls: ['./overflow-list.component.scss']
})
export class OverFlowListComponent {

    @Input() numberItemsToShow: Observable<number> = this.mediaService.mediaObservableFromMap(new Map([
        ['xs', 3],
        ['sm', 3],
        ['md', 4],
        ['lg', 6],
        ['xl', 6]
      ]));

    @Input() items: any[];

    shownItems: any[];
    overflowItems: any[];

    constructor(private mediaService: OpenposMediaService) {}
}
