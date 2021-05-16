package com.jit.dyy.dosleep;

import org.junit.Test;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
        String str = "2021-05-04 00:00:00:000";

        Date date = new Date();
        Instant instant = date.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();

        LocalDateTime localDateTime = instant.atZone(zoneId).toLocalDateTime();
        System.out.println("Date = " + date);
        System.out.println("LocalDateTime = " + localDateTime);
//
//        getDateString(new Date());
//
//        getStringDate("2021-5-14");
    }
    private Date getStringDate(String dateStr) {
        Date date = new Date();
        //注意format的格式要与日期String的格式相匹配
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            date = sdf.parse(dateStr);
            System.out.println("AAAdate.toString():"+date.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return date;
    }

    private String getDateString(Date date) {
        String dateStr = "";
        //format的格式可以任意
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            dateStr = sdf.format(date);
            System.out.println("AAAdateStr:"+dateStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dateStr;
    }

}