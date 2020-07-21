import {HttpClient} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {tap} from 'rxjs/operators';
import {DiscoveryService} from '../discovery/discovery.service';
import {FetchMessage} from '../messages/fetch-message';
import {OpenposMessage} from '../messages/message';
import {MessageTypes} from '../messages/message-types';
import {PersonalizationService} from '../personalization/personalization.service';
import {SessionService} from './session.service';

@Injectable({providedIn:'root'})
export class FetchMessageService {
    constructor( private session: SessionService, private http: HttpClient, private discovery: DiscoveryService, private personalizer: PersonalizationService,) {
        session.getMessages(MessageTypes.FETCH).subscribe( message => this.handleFetchMessage(message));
    }

    private handleFetchMessage( message: FetchMessage){
        let url = this.discovery.getApiServerBaseURL() + '/app/' + this.personalizer.getAppId$().getValue() + '/node/' + this.personalizer.getDeviceId$().getValue() + '/message/' + message.messageIdToFetch;
        console.log('Fetching message ' + url);
        this.http.get(url).pipe(
            tap( m => console.log('Fetched message: ', m)),
        ).subscribe( m => this.session.sendMessage(m as OpenposMessage));

    }

}