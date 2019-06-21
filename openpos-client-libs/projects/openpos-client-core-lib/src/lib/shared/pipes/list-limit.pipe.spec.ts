import { ListLimitPipe } from './list-limit.pipe';

describe( 'ListLimitPipe', () => {

    const pipe = new ListLimitPipe();

    const testList = [
        'tHing1',
        '$%PoppA',
        'aThing'
    ];

    it('Should return null when given a null list', () => {
        expect(pipe.transform(null, 5)).toBeNull();
    });

    it('Should return null when given a null length', () => {
        expect(pipe.transform(testList, null)).toBeNull();
    });

    it('Should return an empty list when given a length of 0', () => {
        expect(pipe.transform(testList, 0).length).toBeFalsy();
    });

    it('Should return [tHing1] when given a length of 1', () => {
        expect(pipe.transform(testList, 1)).toEqual(['tHing1']);
    });

    it('Should return [tHing1, $%PoppA, aThing] when given a length of 3', () => {
        expect(pipe.transform(testList, 3)).toEqual(testList);
    });
});
