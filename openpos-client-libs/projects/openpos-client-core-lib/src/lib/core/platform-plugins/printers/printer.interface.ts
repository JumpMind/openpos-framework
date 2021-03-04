export interface IPrinter {
    name(): string;

    isSupported(): boolean;

    print(html: String);
}
