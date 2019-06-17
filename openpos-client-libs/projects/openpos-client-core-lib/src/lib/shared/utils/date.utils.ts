export class DateUtils {
    /**
     * Returns the relative positions of the date parts within the given date pattern.
     * E.g., for YYYY/MM/DD, returns yearPos = 0, monthPos = 1, dayOfMonthPos = 2
     * @param dateFormatPattern 
     */
    static datePartPositions(dateFormatPattern: string): DatePartPositions {
        const formatUpper = dateFormatPattern.toUpperCase().replace(/\//g, '');
        let lastChar = '';
        const partPos = [];
        for (const v of formatUpper) {
            if (v !== lastChar) {
                partPos.push(v);
            }
            lastChar = v;
        }

        return {
            monthPos: partPos.indexOf('M'),
            dayOfMonthPos: partPos.indexOf('D'),
            yearPos: partPos.indexOf('Y')
        };

    }

    /**
     * Returns a 4 digit year given the month and year
     * @param month numeric month of the year (0-11)
     * @param year 1, 2, or 4 digit year
     */
    static normalizeDateYear(month: number, dayOfMonth: number, year: number): number {
//                console.log(`year: ${year}, month: ${month}, dayOfMonth: ${dayOfMonth}`);
        let returnYear = year;
        const strYear = year + '';
        if (strYear.length === 1 || strYear.length === 2 ) {
            const curDate = new Date();
            const curYear = curDate.getFullYear();
            // Make assumptions about year in same way that Java SimpleDateFormat does
            const lowerYear = curYear - 80;
            const upperYear = curYear + 20;
            const curCentury = curYear - (curYear % 100);
            let century = curCentury;
            if (
                curCentury + year > upperYear ||
                (curCentury + year === upperYear && month > curDate.getMonth() + 1) ||
                (curCentury + year === upperYear && month === curDate.getMonth() + 1 && dayOfMonth > curDate.getDate())
            ) {
                century = curCentury - 100;
            }
            returnYear = century + year;
        } else if (strYear.length === 4) {
            returnYear = year;
        }

        return returnYear;
    }
}

export interface DatePartPositions {
    monthPos: number;
    dayOfMonthPos: number;
    yearPos: number;
}
