import { IFormatter } from './formatter.interface';

export class PostalCodeCAFormatter implements IFormatter {
    static readonly FILTER_REGEXS = [
        /[abceghjklmnprstvxyABCEGHJKLMNPRSTVXY]/,
        /[0-9]/,
        /[abceghjklmnprstvwxyzABCEGHJKLMNPRSTVWXYZ]/,
        /[0-9]/,
        /[abceghjklmnprstvwxyzABCEGHJKLMNPRSTVWXYZ]/,
        /[0-9]/
    ];

    allowKey(key: string, newValue: string): boolean {
        const len = newValue.length;
        if (len > 0 && len <= PostalCodeCAFormatter.FILTER_REGEXS.length) {
            return PostalCodeCAFormatter.FILTER_REGEXS[len - 1].test(newValue[len - 1]);
        }
        return false;
    }

    formatValue(value: string): string {
        let returnValue = value;
        if (returnValue && returnValue.length > PostalCodeCAFormatter.FILTER_REGEXS.length) {
            returnValue = returnValue.substring(0, PostalCodeCAFormatter.FILTER_REGEXS.length);
        }

        const replacementValues = [];
        for (let i = 0; i < returnValue.length; i++) {
            const allowChar = PostalCodeCAFormatter.FILTER_REGEXS[i].test(returnValue.charAt(i)) ? returnValue.charAt(i) : '';
            // Check each char against allowed pattern and stop once invalid char is encountered
            if (! allowChar && i < returnValue.length - 1) {
                break;
            }
            replacementValues.push(allowChar);
        }
        returnValue = replacementValues.join('');
        return returnValue;
    }

    unFormatValue(value: string): string {
        return value;
    }
}
