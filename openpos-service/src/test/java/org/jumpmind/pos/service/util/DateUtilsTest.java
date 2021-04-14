package org.jumpmind.pos.service.util;

import org.jumpmind.pos.service.PosServerException;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


public class DateUtilsTest {

    private final Date MOCKED_DATE = org.apache.commons.lang3.time.DateUtils.truncate(new GregorianCalendar(1996, Calendar.JULY, 8, 12, 15, 45).getTime(), Calendar.SECOND);
    private final Date MOCKED_DATE_PLUS_DAY = org.apache.commons.lang3.time.DateUtils.truncate(new GregorianCalendar(1996, Calendar.JULY, 7, 12, 15, 45).getTime(), Calendar.SECOND);

    @Rule
    public final ExpectedException EXPECTED_EX = ExpectedException.none();

    @Test
    public void testParseISODateParseException() {
        EXPECTED_EX.expect(PosServerException.class);
        DateUtils.parseDateTimeISO("1996-07-08T12:99:999.618");
    }

    @Test
    public void testParseISOIncorrectDateParseException() {
        EXPECTED_EX.expect(PosServerException.class);
        DateUtils.parseDateTimeISO("ABCD-07-08 12:15:45.618");
    }

    @Test
    public void testParseISONull() {
        Assert.assertNull(DateUtils.parseDateTimeISO(""));
    }

    @Test
    public void testParseISODateTimeMillis() {
        Date result = truncateParsedDateForComparing(DateUtils.parseDateTimeISO("1996-07-08 12:15:45.618"));
        Assert.assertEquals(MOCKED_DATE, result);
    }

    @Test
    public void testParseISODateTimeMillisT() {
        Date result = truncateParsedDateForComparing(DateUtils.parseDateTimeISO("1996-07-08T12:15:45.618"));
        Assert.assertEquals(MOCKED_DATE, result);
    }

    @Test
    public void testParseISODateTimeMillisTNotInUTC() {
        Date result = truncateParsedDateForComparing(DateUtils.parseDateTimeISO("1996-07-08T12:15:45.618-04:00"));
        Assert.assertEquals(MOCKED_DATE, result);
    }

    @Test
    public void testParseISODateTimeSeconds() {
        Date result = truncateParsedDateForComparing(DateUtils.parseDateTimeISO("1996-07-08 12:15:45"));
        Assert.assertEquals(MOCKED_DATE, result);
    }

    @Test
    public void testParseISODateTimeSecondsT() {
        Date result = truncateParsedDateForComparing(DateUtils.parseDateTimeISO("1996-07-08T12:15:45"));
        Assert.assertEquals(MOCKED_DATE, result);
    }

    @Test
    public void testFormatDateTimeISO() {
        String result = DateUtils.formatDateTimeISO(MOCKED_DATE);
        Assert.assertEquals("1996-07-08 12:15:45", result);
    }

    @Test
    public void testFormatDateTimeISONull() {
        String result = DateUtils.formatDateTimeISO(null);
        Assert.assertEquals(DateUtils.SAFE_NULL_STR_VALUE, result);
    }

    @Test
    public void testDaysBetween() {
        long daysBetween = DateUtils.daysBetween(MOCKED_DATE_PLUS_DAY, MOCKED_DATE);
        Assert.assertEquals(1, daysBetween);
    }

    @Test
    public void testChangeFormat() {
        String result = DateUtils.changeFormat("1996-07-08T12:15:45.618", "yyyy-MM-dd'T'HH:mm:ss.SSS", "MM/dd/yyyy");
        Assert.assertEquals("07/08/1996", result);
    }

    @Test
    public void testChangeFormatBlank() {
        String result = DateUtils.changeFormat("", "", "");
        Assert.assertEquals("", result);
    }

    @Test
    public void testChangeFormatParseException() {
        String INCORRECT_FORMATTED_DATE = "1996-07-08T12:95:459.9999";
        String result = DateUtils.changeFormat(INCORRECT_FORMATTED_DATE, "yyyy-MM-dd HH:mm:ss.SSS", "MM/dd/yyyy");
        Assert.assertEquals(INCORRECT_FORMATTED_DATE, result);
    }

    private Date truncateParsedDateForComparing(Date parsedDate) {
        return org.apache.commons.lang3.time.DateUtils.truncate(parsedDate, Calendar.SECOND);
    }

}
