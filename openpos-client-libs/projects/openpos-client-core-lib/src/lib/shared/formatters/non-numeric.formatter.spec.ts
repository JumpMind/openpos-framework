import { NonNumericFormatter } from './non-numeric.formatter';

describe('NonNumericFormatter Test', () => {
    let formatter: NonNumericFormatter;

    beforeEach(() => { formatter = new NonNumericFormatter(); });

    it('#ensure numerics are not accepted as a keypress', () => {
        expect(formatter.allowKey('0', 'a0')).toBe(false);
    });

    it('#ensure values with numerics have the numerics removed', () => {
        expect(formatter.formatValue('0a1b23c456')).toBe('abc');
    });

    it('#ensure special characters are not removed', () => {
        // tslint:disable-next-line:quotemark
        expect(formatter.formatValue("1 Bill $mith's car%")).toBe(" Bill $mith's car%");
    });

});
