package com.psu.hpa.validators;

import com.psu.hpa.messagesource.HPAMessageSource;
import com.psu.hpa.util.RatifiedDate;

import org.apache.commons.lang.StringUtils;
import org.hibernate.validator.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.ReflectionUtils;
import org.springframework.validation.*;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpSession;
import javax.validation.constraints.NotNull;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Abstract base validator for HPA objects.
 */
public abstract class AbstractBaseValidator implements Validator {
	private Logger log = LoggerFactory.getLogger(getClass());

	/** Message code prefix for labels. */
	public static final String LABEL_PREFIX = "";

	/** Message code prefix for custom required message. */
	public static final String REQUIRED_REFIX = "validation.required.";

	/** Message code prefix for custom invalid message. */
	public static final String INVALID_REFIX = "validation.invalid.";

	/** regex for validating name */
	public static final String VALID_NAME_CHARACTERS_PATTERN = "[A-Za-z '-]+";

	/** regex for validating address */
	public static final String VALID_CHARACTERS_PATTERN = "[A-Za-z0-9 '-]+";

	/** The message source. */
	protected MessageSource messageSource;

	/** The SPeL parser. */
	protected ExpressionParser spelParser;

	/** The date format. */
	protected SimpleDateFormat dateFormat;

	/** The default bean validator. */
	protected Validator defaultValidator;

	// Used internally for date comparisons.
	private Calendar calendar1;
	private Calendar calendar2;

	/**
	 * Instantiates a new abstract base validator.
	 *
	 * @param valueLookup the value lookup
	 * @param messageSource the message source
	 * @param defaultValidator the default validator
	 */
	public AbstractBaseValidator(MessageSource messageSource, Validator defaultValidator) {
		this.messageSource = messageSource;
		this.defaultValidator = defaultValidator;
		this.spelParser = new SpelExpressionParser();
		this.calendar1 = Calendar.getInstance();
		this.calendar2 = Calendar.getInstance();
		this.dateFormat = new SimpleDateFormat("MM/dd/yyyy");
	}

	@Override
	abstract public boolean supports(Class<?> clazz);

	@Override
	abstract public void validate(Object target, Errors errors);

	/**
	 * Validate required fields.
	 *
	 * @param requiredPaths the required paths, as returned by the controller
	 * @param target the target of validation
	 * @param errors the errors to update
	 * @param specialCasePattern the special case pattern.  Paths matching this pattern will not
	 *        get errors.  They will be added to specialCasePaths instead.
	 * @param specialCasePaths the special case paths to update
	 */
	protected void validateRequiredFields(Collection<String> requiredPaths, Object target, Errors errors, Pattern specialCasePattern, Set<String> specialCasePaths) {
		// Quick abort if required paths are not available.
		if(requiredPaths == null) {
			return;
		}

		// Validate required fields.
		EvaluationContext context = new StandardEvaluationContext(target);
		for(String fullPath : requiredPaths) {
			// Get the path relative to the target.
			String path = fullPath.replaceFirst("[^.]+\\.", "");

			// Use safe field traversal to deal with nulls in the middle of paths
			Expression expression = spelParser.parseExpression(safePath(path));
			Object value;
			try {
				value = expression.getValue(context);
			} catch(SpelEvaluationException e) {
				log.error("Failed to evaluate path {}", expression.getExpressionString());
				throw e;
			}
			Field field = getFieldForPath(path, context);

			boolean valueIsOk = false;
			if(value != null) {
				if(value instanceof String && StringUtils.isEmpty((String)value)) {
					// Treat blank string as not OK.
					valueIsOk = false;
				} else if(value instanceof Long && ((Long)value).longValue() == 0) {
					// Treat zero currency value as not OK.
					valueIsOk = false;
				} else if(value instanceof BigDecimal && value.equals(new BigDecimal("0").setScale(((BigDecimal)value).scale()))) {
					// Treat zero currency value as not OK.
					valueIsOk = false;
				} else {
					// It's not null or blank or anything else.
					valueIsOk = true;
				}
			}

			// If this field already has NotNull or NotBlank we can skip it, since the
			// JSR-303 validator should have already picked it up.
			if(!valueIsOk && field != null && !(value instanceof Long)) {
				valueIsOk = (field.isAnnotationPresent(NotNull.class) || field.isAnnotationPresent(NotBlank.class));
			}

			if(!valueIsOk) {
				// Reject only if it doesn't match the special case pattern.
				if(specialCasePattern == null || !specialCasePattern.matcher(path).matches()) {
					rejectRequired(errors, path);
				} else {
					specialCasePaths.add(path);
				}
			}
		}
	}

	/**
	 * Validate required fields.
	 *
	 * @param requiredPaths the required paths, as returned by the controller
	 * @param target the target of validation
	 * @param errors the errors to update
	 */
	protected void validateRequiredFields(Collection<String> requiredPaths, Object target, Errors errors) {
		validateRequiredFields(requiredPaths, target, errors, null, null);
	}


	/**
	 * Validate an object using the default bean validator.
	 *
	 * @param path the path
	 * @param target the target
	 * @param errors the errors to add to
	 */
	protected void validateObject(String path, Object target, Errors errors) {
		// Do nothing on null target.
		if(target == null) {
			return;
		}

		// Validate the object.
        WebDataBinder dataBinder = new WebDataBinder(target, path);
        dataBinder.setValidator(defaultValidator);
        dataBinder.validate();
        BindingResult result = dataBinder.getBindingResult();

        // Add all of the errors.
        errors.pushNestedPath(path);
        try {
        	for(ObjectError error : result.getGlobalErrors()) {
        		errors.reject(error.getCode(), error.getArguments(), error.getDefaultMessage());
        	}
        	for(FieldError error : result.getFieldErrors()) {
        		errors.rejectValue(error.getField(), error.getCode(), error.getArguments(), error.getDefaultMessage());
        	}
        } finally {
        	errors.popNestedPath();
        }
	}

	/**
	 * Gets an object from the current session.
	 *
	 * @param attributeName the session attribute name
	 * @return the object, or null if not found
	 */
	protected Object getSessionObject(String attributeName) {
	    ServletRequestAttributes attr = (ServletRequestAttributes)RequestContextHolder.currentRequestAttributes();
	    HttpSession session = attr.getRequest().getSession(false);
	    return session.getAttribute(attributeName);
	}
	
	/**
	 * Gets the current session.
	 *
	 * @return the current session
	 */
	protected HttpSession getSession() {
	    ServletRequestAttributes attr = (ServletRequestAttributes)RequestContextHolder.currentRequestAttributes();
	    HttpSession session = attr.getRequest().getSession(false);
	    return session;
	}

	/**
	 * Checks if is date1 is equal to date2.
	 *
	 * @param date1 the date1
	 * @param date2 the date2
	 * @return true, if is date1 is equal to date2
	 */
	protected boolean isDateEqual(RatifiedDate date1, RatifiedDate date2) {
		if(date1 == null || !date1.isRatified() || date2 == null || date2.isRatified()) {
			return false;
		}
		setCalendars(date1, date2);
		return (calendar1.compareTo(calendar2) == 0);
	}

	/**
	 * Checks if is date1 is less than date2.
	 *
	 * @param date1 the date1
	 * @param date2 the date2
	 * @return true, if is date1 is less than date2
	 */
	protected boolean isDateLessThan(RatifiedDate date1, RatifiedDate date2) {
		if(date1 == null || !date1.isRatified() || date2 == null || date2.isRatified()) {
			return false;
		}
		setCalendars(date1, date2);
		return (calendar1.compareTo(calendar2) < 0);
	}

	/**
	 * Checks if is date1 is greater than date2.
	 *
	 * @param date1 the date1
	 * @param date2 the date2
	 * @return true, if is date1 is greater than date2
	 */
	protected boolean isDateGreaterThan(RatifiedDate date1, RatifiedDate date2) {
		if(date1 == null || !date1.isRatified() || date2 == null || !date2.isRatified()) {
			return false;
		}
		setCalendars(date1, date2);
		return (calendar1.compareTo(calendar2) > 0);
	}

	/**
	 * Gets a message source resolvable for a path with a prefix.
	 *
	 * @param errors the current errors
	 * @param prefix the prefix
	 * @param path the path
	 * @return the resolvable
	 */
	protected MessageSourceResolvable getResolvableForPathWithPrefix(Errors errors, String prefix, String path) {
		List<String> paths = new ArrayList<String>();

		String target = errors.getObjectName();
		String nestedPath = errors.getNestedPath();

		// Full path with target object and nested path.
		if(!StringUtils.isEmpty(target)) {
			String fullPath;
			if(!StringUtils.isEmpty(nestedPath)) {
				fullPath = target + '.' + nestedPath + '.' + path;
			} else {
				fullPath = target + '.' + path;
			}
			paths.add(prefix + fullPath);
		}

		// Full path with only nested path.
		if(!StringUtils.isEmpty(nestedPath)) {
			String fullPath = nestedPath + '.' + path;
			paths.add(prefix + fullPath);
		}

		// Relative path.
		paths.add(prefix + path);

		String[] codeArray = new String[paths.size()];
		String defaultMessage = prefix + path;
		DefaultMessageSourceResolvable result = new DefaultMessageSourceResolvable(paths.toArray(codeArray), null, defaultMessage);
		return result;
	}

	/**
	 * Gets a message source resolvable for a path.
	 *
	 * @param errors the current errors
	 * @param path the path
	 * @return the resolvable
	 */
	protected MessageSourceResolvable getResolvableForPath(Errors errors, String path) {
		return getResolvableForPathWithPrefix(errors, "", path);
	}

	/**
	 * Gets a message code for a path with a prefix.
	 *
	 * @param errors the current errors
	 * @param prefix the prefix
	 * @param path the path
	 * @return the message code, or null if not found
	 */
	protected String getCodeForPathWithPrefix(Errors errors, String prefix, String path) {
		String result = null;
		MessageSourceResolvable resolvable = getResolvableForPathWithPrefix(errors, prefix, path);
		for(String code : resolvable.getCodes()) {
			String message = messageSource.getMessage(code, null, null, LocaleContextHolder.getLocale());
			if(message != null) {
				result = code;
				break;
			}
		}
		return result;
	}

	/**
	 * Gets the label for a path from messages.
	 *
	 * @param errors the current errors
	 * @param path the path
	 * @return the label
	 */
	protected String getLabel(Errors errors, String path) {
		HPAMessageSource hpaMessageSource = (HPAMessageSource)messageSource;
		MessageSourceResolvable resolvable = getResolvableForPath(errors, path);

		String result = hpaMessageSource.getLabel(path, null, resolvable, LocaleContextHolder.getLocale());
		if(result == null) {
			log.warn("Could not find label for path {}", path);
			result = path; // Can't find anything.  Return the path.
		}
		return result;
	}

	/**
	 * Add an error for a required path.
	 *
	 * @param errors the errors to add to
	 * @param path the path
	 */
	protected void rejectRequired(Errors errors, String path) {
		// Build a default message.
		String label = getLabel(errors, path);
		String defaultMessage = "The " + label + " is required.";

		// Check for a custom message first.

		String code = getCodeForPathWithPrefix(errors, REQUIRED_REFIX, path);
		if(code != null) {
			errors.rejectValue(
					path,
					code,
					defaultMessage);
		} else {
			// Otherwise, use the standard message.
			errors.rejectValue(
					path,
					"validation.modelPath.isRequired",
					new Object[] { label },
					defaultMessage);
		}
	}

	/**
	 * Add an error for an invalid path
	 *
	 * @param errors the errors to add to
	 * @param path the path
	 */
	protected void rejectInvalid(Errors errors, String path) {
		// Build a default message.
		String label = getLabel(errors, path);
		String defaultMessage = "The " + label + " is invalid.";

		// Check for a custom message first.
		String code = getCodeForPathWithPrefix(errors, INVALID_REFIX, path);
		if(code != null) {
			errors.rejectValue(
					path,
					code,
					defaultMessage);
		} else {
			errors.rejectValue(
					path,
					"validation.modelPath.isInvalid",
					new Object[] { label },
					defaultMessage);
		}
	}

	/** Set calendar1 to date1 and calendar2 to date2, with no time of day. */
	private void setCalendars(Date date1, Date date2) {
		calendar1.setTime(date1);
		calendar2.setTime(date2);
		clearCalendarTimesOfDay();
	}

	/** Clear the times of day on both internal calendars. */
	private void clearCalendarTimesOfDay() {
		clearCalendarTimesOfDay(calendar1, calendar2);
	}

	/** Clear the times of day on some calendars. */
	private static void clearCalendarTimesOfDay(Calendar... calendars) {
		for(Calendar cal : calendars) {
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
		}
	}

	/** Return a SPeL path with '.'s replaced with '.?'s. */
	private String safePath(String path) {
		return path.replace(".", "?.");
	}

	/** Get the Field information for a path. */
	private Field getFieldForPath(String path, EvaluationContext context) {
		Field result = null;

		// Evaluate the parent object so we can access its fields.
		String parts[] = path.split("\\.(?=[^.]+$)");
		if(parts.length == 2) {
			String parentPath = parts[0];
			String fieldName = parts[1];
			Object parent = null;
			try {
				parent = spelParser.parseExpression(safePath(parentPath)).getValue(context);
			} catch(SpelEvaluationException e) {
				// Grandparent is null?
			}
			if(parent != null) {
				result = ReflectionUtils.findField(parent.getClass(), fieldName);
			}
		}

		return result;
	}
}
