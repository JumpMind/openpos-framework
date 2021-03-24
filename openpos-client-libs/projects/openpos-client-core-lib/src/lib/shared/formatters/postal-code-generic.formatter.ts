import { IFormatter } from './formatter.interface';
import {isNumeric} from "rxjs/internal-compatibility";

export class PostalCodeGenericFormatter implements IFormatter {
    static readonly FILTER_REGEXS_CA = [
        /[abceghjklmnprstvxyABCEGHJKLMNPRSTVXY]/,
        /[0-9]/,
        /[abceghjklmnprstvwxyzABCEGHJKLMNPRSTVWXYZ]/,
        /[0-9]/,
        /[abceghjklmnprstvwxyzABCEGHJKLMNPRSTVWXYZ]/,
        /[0-9]/
    ];
    static readonly FILTER_REGEXS_US = [
        /[0-9]/,
        /[0-9]/,
        /[0-9]/,
        /[0-9]/,
        /[0-9]/
    ];
    allowKey(key: string, newValue: string): boolean {
        const len = newValue.length;

        return PostalCodeGenericFormatter.FILTER_REGEXS_CA[len - 1].test(newValue[len - 1]) ||
            PostalCodeGenericFormatter.FILTER_REGEXS_US[len - 1].test(newValue[len - 1 ]);
    }

    formatValue(value: string): string {
        if (!value) {
            return '';
        } else {
            let returnValue = value;
            if (returnValue && (returnValue.length > PostalCodeGenericFormatter.FILTER_REGEXS_CA.length ||
                    returnValue.length > PostalCodeGenericFormatter.FILTER_REGEXS_US.length)) {
                returnValue = returnValue.substring(0, PostalCodeGenericFormatter.FILTER_REGEXS_CA.length);
            }

            const replacementValues = [];
            for (let i = 0; i < returnValue.length; i++) {
                let allowChar;
                if (PostalCodeGenericFormatter.FILTER_REGEXS_CA[i].test(returnValue.charAt(i)) || PostalCodeGenericFormatter.FILTER_REGEXS_US[i].test(returnValue.charAt(i))) {
                    allowChar = returnValue.charAt(i);
                } else {
                    allowChar = '';
                }
                // Check each char against allowed pattern and stop once invalid char is encountered
                if (!allowChar && i < returnValue.length - 1) {
                    break;
                }
                replacementValues.push(allowChar);
            }
            returnValue = replacementValues.join('');
            return returnValue;
        }
    }

    unFormatValue(value: string): string {
        return value;
    }
}
