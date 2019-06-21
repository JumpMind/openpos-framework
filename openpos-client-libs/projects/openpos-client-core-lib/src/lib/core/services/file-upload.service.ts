
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Logger } from './logger.service';
import { FileUploadResult } from './../interfaces/file-upload-result.interface';
import { PersonalizationService } from './personalization.service';
import { CordovaService } from './cordova.service';
import { Subscription } from 'rxjs';
import { FileChunkReader } from './../../shared/utils/filechunkreader';
@Injectable({
    providedIn: 'root',
  })
export class FileUploadService {
    constructor(private log: Logger, private cordovaService: CordovaService, private personalization: PersonalizationService, private httpClient: HttpClient) {
    }

    public async uploadLocalDeviceFileToServer(context: string, filename: string, contentType: string, filepath: string):
      Promise<FileUploadResult> {

        if (this.cordovaService.isRunningInCordova()) {
            let localfilepath = filepath;
            if (! filename.startsWith('file:')) {
                localfilepath = `file://${localfilepath}`;
            }
            this.log.info(`File to upload: ${localfilepath}`);

            const url = this.getUploadServiceUrl();
            this.log.info(`File upload endpoint url: ${url}`);
            try {
                return this.uploadFileInChunks(url, context, localfilepath, filename, contentType);
            } catch (error) {
                if ('success' in error && 'message' in error) {
                    return error;
                } else {
                    return { success: false, message: error instanceof Error ? error.message : JSON.stringify(error)};
                }
            }
        } else {
            const msg = `Not running in Cordova, cannot upload file ${filename}`;
            this.log.warn(msg);
           return Promise.reject({success: false, message: msg});
       }

    }

    public async uploadFileInChunks(url: string, context: string, localfilepath: string, filename: string,
        contentType: string, timeoutMillis = 60000): Promise<FileUploadResult> {

        let fileUploadTimer: any = null;
        let uploadSub: Subscription = null;
        // Callback function to handle each chunk read
        const uploadFunc = (blob: Blob): Promise<boolean> => {
            const prom = new Promise<boolean>((resolve, reject) => {
                const f = new FormData();
                f.append('nodeId', this.personalization.getDeviceId());
                f.append('targetContext', context);
                f.append('filename', filename);
                f.append('file', blob);
                f.append('chunkIndex', fileChunkReader.chunkIndex.toString());
                uploadSub = this.httpClient.post(url, f, {observe: 'response'}).subscribe(response => {
                        this.log.debug(`${filename} chunk uploaded.  bytes uploaded/total: ${fileChunkReader.bytesReadCount}/${fileChunkReader.fileSize}`);
                        resolve(true);
                    },
                    err => {
                        fileChunkReader.cancel();
                        if (fileUploadTimer) {
                            clearTimeout(fileUploadTimer);
                        }
                        throw err;
                    }
                );
            });
            return prom;
        };

        const fileChunkReader = new FileChunkReader(localfilepath, contentType, uploadFunc);

        const timeoutPromise = new Promise<FileUploadResult>((resolve, reject) => {
            fileUploadTimer = setTimeout( () => {
                this.log.info(`upload timed out`);
                fileChunkReader.cancel();
                if (uploadSub) {
                    uploadSub.unsubscribe();
                }
                resolve({success: false, message: `Upload request for '${filename}' timed out.`});
            },
            timeoutMillis);
        });


        const readAndUploadPromise = new Promise<FileUploadResult>((resolve, reject) => {
            fileChunkReader.readFileInChunks().then(result => {
                if (fileUploadTimer) {
                    clearTimeout(fileUploadTimer);
                }
                if (result) {
                    const msg = `File '${filename}' uploaded to server successfully.`;
                    this.log.info(msg);
                    // this.log.info(`File upload response: ${JSON.stringify(response)}`);
                    resolve({success: true, message: msg});
                } else {
                    const msg = `Upload of file '${filename}' FAILED!`;
                    this.log.error(msg);
                    resolve({success: false, message: msg});
                }
            }).catch(err => {
                if (fileUploadTimer) {
                    clearTimeout(fileUploadTimer);
                }
                const msg = `Upload Error occurred: ${err}`;
                this.log.error(msg);
                const statusCode = err.status || (err.error ? err.error.status : null);
                let errMsg = '';
                if (err.error) {
                    if (err.error.error) {
                        errMsg += err.error.error;
                    }
                    if (err.error.message) {
                        errMsg += (errMsg ? '; ' : '') + err.error.message;
                    }
                }
                const returnMsg = `${statusCode ? statusCode + ': ' : ''}` +
                   (errMsg ? errMsg : 'Upload failed. Check client and server logs.');
                reject({success: false, message: returnMsg});
            });
        });
        return Promise.race([timeoutPromise, readAndUploadPromise]);

    }

    protected getUploadServiceUrl(): string {
        const url = `${this.personalization.getServerBaseURL()}/fileupload/uploadToNode`;
        return url;
    }

    protected handleError(msg: string, e: any) {
        const errorMsg = `${msg}; Error code: ${e.code}, ${JSON.stringify(e)}`;
        this.log.error(errorMsg);
        throw new Error(errorMsg);
    }

}
