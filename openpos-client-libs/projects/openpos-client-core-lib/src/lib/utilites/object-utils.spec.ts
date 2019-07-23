import { getValue } from './object-utils';

describe('object-utils', () => {

    describe('getValue', () => {
        it('returns undefined when obj is undefined', () => {
            expect(getValue(undefined, 'foo')).toBeUndefined();
        });

        it('returns value when single property value', () => {
            expect(getValue({foo: 'bar'}, 'foo')).toEqual('bar');
        });

        it('returns object when value is an object', () => {
            expect(getValue({foo: {bar: 'zar'}}, 'foo')).toEqual({bar: 'zar'});
        });

        it('returns nested object when value is nested', () => {
            expect(getValue({foo: {bar: 'zar'}}, 'foo.bar')).toEqual('zar');
        });

        it('returns array when value is an array', () => {
            expect(getValue({foo: {bar: ['baz', 'zar']}}, 'foo.bar')).toEqual(['baz', 'zar']);
        });

        it('returns undefined when value is nested and no property', () => {
            expect(getValue({foo: {bar: 'zar'}}, 'foo.bah')).toBeUndefined();
        });

        it('returns undefined when value is undefined', () => {
            expect(getValue({foo: 'bar'}, undefined)).toBeUndefined();
        });

        it('returns null when object has null for value', () => {
            expect(getValue({foo: {bar: null}}, 'foo.bar')).toBeNull();
        });

        it('returns undefined when value starts with a .', () => {
            expect(getValue({foo: 'bar'}, '.foo')).toBeUndefined();
        });

        it('returns undefined when value ends with a .', () => {
            expect(getValue({foo: 'bar'}, 'foo.')).toBeUndefined();
        });

        it('return undefined when object attribute is a primitive', () => {
            expect(getValue({foo: 'bar'}, 'foo.bar')).toBeUndefined();
        });

    });
});
