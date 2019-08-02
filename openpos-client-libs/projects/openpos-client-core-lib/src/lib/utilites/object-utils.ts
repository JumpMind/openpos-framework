
export function getValue(obj: any, value: string): any {
    if (isJsObject(obj) && isString(value)) {
        const index = value.indexOf('.');
        if (index > 0) {
            const splitValue = value.substring(0, index);
            const remainingValue = value.substr(index + 1, value.length);
            const newObject = obj[splitValue];
            if (isJsObject(newObject)) {
                return getValue(newObject, remainingValue);
            }
            return undefined;
        }
        else {
            return obj[value];
        }
    }
}

function isJsObject(obj: any): boolean {
    return (obj !== null && obj !== undefined && typeof obj === 'object' && !Array.isArray(obj));
}

function isString(obj: any): boolean {
    return (obj !== null  && obj !== undefined && typeof obj === 'string');
}
