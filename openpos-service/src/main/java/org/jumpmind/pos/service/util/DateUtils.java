package org.jumpmind.pos.service.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {
    
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
    
}
