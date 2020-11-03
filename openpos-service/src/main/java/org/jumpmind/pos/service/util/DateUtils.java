package org.jumpmind.pos.service.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.jumpmind.pos.service.PosServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DateUtils {
    private static final Logger logger = LoggerFactory.getLogger(DateUtils.class);

    private static final String ISO_DATE_TIME_MILLIS = "yyyy-MM-dd HH:mm:ss.SSS";
    private static final String ISO_DATE_TIME_MILLIS_T = "yyyy-MM-dd'T'HH:mm:ss.SSS";
    private static final String ISO_DATE_TIME_SECONDS = "yyyy-MM-dd HH:mm:ss";
    private static final String ISO_DATE_TIME_SECONDS_T = "yyyy-MM-dd'T'HH:mm:ss";
    private static final String ISO_DATE = "yyyy-MM-dd";
    
    private DateUtils() {
    }
    
    private static String[] FORMATS = new String[] {
            ISO_DATE_TIME_MILLIS,
            ISO_DATE_TIME_MILLIS_T,
            ISO_DATE_TIME_SECONDS,
            ISO_DATE_TIME_SECONDS_T,
            ISO_DATE
    };
    
    public static Date parseDateTimeISO(String date) {
        if (!StringUtils.isEmpty(date)) {
            Exception originalException = null;
            for (String format : FORMATS) {
                if (date.length() == format.length() || date.length() == format.length()-2) {                    
                    SimpleDateFormat dateFormat = new SimpleDateFormat(format);
                    try {
                        return dateFormat.parse(date);
                    } catch (ParseException ex) {
                        originalException = ex;
                    }
                }
            }
            if (originalException != null) {
                throw new PosServerException("Failed to parse date as ISO format: '" + date + "'", originalException);                
            } else {                
                throw new PosServerException("Failed to parse date as ISO format: '" + date + "'");
            }
        } else {
            return null;            
        }
    }
    
    public static String formatDateTimeISO(Date date) {
        if (date != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return dateFormat.format(date);
        } else {
            return "null";            
        }
    }

    public static long daysBetween(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance(); 
        cal1.setTime(date1);
        Calendar cal2 = Calendar.getInstance(); 
        cal2.setTime(date2);
        
        long diffDays = (cal2.getTimeInMillis()-cal1.getTimeInMillis()) / (24 * 60 * 60 * 1000);
        return diffDays;
    }

    public static String changeFormat(String value, String existingFormat, String newFormat) {
        if(StringUtils.isBlank(value)) {
            return value;
        }

        try {
            Date date = new SimpleDateFormat(existingFormat).parse(value);
            return new SimpleDateFormat(newFormat).format(date);
        } catch (ParseException e) {
            logger.warn("Unable to change format of date \"{}\" from \"{}\" to \"{}\"", value, existingFormat, newFormat, e);
            return value;
        }
    }
}
