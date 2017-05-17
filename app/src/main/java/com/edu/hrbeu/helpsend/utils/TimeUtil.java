package com.edu.hrbeu.helpsend.utils;


import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtil {


    public static String getDateToString(long time) {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date d = new Date(time);
        return sf.format(d);
    }
}
