package sun.project.toparking.util;


import java.text.SimpleDateFormat;
import java.util.Calendar;


/**
 * 时间格式工具类
 */
public final class TimeUtil {
    public static void main(String[] args) {
        System.out.println(getStandardTimeToDay());
    }
    /**
     * 时间格式： yyyy-MM-dd HH:mm:ss
     */
    public static String getStandardTimeToSecond(){
        Calendar calendar=Calendar.getInstance();
        SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time=sdf.format(calendar.getTime());
        return time;
    }
    /**
     * 时间格式：yyyy-MM-dd HH:mm
     */
    public static String getStandardTimeToMinute(){
        Calendar calendar=Calendar.getInstance();
        SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String time=sdf.format(calendar.getTime());
        return time;
    }
    /**
     * 时间格式：yyyy.MM.dd
     */
    public static String getStandardTimeToDay(){
        Calendar calendar=Calendar.getInstance();
        SimpleDateFormat sdf =new SimpleDateFormat("yyyy.MM.dd");
        String time=sdf.format(calendar.getTime());
        return time;
    }
    /**
     * 时间格式：yyyyMMdd
     */
    public static String getStandardTimeToDayTwo(){
        Calendar calendar=Calendar.getInstance();
        SimpleDateFormat sdf =new SimpleDateFormat("yyyyMMdd");
        String time=sdf.format(calendar.getTime());
        return time;
    }
}
