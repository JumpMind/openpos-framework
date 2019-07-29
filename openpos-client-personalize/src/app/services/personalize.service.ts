import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { PersonalizationResponse } from '../interfaces/personalization-response.interface';

@Injectable({
    providedIn: 'root',
})
export class PersonalizeService {

    constructor(private http: HttpClient) { }

    public async requestPersonalization(serverName: string, serverPort: string, sslEnabled: boolean): Promise<PersonalizationResponse> {
        let url = sslEnabled ? 'https://' : 'http://';
        url += serverName + ':' + serverPort + '/personalize';

        console.log('Requesting Personalization with url: ' + url);

        let personalizeError: any = null;
        try {
            const httpResult = await this.http.get<PersonalizationResponse>(url, {}).toPromise();
            if (httpResult) {
                httpResult.success = true;
                console.log('Successful Personalization with url: ' + url);
                return httpResult;
            } else {
                personalizeError = { message: '?' };
            }
        } catch (error) {
            personalizeError = error;
        }

        if (personalizeError) {
            console.warn('bad validation of ' + url + ' with an error message of :' + personalizeError.message);
            return { success: false, message: personalizeError.message };
        }
    }
}
