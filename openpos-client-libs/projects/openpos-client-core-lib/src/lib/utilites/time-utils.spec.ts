import {getFormattedTime, getHourMinSeconds, TimeUnitLabels, Time} from './time-utils';

describe( 'time-utils', () => {
    describe('getHourMinSeconds()', () => {
        function doTestCase( input: number, expected: Time) {
            it(`should convert ${input} to ${expected}`, () => {
                const result = getHourMinSeconds(input);
                expect(result).toEqual(expected);
            });
        }

        doTestCase(0, new Time(0, 0, 0));
        doTestCase(30, new Time(0, 0, 30));
        doTestCase(59, new Time(0, 0, 59));
        doTestCase(60, new Time(0, 1, 0));
        doTestCase(59.5, new Time(0, 1, 0));
        doTestCase(59.4, new Time(0, 0, 59));
        doTestCase(60.3, new Time(0, 1, 0));
        doTestCase(3600, new Time(1,0,0));
        doTestCase(3663, new Time(1, 1,3));
    });

    describe( 'getFormattedTime()', () => {
        const timeLabels = new TimeUnitLabels('hour', 'hours', 'minute', 'minutes', 'second', 'seconds', 'Now');
        let format = "before HH mm ss after";
        function doTestCase( f: string, input: Time, expected: String) {
            it( `should format ${input} to ${expected}`, () => {
                const result = getFormattedTime(f, input, timeLabels);
                expect(result).toEqual(expected);
            });
        }

        doTestCase(format, new Time(0,0,0), 'Now');
        doTestCase(format, new Time(0,0,30), 'before 30 seconds after');
        doTestCase(format, new Time(0,0,1), 'before 1 second after');
        doTestCase(format, new Time(0,1,0), 'before 1 minute after');
        doTestCase(format, new Time(0,30,0), 'before 30 minutes after');
        doTestCase(format, new Time(1,0,0), 'before 1 hour after');
        doTestCase(format, new Time(15,0,0), 'before 15 hours after');
        doTestCase(format, new Time(2,29,12), 'before 2 hours 29 minutes 12 seconds after');

        format = "before HH mm after";
        doTestCase(format, new Time(0,0,30), 'Now');
    });
});