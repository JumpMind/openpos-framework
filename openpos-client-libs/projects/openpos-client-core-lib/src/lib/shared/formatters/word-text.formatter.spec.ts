import { WordTextFormatter } from "./word-text.formatter";


describe('WordTextFormatter Test', () => {
    let formatter: WordTextFormatter;

    beforeEach(() => { formatter = new WordTextFormatter(); });

    it('#ensure special characters are not accepted as a keypress', () => {
        expect(formatter.allowKey('%', '%')).toBe(false);
        expect(formatter.allowKey('=', '=')).toBe(false);
        expect(formatter.allowKey('!', '!')).toBe(false);
        expect(formatter.allowKey('_', '_')).toBe(false);
        
    });

    it('#ensure WordText characters are accepted as a keypress', () => {
        expect(formatter.allowKey('a', 'a')).toBe(true);
        expect(formatter.allowKey('A', 'A')).toBe(true);
        expect(formatter.allowKey('q', 'q')).toBe(true);
        expect(formatter.allowKey('7', '7')).toBe(true);
        expect(formatter.allowKey('0', '0')).toBe(true);
        expect(formatter.allowKey('3', '3')).toBe(true);
    });

});
