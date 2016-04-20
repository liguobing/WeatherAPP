package cn.lixyz.weatherapp.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 返回当前日期
 * Created by LGB on 2016/4/20.
 */
public class DateUtil {
    public static String getDate() {

        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(date);
    }
}
