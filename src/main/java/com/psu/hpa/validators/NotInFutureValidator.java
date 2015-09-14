package com.psu.hpa.validators;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.psu.hpa.validators.annotations.NotInFuture;

public class NotInFutureValidator implements ConstraintValidator<NotInFuture, Date> {

	private static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

	@Override
	public void initialize(NotInFuture constraint) {
	}

	@Override
	public boolean isValid(Date date, ConstraintValidatorContext context) {
		// Null dates have nothing to check.
		if(date == null) {
			return true;
		}

		// Check that the string value of the given date is less than or equal to the
		// string value of the current date.
		//
		// Why are we doing this?  Defect #877 reports that the current date is being
		// rejected as being in the future before 8 AM.  We cannot reproduce this.
		// The QA installation's date and time appear correct when we print them out.
		// So we're comparing them printed out.
		//
		// - kvance
		String thenDate;
		String nowDate;
		synchronized(DATE_FORMAT) {
			thenDate = DATE_FORMAT.format(date);
			nowDate = DATE_FORMAT.format(new Date());
		}
		if(thenDate.compareTo(nowDate) == 1) {
			return false;
		} else {
			return true;
		}
	}
}
