import {Injectable} from '@angular/core';
import {Observable, of, Subject} from 'rxjs';
import {IPlatformPlugin} from '../platform-plugin.interface';

export class AndroidContentQuery{
    constructor( public contentUri: string, public projection?: string[] ) {
    }
}

@Injectable({
    providedIn: 'root'
})
export class AndroidContentProviderPlugin implements IPlatformPlugin {


    name(): string {
        return 'Android Content Provider Plugin';
    }

    pluginPresent(): boolean {
        return window.hasOwnProperty('plugins') && window['plugins'].hasOwnProperty('contentproviderplugin');
    }

    initialize(): Observable<string> {
        return of('Android Content Provider Initialized');
    }

    query( queryRequest: AndroidContentQuery ){
        let response = new Subject();
        window['plugins'].contentproviderplugin.query({
            contentUri: queryRequest.contentUri,
            projection: queryRequest.projection,
            selection: null,
            selectionArgs: null,
            sortOrder: null
        }, function (data) {
            console.log(`Content provider data for request ${queryRequest} is : ${data}`);
            response.next(data);
            response.complete();
        }, function (err) {
            console.warn(`Content provider query resulted in error: ${err}`);
            response.error(err);
        });

        return response;
    }

}