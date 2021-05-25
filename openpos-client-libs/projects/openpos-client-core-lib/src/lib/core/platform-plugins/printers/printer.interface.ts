import { Observable } from "rxjs";

export interface IPrinter {
    name(): string;

    isSupported(): boolean;

    print(html: String): Observable<void>;
}
