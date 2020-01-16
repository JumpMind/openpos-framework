import {ListRange} from '@angular/cdk/collections/typings/collection-viewer';
import {cold, getTestScheduler} from 'jasmine-marbles';
import {InfiniteScrollDatasource} from './infinite-scroll-datasource';

describe('infinite-scroll-datasource', () => {

    let infiniteScrollDatasource : InfiniteScrollDatasource<string>;
    const collectionViewerSpy = jasmine.createSpyObj('CollectionViewer', ['viewChange']);


    it('should call for more data when the view is near the end', () => {
        const moreDataSpy = jasmine.createSpyObj('MoreData', ['get']);
        let data = cold('x-', {x: ['data1', 'data2', 'data3', 'data4', 'data5', 'data6']});

        infiniteScrollDatasource = new InfiniteScrollDatasource<string>(data, moreDataSpy.get, 1);

        getTestScheduler().flush();

        collectionViewerSpy.viewChange = cold('-x', {x: {end: 5} as ListRange});
        infiniteScrollDatasource.connect(collectionViewerSpy);

        getTestScheduler().flush();

        expect(moreDataSpy.get).toHaveBeenCalled();
    });

    it( 'should not call for more data if the view is not near the end', () => {
        let data = cold('x-', {x: ['data1', 'data2', 'data3', 'data4', 'data5']});
        const moreDataSpy = jasmine.createSpyObj('MoreData', ['get']);
        infiniteScrollDatasource = new InfiniteScrollDatasource<string>(data, moreDataSpy.get, 1);

        getTestScheduler().flush();

        collectionViewerSpy.viewChange = cold('-x', {x: {end: 3} as ListRange});
        infiniteScrollDatasource.connect(collectionViewerSpy);

        getTestScheduler().flush();

        expect(moreDataSpy.get).not.toHaveBeenCalled();
    });
});