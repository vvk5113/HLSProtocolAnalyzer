package com.psu.hpa.util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Date utilities.
 */
public class DateUtils {
	
	/** The calendar used for comparisons. */
	private static final Calendar calendar = new GregorianCalendar();
	
	/**
	 * Check if the year, month, and day of two Dates equal, ignoring the time of day.
	 *
	 * @param date1 the first date
	 * @param date2 the second date
	 * @return true if they dates are equal
	 */
	public static synchronized boolean datesEqual(Date date1, Date date2) {
		// Don't dereference null dates.
		if(date1 == null || date2 == null) {
			return date1 == date2;
		}
		
		calendar.setTime(date1);
		int year1 = calendar.get(Calendar.YEAR);
		int month1 = calendar.get(Calendar.MONTH);
		int day1 = calendar.get(Calendar.DAY_OF_MONTH);
		
		calendar.setTime(date2);
		int year2 = calendar.get(Calendar.YEAR);
		int month2 = calendar.get(Calendar.MONTH);
		int day2 = calendar.get(Calendar.DAY_OF_MONTH);

		return (year1 == year2) && (month1 == month2) && (day1 == day2);
	}
}
