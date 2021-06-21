import { Injectable } from '@angular/core';
import { map, scan} from 'rxjs/operators';
import {ReplaySubject, Subject, Observable} from 'rxjs';
import {ActionMessage} from '../messages/action-message';
import {MessageTypes} from '../messages/message-types';
import {UIDataMessage} from '../messages/ui-data-message';
import {SessionService} from '../services/session.service';

@Injectable({
  providedIn: 'root'
})
export class UIDataMessageService {

  //Accumulates the payloads by type
  private accumulatedResultsMap = new Map<String, ReplaySubject<any[]>>();
  //Splits out the messages by type
  private dataMessages = new Map<String, Subject<UIDataMessage<any[]>>>();

  constructor( private sessionService: SessionService) {
      this.sessionService.getMessages(MessageTypes.CONNECTED).subscribe( m => {
          //Clear out out cache if the server reboots. Otherwise we could end up out of sync
          this.accumulatedResultsMap.clear();
          this.dataMessages.clear();
      });

      this.sessionService.getMessages(MessageTypes.DATA_CLEAR).subscribe( m => {
          // Clear out out cache if the device is reset
          this.accumulatedResultsMap.clear();
          this.dataMessages.clear();
      });

    this.sessionService.getMessages(MessageTypes.DATA).subscribe( (message: UIDataMessage<any>) => {
        if(message.seriesId === -1){
            // -1 signals stale data that no longer exists on the server so clean up.
            this.dataMessages.get(message.dataType).complete();
            this.dataMessages.delete(message.dataType);
            this.accumulatedResultsMap.get(message.dataType).complete();
            this.accumulatedResultsMap.delete(message.dataType);
            console.log(`Clearing out Data ${message.dataType}`)
            return;
        }
        if( !this.dataMessages.has(message.dataType)){
            //If we don't have listeners set up yet create them
            console.log(`Received new data type ${message.dataType}`)
            this.createDataListener(message.dataType);
        }
        console.log(`Received new data for type ${message.dataType}`)
        this.dataMessages.get(message.dataType).next(message);
    } );
  }

  public getData$(key: string): Observable<any[]> {
      if(!this.accumulatedResultsMap.has(key)){
          //If we don't have any data for this key yet, add an entry and start listening
          this.createDataListener(key);
      }
      return this.accumulatedResultsMap.get(key);
  }

  public requestMoreData(key: string){
      this.sessionService.sendMessage(new ActionMessage(key, true));
  }

  private createDataListener( dataType: string){
      this.dataMessages.set(dataType, new Subject<UIDataMessage<any[]>>());
      this.accumulatedResultsMap.set(dataType, new ReplaySubject<any[]>(1));

      // set up a subscription that will accumulate all the messages in a series
      // map out just the data
      // then push it onto a replay subject so subscribers don't miss anything.
      this.dataMessages.get(dataType).pipe(
          scan( (acc: UIDataMessage<any[]>, curr: UIDataMessage<any[]>) => {
              // If this new data is part of our current series add to it.
              if(curr.seriesId === acc.seriesId){
                  acc.data.push(...curr.data);
                  return acc;
              }
              console.log(`New Series for ${curr.dataType}`)
              // If this data is not part of our series throw away the old accumulation and start a new accumulation
              return curr;
          }),
          map( (message: UIDataMessage<any[]>) => message.data)
      ).subscribe( value => this.accumulatedResultsMap.get(dataType).next(value));
  }
}