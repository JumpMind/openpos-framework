import { IFormatter } from "./iformatter";

export class MoneyFormatter implements IFormatter {

    locale?: string;

    formatValue(value: string): string {
        if (!value) return '';

        const i = value.toString().indexOf('.');
        if( i > 0 ){
            const d = value.toString().slice(i+1);
            switch( d.length) {
                case 0:
                    return `$${value}00`;
                case 1:
                    return `$${value}0`;
                case 2:
                    return `$${value}`;
                default:
                    return `$${value.toString().slice(0, i+2)}`;
            }

        } else {
            return `$${value}.00`
        }
    }

    unFormatValue(value: string): string {
        let n = value.replace(/[^(\d)]/g, "");

        let i = 0;
        while (i < n.length && n[i] === '0') {
            ++i;
        }

        n = n.slice(i, n.length);

        if (n.length > 2) {
            let dec = n.slice(n.length - 2, n.length);
            let whole = n.slice(0, n.length - 2);

            return `${whole}.${dec}`;
        }

        if (n.length === 1) {
            return `0.0${n}`;
        } else if (n.length === 0) {
            return '';
        }

        return `0.${n}`;
    }

    allowKey(key: string, newValue: string): boolean {
        return this.keyFilter.test(key);
    }

    private keyFilter = /[0-9\ | \.]/;
}