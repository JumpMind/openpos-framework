import { FileUploadService } from '../services/file-upload.service';
import { CordovaDevicePlugin } from './cordova-device-plugin';
import { IDeviceRequest } from './device-request.interface';
/*
 * A wrapper around the OpenPOSCordovaLogPlugin to provide operations to allow
 * the server to request download of one or more log files from the client.
 */
export class LogfileDownloadPlugin extends CordovaDevicePlugin {

    pluginId = 'logfileDownloadPlugin';
    pluginName = this.pluginId;

    constructor(private fileUploadService: FileUploadService) {
        super('openPOSCordovaLogPlugin');
    }

    init(successCallback: () => void, errorCallback: (error?: string) => void): void {
        super.init(successCallback, errorCallback);
    }

    processRequest(deviceRequest: IDeviceRequest, successCallback: (response: any) => void, errorCallback: (error: string) => void) {
        switch (deviceRequest.subType) {
            case 'LIST': // list the log files
                this.log.info('list files request received.');
                if (this.impl) {
                    this.impl.listLogFiles('DESC', successCallback, errorCallback);
                } else {
                    this.log.info('No log files available');
                    successCallback([]);
                }
                break;
            case 'DOWNLOAD_FILES': // download the given list of files
                this.log.info('download-files request received.');
                if (this.impl) {
                    const logFiles = deviceRequest.payload || [];
                    const uploadPromises: Promise<string>[] = [];
                    logFiles.forEach(logfile => {
                        uploadPromises.push(this.uploadFile(logfile));
                    });
                    Promise.all(uploadPromises).then(messages => {
//                        results.forEach(result => this.log.info(`Promise result: ${JSON.stringify(result)}`));
//                        this.log.info(`Messages received: ${messages}`);
                        successCallback(messages);
                    }).catch( error =>
                        errorCallback(error)
                    );
                } else {
                    successCallback('No files to download');
                }
                break;
        }
    }

    private uploadFile(logFilename: string): Promise<string> {
        return new Promise<string>((resolve, reject) => {
            this.impl.getLogFilePath(
                logFilename,
                (logfilePath) => {
                    this.fileUploadService.uploadLocalDeviceFileToServer('log', logFilename, 'text/plain', logfilePath).then(
                        result => {
                            resolve(result.message);
                        }
                    ).catch(error => {
                        reject(error);
                    });
                },
                (error) => {
                    this.log.info(error);
                    reject(error);
                }
            );
        });
    }

}
