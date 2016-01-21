package com.ztm;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtil {


	public static void main(String[] args) {
		
		Calendar ca = Calendar.getInstance();
		System.out.println(ca.getTime().toLocaleString());
		

	}
	static SimpleDateFormat ddf = new SimpleDateFormat( 
			"MMM d HH:mm" , Locale.US); 
	public static Date getDatefromStrNoWeek(String newtime)
	{
		
		Date sdate = null;
		try {
			sdate = ddf.parse(newtime);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return sdate;
	}
	static SimpleDateFormat cdf = new SimpleDateFormat( 
	"MM-dd HH:mm"); 
	public static String formatDateToStrNoWeek(Date newtime)
	{
		
		if(newtime==null) return null;
		String ss = cdf.format(newtime);
		return ss;
	}
	
	
	static SimpleDateFormat blogdf = new SimpleDateFormat( 
	"M月d日 HH:mm"); 
	public static String formatDateForBlog(Date newtime)
	{
		
		if(newtime==null) return null;
		String ss = blogdf.format(newtime);
		return ss;
	}
	
	
	
	static SimpleDateFormat bdf = new SimpleDateFormat( 
			"EEE MMM d HH:mm:ss yyyy" , Locale.US); 
	public static Date getDatefromStr(String newtime)
	{
		
		Date sdate = null;
		try {
			sdate = bdf.parse(newtime);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return sdate;
	}
	static SimpleDateFormat df = new SimpleDateFormat( 
	"yyyy年M月d日 HH:mm:ss");
	public static String formatDateToStr(Date newtime)
	{
		
		if(newtime==null) return null;
		String ss = df.format(newtime);
		return ss;
	}

}
