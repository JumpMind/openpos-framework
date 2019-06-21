
// This method assumes you do not have any cyclic references. For the case we plan on using it
// there should be no issues with that since it currently is only being used to copy the JSON
// screen objects into their local versions with out triggering a reference change
export function deepAssign( target: any, src: any): any {

    // if target is undefined or null we'll break out and return src
    if ( target === null || typeof target === 'undefined' ) {
        return _doCopy(src);
    }

    // if src is not an object or array return it.
    if ( _isPrimitive(src) ) {
        return src;
    }

    // if we are an object we'll iterater our keys, copy our values and return target
    if ( typeof src === 'object' ) {
        if ( Array.isArray(src) ) {
            if ( Array.isArray(target) ) {
                (target as Array<any>).length = 0;
            } else {
                target = [];
            }
        }

        // if the target is an array and the new src isn't. we'll just copy over it
        if ( Array.isArray(target) && !Array.isArray(src) ) {
            return _doCopy(src);
        }

        Object.getOwnPropertyNames(src).forEach( prop => {
            target[prop] = deepAssign( target[prop], src[prop]);
        });

        Object.getOwnPropertyNames(target).forEach( prop => {
            if ( !src.hasOwnProperty(prop) ) {
                delete target[prop];
            }
        });
        return target;
    }
}

// Returns the value if primitive, or will create a array or object and copy all of the values.
function _doCopy( src: any ): any {
    if ( _isPrimitive(src) ) {
        return src;
    }
    if ( Array.isArray(src) ) {
        const arr = [];
        (src as Array<any>).forEach( item => {
            arr.push(_doCopy(item));
        });
        return arr;
    }
    if ( typeof src === 'object' ) {
        const obj = {};
        Object.getOwnPropertyNames(src).forEach( prop => {
            obj[prop] = _doCopy(src[prop]);
        });
        return obj;
    } else {
        return undefined;
    }
}

// Returns true if src is null, undefiend, string, number or boolean.
function _isPrimitive( src: any ): boolean {
    return src === null || typeof src === 'undefined' || typeof src === 'string' || typeof src === 'number' || typeof src === 'boolean';
}
