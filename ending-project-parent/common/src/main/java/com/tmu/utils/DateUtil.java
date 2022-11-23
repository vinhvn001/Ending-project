package com.tmu.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

    public static Date getDateTime(String strTime, String formatDate) {
        SimpleDateFormat formatter = new SimpleDateFormat(formatDate);
        Date date = null;
        try {
            date = formatter.parse(strTime);
        } catch (ParseException e) {
        }

        return date;
    }
}
