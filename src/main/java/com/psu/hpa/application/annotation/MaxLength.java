package com.psu.hpa.application.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Corresponds to the maxlength attribute of an input field.  Meant only as a suggestion, not for validation.
 */
@Target(value={FIELD})
@Retention(value=RUNTIME)
public @interface MaxLength {
	int value();
}
