package com.psu.hpa.format;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Locale;

/**
 * The HPA currency formatter.  Dollar sign, commas, and no decimal places.
 */
public class CurrencyFormatter extends org.springframework.format.number.CurrencyFormatter {
	/**
	 * Instantiates a new currency formatter.
	 */
	public CurrencyFormatter() {
		super();
		setFractionDigits(0);
	}

	/**
	 * Formats the number as currency.
	 *
	 * @param number the number
	 * @param locale the locale
	 * @return the string
	 */
	@Override
	public String print(Number number, Locale locale) {
		if(number == null || number.intValue() == 0) {
			return "$";
		} else {
			return super.print(number, locale);
		}
	}

	/**
	 * Parses the formatted currency.
	 *
	 * @param text the text
	 * @param locale the locale
	 * @return the big decimal
	 * @throws ParseException the parse exception
	 */
	@Override
	public BigDecimal parse(String text, Locale locale) throws ParseException {
		if(text.length() == 0 || text.equals("$")) {
			return new BigDecimal("0");
		} else {
			return super.parse(text,  locale);
		}
	}
}
