import { StringListFilterPipe } from './string-list-filter.pipe';

describe('StringListFilterPipe', () => {
    const pipe = new StringListFilterPipe();

    const testList = [
        'tHing1',
        '$%PoppA',
        'aThing'
    ];

    it('filtering NULL list with NULL should be null', () => {
      expect(pipe.transform(null, null)).toBeNull();
    });

    it('filtering NULL list with ABC should be null', () => {
        expect(pipe.transform(null, 'ABC')).toBeNull();
    });

    it('filtering EMPTY list with NULL should be an null', () => {
        expect(pipe.transform([], null)).toBeNull();
    });

    it('filtering Undefined list with Undefined should be null', () => {
        expect(pipe.transform(undefined, undefined)).toBeNull();
      });

      it('filtering Undefined list with ABC should be null', () => {
          expect(pipe.transform(undefined, 'ABC')).toBeNull();
      });

      it('filtering EMPTY list with Undefined should be null', () => {
          expect(pipe.transform([], undefined)).toBeNull();
      });

    it('filter a list with an empty string should be an empty list', () => {
        expect(pipe.transform(testList, '').length).toBeFalsy();
    });

    it('filter the testList with pOP should result in [$%PoppA]', () => {
        expect(pipe.transform(testList, 'pOP')).toEqual(['$%PoppA']);
    });

    it('filter the testList with a should result in [$%PoppA, aThing]', () => {
        expect(pipe.transform(testList, 'a')).toEqual(['$%PoppA', 'aThing']);
    });

  });
