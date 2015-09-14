package com.psu.hpa.format.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/** Apply the HPA currency formatter to a model field. */
@Target(value={METHOD,FIELD,PARAMETER})
@Retention(value=RUNTIME)
public @interface CurrencyFormat {
	int max() default 10;
}