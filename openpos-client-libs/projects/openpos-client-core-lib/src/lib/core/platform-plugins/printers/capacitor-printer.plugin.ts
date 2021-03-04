import { Injectable } from '@angular/core';
import { IPrinter } from './printer.interface';


declare module '@capacitor/core' {
    interface PluginRegistry {
        Print: PrintPlugin;
    }
}

interface PrintPlugin {
    print(args: { html: string }): Promise<void>;
}

import { Capacitor, Plugins as CapacitorPlugins } from '@capacitor/core';

@Injectable()
export class CapacitorPrinterPlugin implements IPrinter {
    name(): string {
        return 'cap-printer';
    }

    isSupported(): boolean {
        return Capacitor.isNative && Capacitor.isPluginAvailable('Print');
    }

    print(html: string) {
        CapacitorPlugins.Print.print({ html });
    }
}
