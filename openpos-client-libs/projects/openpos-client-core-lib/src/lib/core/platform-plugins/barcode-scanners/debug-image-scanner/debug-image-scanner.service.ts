import { Observable } from "rxjs";
import { ImageScanner, ScanData, ScannerViewRef } from "../scanner";

export class DebugImageScanner implements ImageScanner {
    name(): string {
        return "Debug";
    }

    beginScanning(view: ScannerViewRef): Observable<ScanData> {
        return new Observable(() => {
            let subscription = view.viewChanges().subscribe({
                next: value => {
                    console.log('scanner view container changed', value)
                }
            });

            return () => {
                subscription.unsubscribe();
                console.log('scan data stopped');
            };
        });
    }
}
