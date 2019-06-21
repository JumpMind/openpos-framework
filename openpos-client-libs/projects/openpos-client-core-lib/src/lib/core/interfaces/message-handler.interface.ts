export interface IMessageHandler<T> {
    handle(message: T);
}
