export interface ILogger {
    log( message: string );
    info( message: string );
    error( message: string );
    warn( message: string );
    debug( message: string );
}
