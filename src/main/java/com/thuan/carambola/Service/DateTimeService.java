package com.thuan.carambola.Service;

import java.time.*;

public class DateTimeService {
    public static boolean isAfterTime(int hour, int minute){
        return LocalTime.of(hour, minute).isAfter(LocalTime.now());
    }

    public static boolean isAfterTime(String sHour, String sMinute){
        int hour = Integer.parseInt(sHour);
        int minute = Integer.parseInt(sMinute);
        return isAfterTime(hour, minute);
    }
    public static int getHour(Instant instant)
    {
        return instant.atZone(ZoneOffset.UTC).getHour();
    }
    public static int getMinute(Instant instant)
    {
        return instant.atZone(ZoneOffset.UTC).getMinute();
    }
    public static LocalDate get(Instant instant)
    {
        ZoneId zone =  ZoneId.of("Asia/Ho_Chi_Minh");
        return LocalDate.ofInstant(instant, zone);
    }
}
