import {CollectionViewer, DataSource} from '@angular/cdk/collections';
import {BehaviorSubject, Observable, Subscription} from 'rxjs';

export class InfiniteScrollDatasource<T> extends DataSource<T> {

    data = [];
    dataLoaded = new BehaviorSubject<boolean>(false);
    private subscription = new Subscription();
    private dataStream = new BehaviorSubject<(T | undefined)[]>([]);
    private readonly moreData: () => void;

    constructor( data: Observable<T[]>, moreData: () => void, private dataLoadBuffer: number){
        super();
        this.moreData = moreData;
        this.subscription.add(data.subscribe( d => {

            this.dataLoaded.next(d && d.length > 0);

            this.data = d;
            this.dataStream.next(this.data);
        }));
    }

    connect(collectionViewer: CollectionViewer): Observable<T[] | ReadonlyArray<T>> {

        this.subscription.add(collectionViewer.viewChange.subscribe( range => {
            if(range.end > this.data.length - this.dataLoadBuffer){
                this.moreData();
            }
        }));

        return this.dataStream;
    }

    disconnect(collectionViewer: CollectionViewer): void {
        if(this.subscription){
            this.subscription.unsubscribe();
        }
    }

}