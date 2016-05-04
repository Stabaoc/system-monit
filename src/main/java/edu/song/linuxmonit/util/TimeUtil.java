package edu.song.linuxmonit.util;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class TimeUtil {
	public static String timestampToDate(long timeLong){
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//定义格式，不显示毫秒
		Timestamp timestamp = new Timestamp(timeLong);//获取时间
		String dateString = df.format(timestamp);
		return dateString;
	}
	
	public static String timestampToDay(long timeLong){
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//定义格式，不显示毫秒
		Timestamp timestamp = new Timestamp(timeLong);//获取时间
		String dateString = df.format(timestamp);
		return dateString;
	}
	public static String timestampToHour(long timeLong){
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH");//定义格式，不显示毫秒
		Timestamp timestamp = new Timestamp(timeLong);//获取时间
		String dateString = df.format(timestamp);
		return dateString;
	}
	public static String timestampToHourAsId(long timeLong){
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-ddHH");//定义格式，不显示毫秒
		Timestamp timestamp = new Timestamp(timeLong);//获取时间
		String dateString = df.format(timestamp);
		return dateString;
	}
	public static long dateToTimestamp(String timeString) {
		Timestamp ts = new Timestamp(System.currentTimeMillis()); 
        ts = Timestamp.valueOf(timeString);   
        return ts.getTime();
	}
}
