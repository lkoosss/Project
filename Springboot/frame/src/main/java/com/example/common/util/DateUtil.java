package com.example.common.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


/**
 *
 *
 * @author BoKyu Park
 * @since 1.0
 * @see Date
 */
public class DateUtil {
    private DateUtil() {
        throw new IllegalStateException("DateUtil class");
    }

    public enum CalendarType {
        YEAR(Calendar.YEAR),
        MONTH(Calendar.MONTH),
        WEEK_OF_YEAR(Calendar.WEEK_OF_YEAR),
        WEEK_OF_MONTH(Calendar.WEEK_OF_MONTH),
        DATE(Calendar.DATE),
        HOUR(Calendar.HOUR),
        HOUR_OF_DAY(Calendar.HOUR_OF_DAY),
        MINUTE(Calendar.MINUTE),
        SECOND(Calendar.SECOND)
        ;

        private int calType;

        CalendarType(int calType) {
            this.calType = calType;
        }

        public int getCalType() {
            return this.calType;
        }
    }

    /**
     * <pre>
     * getDate
     * 입력한 날짜를 입력받은 형식으로 반환
     * </pre>
     *
     * @param date
     * @param format (yyyy-MM-dd HH:mm:ss.SSS)
     * @return
     */
    public static String getDate(Date date, String format){
        SimpleDateFormat f = new SimpleDateFormat(format, Locale.KOREA);
        return date != null ? f.format(date) : "";
    }

    /**
     * <pre>
     * getDate
     * 입력받은 long값을 입력받은 형식으로 반환
     * </pre>
     *
     * @param longDate
     * @param format (yyyy-MM-dd HH:mm:ss.SSS)
     * @return
     */
    public static String getDate(long longDate, String format) {
        return longDate == 0L ? "" : getDate(new Date(longDate), format);
    }

    /**
     * <pre>
     * getDate
     * 오늘날짜를 입력받은 형식으로 반환
     * </pre>
     *
     * @param format (yyyy-MM-dd HH:mm:ss.SSS)
     * @return
     */
    public static String getDate(String format) {
        return getDate(new Date(), format);
    }

    /**
     * <pre>
     * getCurrentDate
     * 현재 날짜를 format형식으로 반환
     * </pre>
     *
     * @param format
     * @return
     */
    public static String getCurrentDate(String format) {
        return getDate(format);
    }

    /**
     * <pre>
     * toDate
     * 입력된 문자열을 입력된 포멧 형태로 파싱하여 Date반환
     * </pre>
     *
     * @param strDate	(ex> 20210817)
     * @param format	(ex> yyyyMMdd)
     * @return
     * @throws IllegalArgumentException
     */
    public static Date strToDate(String strDate, String format) throws IllegalArgumentException {
        Date date = null;
        if (strDate != null && format != null) {
            try {
                DateFormat dateFormat = new SimpleDateFormat(format, Locale.KOREA);
                date = dateFormat.parse(strDate.trim());

                return (strDate.trim().equals(dateFormat.format(date)) ? date : null);
            } catch (ParseException e) {
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * <pre>
     * difference
     * 두 날짜의 차이를 계산
     * </pre>
     *
     * @param date1	(날짜 1)
     * @param date2	(날짜 2)
     * @param calendarType	(비교될 필드, Year, Month, Date, Hour, Minute, Second...)
     * @return
     */
    public static long difference(Date date1, Date date2, CalendarType calendarType) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();

        cal1.setTime(date1);
        cal2.setTime(date2);

        int field = calendarType.getCalType();
        if ( field == Calendar.YEAR || field == Calendar.WEEK_OF_YEAR ) {
            return (cal2.get(field) - cal1.get(field));

        } else if ( field == Calendar.MONTH ) {
            return ((cal2.get(Calendar.YEAR) * 12 + cal2.get(field)) - (cal1.get(Calendar.YEAR) * 12 + cal1.get(field)));

        } else {
            long time1 = cal1.getTimeInMillis() + cal1.getTimeZone().getRawOffset();
            long time2 = cal2.getTimeInMillis() + cal2.getTimeZone().getRawOffset();

            switch (field) {
                case Calendar.DATE:
                    time1 /= 86400000;
                    time2 /= 86400000;
                    break;
                case Calendar.HOUR_OF_DAY:
                    time1 /= 3600000;
                    time2 /= 3600000;
                    break;
                case Calendar.MINUTE:
                    time1 /= 60000;
                    time2 /= 60000;
                    break;
                case Calendar.SECOND:
                    time1 /= 1000;
                    time2 /= 1000;
                    break;
                default :
                    break;
            }

            return time2 - time1;
        }
    }

    /**
     * <pre>
     * getCustomDate
     * 주어진 Date의 원하는 시간 단위로 가감하여 반환
     * </pre>
     *
     * @param date		(날짜)
     * @param calType	(Year, Month, Date, Hour, Minute, Second...)
     * @param val		(가감할 값)
     * @param format	(날짜 포멧)
     * @return
     */
    public static String getCustomDate(Date date, CalendarType calType, int val, String format) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(calType.getCalType(), val); // Calendar.Date, -1

        DateFormat df = new SimpleDateFormat(format, Locale.KOREA);
        return df.format(cal.getTime());
    }


    /**
     * <pre>
     * getCustomDate
     * 현재 날짜에 원하는 시간 단위로 가감하여 반환
     * </pre>
     *
     * @param calType	(Year, Month, Date, Hour, Minute, Second...)
     * @param val		(가감할 값)
     * @param format	(날짜 포멧)
     * @return
     */
    public static String getCustomDate(CalendarType calType, int val, String format) {
        return getCustomDate(new Date(), calType, val, format);
    }

}



