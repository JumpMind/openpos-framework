import { Injectable } from '@angular/core';
import { MediaService } from '@angular/flex-layout';
import { Observable } from 'rxjs';
import { startWith, map } from 'rxjs/operators';

@Injectable({
    providedIn: 'root',
  })
export class OpenposMediaService {
    constructor(private observableMedia: MediaService) {
    }

    /*
    ** Expects a map of media sizes (xs, s, m, l, xl) to values
    ** Returns an observable that streams out the appropriate values on media size changes
    */
    mediaObservableFromMap<T>( mappedValues: Map<string, T>): Observable<T> {

        let startValue: T;
        mappedValues.forEach((value, mqAlias) => {
            if (this.observableMedia.isActive(mqAlias)) {
              startValue = value;
            }
          });

         return this.observableMedia.asObservable().pipe(
                map(change => mappedValues.has(change.mqAlias) ? mappedValues.get(change.mqAlias) : mappedValues.get('')),
                startWith(startValue)
            );
    }

}


