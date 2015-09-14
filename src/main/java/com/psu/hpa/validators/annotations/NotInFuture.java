package com.psu.hpa.validators.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import com.psu.hpa.validators.NotInFutureValidator;

@Target({METHOD, FIELD})
@Retention(RUNTIME)
@Constraint(validatedBy = NotInFutureValidator.class)
public @interface NotInFuture {
  String message() default "{NotInFuture}";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}