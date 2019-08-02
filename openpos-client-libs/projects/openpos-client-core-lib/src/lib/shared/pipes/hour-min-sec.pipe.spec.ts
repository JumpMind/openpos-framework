import { HourMinSecPipe } from './hour-min-sec.pipe';


describe('HourMinSecPipe', () => {
    const sut = new HourMinSecPipe();
    describe('transform', () => {
        function doTestCase( input: number, expected: string) {
            it(`should convert ${input} to ${expected}`, () => {
                const result = sut.transform(input);
                expect(result).toBe(expected);
            });
        }

        doTestCase(0, '00:00:00');
        doTestCase(30, '00:00:30');
        doTestCase(59, '00:00:59');
        doTestCase(60, '00:01:00');
        doTestCase(59.5, '00:01:00');
        doTestCase(59.4, '00:00:59');
        doTestCase(60.3, '00:01:00');
        doTestCase(3600, '01:00:00');
        doTestCase(3663, '01:01:03');

    });
});
