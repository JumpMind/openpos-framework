import { Subject, Subscription } from 'rxjs';

export class FileChunkReader {
    static readonly DEFAULT_CHUNK_SIZE = 500 * 1024;  // 500 KB

    protected file: any;
    protected _fileSize: number;
    protected reader: FileReader;
    protected currentOffset = 0;
    protected _done = false;
    protected cancelled = false;
    protected onReadComplete = new Subject<boolean>();
    protected completeSubscr: Subscription;
    protected _chunkIndex = 0;


    constructor(
        public filepath: string,
        public contentType: string,
        public chunkHandler: (blob: Blob) => Promise<boolean>,
        public chunkSize = FileChunkReader.DEFAULT_CHUNK_SIZE
        ) {
    }

    public get done(): boolean {
        return this._done;
    }

    public get chunkIndex(): number {
        return this._chunkIndex;
    }

    public get bytesReadCount(): number {
        return this.currentOffset;
    }

    public get fileSize(): number {
        return this._fileSize;
    }

    public cancel(): void {
        this.cancelled = true;
        this.onFinished();
        console.log(`Read cancelled on file '${this.file}'`);
    }


    public readFileInChunks(): Promise<boolean> {
        const prom = new Promise<boolean>(async (resolve, reject) => {
            try {
                const initResult = await this.initFile();
                if (! initResult) {
                    reject(`Failed to initialize filesystem or access to file`);
                }
//                console.log(`Starting reading`);
                const processResult = await this.processFile();
                this.onFinished();
                resolve(processResult);

            } catch (error) {
                this.onFinished();
                reject(error);
            }
        });
        return prom;
    }

    protected processFile(): Promise<boolean> {
        const prom = new Promise<boolean>(async (resolve, reject) => {
            while (this.currentOffset < this.fileSize) {
                if (this.cancelled) {
                    this.onReadComplete.next(true);
                    resolve(false);
                }

                const blob = await this.readFileChunk();
                // If we got a good blob, invoke the given handler to processs it
                if (blob) {
                    const chunkHandleSuccess = await this.chunkHandler(blob);
                } else {
                    console.log(`Failed to read chunk.  currentOffset: ${this.currentOffset}`);
                    resolve(false);
                }

                this._chunkIndex++;

                if (this.currentOffset >= this.fileSize) {
                    this.onReadComplete.next(true);
                    resolve(true);
                }
            }
        });
        return prom;
    }

    protected initFile(): Promise<boolean> {
        const prom = new Promise<boolean>((resolve, reject) => {
            (<any>window).requestFileSystem((<any>window).PERSISTENT, 0, (fs: any) => {
                (<any>window).resolveLocalFileSystemURL(this.filepath, (fileEntry: any) => {
                    fileEntry.file( (file: any) => {
                        this.file = file;
                        this._fileSize = this.file.size;
                        console.log(`Got file: ${JSON.stringify(file)}. fileSize: ${this.file.size}`);
                        resolve(true);
                    }, (fileErr: any) => { console.log(`fileError: ${fileErr}`); reject(this.handleError(`Error getting fileEntry for file: ${this.filepath}`, fileErr)); });
                }, (resolveErr: any) => { console.log(`resolveErr: ${resolveErr}`); reject(this.handleError(`Error resolving file: ${this.filepath}`, resolveErr)); });
            }, (fsErr: any) => { console.log(`fsErr: ${fsErr}`); reject(this.handleError('Error getting persistent filesystem!', fsErr)); });
        });

        return prom;
    }

    protected readFileChunk(): Promise<Blob> {
        const prom = new Promise<Blob>((resolve, reject) => {
            const reader = new FileReader();
            const blob = this.file.slice(this.currentOffset, this.currentOffset + this.chunkSize);
            reader.onload = async (e) => {
                resolve(await this.readEventHandler(e));
            };
            try {
                reader.readAsArrayBuffer(blob);
            } catch (error) {
                console.log(error);
                throw error;
            }
        });

        return prom;
    }

    protected readEventHandler(readEvent: any): Promise<Blob> {

        if (readEvent.target.error == null) {
            const blob = new Blob([new Uint8Array(<ArrayBuffer>readEvent.target.result)], { type: this.contentType });
            this.currentOffset += blob.size;
//            console.log(`FileChunkReader currentOffset: ${this.currentOffset}, blob.size: ${blob.size}`);
            return Promise.resolve(blob);
        } else {
            this.onFinished();
            this.handleError(`Failed to read chunk at offset: ${this.currentOffset} in file '${this.filepath}'`, readEvent.target.error);
            return;
        }
    }

    protected handleError(msg: string, e: any) {
        const errorMsg = `${msg}; Error code: ${e.code}, ${JSON.stringify(e)}`;
        console.log(errorMsg);
        throw new Error(errorMsg);
    }

    protected onFinished() {
        this._done = true;
        if (this.completeSubscr && ! this.completeSubscr.closed) {
            this.completeSubscr.unsubscribe();
        }
    }

}
