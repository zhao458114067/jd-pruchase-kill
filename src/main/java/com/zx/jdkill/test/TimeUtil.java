package com.zx.jdkill.test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author : zhaoxu
 */
public class TimeUtil {
    public static final String PATTERN_YMD_HMS = "yyyy-MM-dd HH:mm:ss";
    public static final String PATTERN_YMD = "yyyy-MM-dd HH:mm:ss";
    public static final String PATTERN_TARGET_TIME = "ss mm HH dd MM ? yyyy";


    /**
     * time时间戳转Date
     *
     * @param time
     * @return
     */
    public static Date timeToDate(String time, String pattern) {
        SimpleDateFormat sdfTime = new SimpleDateFormat(pattern);
        String str = sdfTime.format(Long.valueOf(time));
        try {
            Date date = sdfTime.parse(str);
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 日期字符串转String型Time
     *
     * @param date
     * @return
     */
    public static String stringToTime(String date, String pattern) {
        SimpleDateFormat sdfTime = new SimpleDateFormat(pattern);
        try {
            Date dateDate = sdfTime.parse(date);
            return String.valueOf(dateDate.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将日期转化为对应的格式
     * @param date
     * @param dateFormat
     * @return
     */
    public static String formatDateByPattern(Date date, String dateFormat){
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        String formatTimeStr = null;
        if (date != null) {
            formatTimeStr = sdf.format(date);
        }
        return formatTimeStr;
    }
}
