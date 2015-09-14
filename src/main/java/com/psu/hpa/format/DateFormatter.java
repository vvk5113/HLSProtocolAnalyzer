package com.psu.hpa.format;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.format.Formatter;

import com.psu.hpa.format.DateFormatterException;

/**
 * Date formatter that is more strict than SimpleDateFormat with lenient=false.
 */
public class DateFormatter implements Formatter<Date> {

	/** The pattern. */
	private Pattern pattern = Pattern.compile("(\\d\\d)/(\\d\\d)/(\\d\\d\\d\\d)");

	/** The calendar. */
	private Calendar calendar = GregorianCalendar.getInstance();

	/** The date format (used for printing only). */
	private DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

	@Override
	public String print(Date object, Locale locale) {
		return dateFormat.format(object);
	}

	@Override
	public Date parse(String text, Locale locale) throws ParseException {
		Date result = null;
		if(text != null) {
			text = text.trim();
			if(!text.isEmpty()) {
				Matcher matcher = pattern.matcher(text);
				if(!matcher.matches()) {
					throw new DateFormatterException("DateFormatter.badFormat", "Date must be MM/DD/YYYY");
				} else {
					int month = Integer.parseInt(matcher.group(1));
					int day = Integer.parseInt(matcher.group(2));
					int year = Integer.parseInt(matcher.group(3));
					if(month < 1 || month > 12) {
						throw new DateFormatterException("DateFormatter.badMonth", "The first two numbers (MM) must be between 1 and 12.");
					}
					if(day < 1) {
						throw new DateFormatterException("DateFormatter.dayMin", "The second two numbers (DD) must not be 00 or negative.");
					} else if((month == 4 || month == 6 || month == 9 || month == 11) && day > 30) {
						throw new DateFormatterException("DateFormatter.dayMax30", "For the month (MM) 04, 06, 09, or 11 the \"DD\" numbers entered must be between 01 and 30.");
					} else if(day > 31) {
						throw new DateFormatterException("DateFormatter.dayMax31", "The second two numbers (DD) entered must be between 01 and 31, for the months \"MM\" 01, 03, 05, 07, 08, 10 and 12.");
					}
					if(month == 2) {
						boolean isLeapYear = ((GregorianCalendar)calendar).isLeapYear(year);
						if(isLeapYear && day > 29) {
							throw new DateFormatterException("DateFormatter.dayMax29", "For the month (MM) 02, the \"DD\" numbers entered must be between 01 and 29 on a leap year.");
						} else if(!isLeapYear && day > 28) {
							throw new DateFormatterException("DateFormatter.dayMax28", "For the month (MM) 02, the \"DD\" numbers entered must be between 01 and 28 when not on a leap year.");
						}
					}
					if(year < 1) {
						throw new DateFormatterException("DateFormatter.badYear", "The year entered \"YYYY\" must not be 0000.");
					}
					calendar.set(Calendar.YEAR, year);
					calendar.set(Calendar.MONTH, month - 1);
					calendar.set(Calendar.DAY_OF_MONTH, day);
					result = calendar.getTime();
				}
			}
		}
		return result;
	}
}
