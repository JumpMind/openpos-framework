import { IFormatter } from './formatter.interface';

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

        if (len > 0 && len <= PostalCodeGenericFormatter.FILTER_REGEXS_CA.length && PostalCodeGenericFormatter.FILTER_REGEXS_CA[0].test(newValue[0])) {
            return this.patternMatch(newValue, 'ca', newValue.length-1);
        } else if (len > 0 && len <= PostalCodeGenericFormatter.FILTER_REGEXS_US.length && PostalCodeGenericFormatter.FILTER_REGEXS_US[0].test(newValue[0])) {
            return  this.patternMatch(newValue, 'us', newValue.length-1);
        } else {
            return false;
        }
    }

    formatValue(value: string): string {
        let reg: string;
        if (!value) {
            return '';
        } else {
            if (PostalCodeGenericFormatter.FILTER_REGEXS_CA[0].test(value[0])) {
                reg = 'ca';
            } else if (PostalCodeGenericFormatter.FILTER_REGEXS_US[0].test(value[0])) {
                reg = 'us';
            }
            let returnValue = value;
            if (returnValue.length > PostalCodeGenericFormatter.FILTER_REGEXS_CA.length && PostalCodeGenericFormatter.FILTER_REGEXS_CA[0].test(returnValue[0])) {
                returnValue = returnValue.substring(0, PostalCodeGenericFormatter.FILTER_REGEXS_CA.length);
            } else if (returnValue.length > PostalCodeGenericFormatter.FILTER_REGEXS_US.length && PostalCodeGenericFormatter.FILTER_REGEXS_US[0].test(returnValue[0])) {
                returnValue = returnValue.substring(0, PostalCodeGenericFormatter.FILTER_REGEXS_US.length);
            }

            const replacementValues = [];
            for (let i = 0; i < returnValue.length; i++) {
                let allowChar: string;
                if (reg == 'ca') {
                    allowChar = PostalCodeGenericFormatter.FILTER_REGEXS_CA[i].test(returnValue.charAt(i)) ? returnValue.charAt(i) : '';
                } else if (reg == 'us') {
                    allowChar = PostalCodeGenericFormatter.FILTER_REGEXS_US[i].test(returnValue.charAt(i)) ? returnValue.charAt(i) : '';
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

    patternMatch(value: string, reg: string, index: number): boolean {
        let isPatternMatched: boolean;
        if (index > 0) {
            if (reg == 'ca') {
                if (PostalCodeGenericFormatter.FILTER_REGEXS_CA[index].test(value[index])) {
                    isPatternMatched = this.patternMatch(value, reg, index - 1);
                } else {
                    isPatternMatched =  false;
                }
            } else if (reg == 'us') {
                if (PostalCodeGenericFormatter.FILTER_REGEXS_US[index].test(value[index])) {
                    isPatternMatched = this.patternMatch(value, reg, index - 1);
                } else {
                    isPatternMatched =  false;
                }
            }
        } else {
            if (reg == 'ca') {
                isPatternMatched =  PostalCodeGenericFormatter.FILTER_REGEXS_CA[0].test(value.charAt(0));
            } else if (reg == 'us') {
                isPatternMatched =  PostalCodeGenericFormatter.FILTER_REGEXS_US[0].test(value.charAt(0));
            } else {
                isPatternMatched =  false;
            }
        }

        return isPatternMatched;
    }
}
