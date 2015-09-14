package com.psu.hpa.application;

import java.lang.reflect.Field;

import javax.validation.constraints.Max;
import javax.validation.constraints.Size;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.servlet.support.BindStatus;
import org.springframework.web.servlet.support.RequestContext;

import com.psu.hpa.application.annotation.MaxLength;
import com.psu.hpa.format.annotation.CurrencyFormat;

/**
 * Tool for retrieving annotations from the model objects.
 */
@Service("hpaFieldInfo")
public class FieldInfo {

	/** The logger. */
	private final Logger log = LoggerFactory.getLogger(getClass());

	/**
	 * Gets the max length of a field, defined by a {@literal @}Size(max=), {@literal @}CurrencyFormat(max=), or {@literal @}Max() constraint.
	 *
	 * @param context the request context
	 * @param path the path into the model
	 * @return the max length, or -1 if no max length
	 */
	public int getMaxLength(RequestContext context, String path) {
		int result = -1;
		// Bind the object containing the field.  It has the annotations.
		int fieldSeparatorIndex = path.lastIndexOf('.');
		if(fieldSeparatorIndex <= 0 || fieldSeparatorIndex >= path.length() - 1) {
			log.error("Invalid field path " + path);
		} else {
			String instancePath = path.substring(0, fieldSeparatorIndex);
			String fieldName = path.substring(fieldSeparatorIndex + 1);
			// Spring won't bind to a top level instance, so extract it from the model in that case.
			Object instance = null;
			int firstSeparatorIndex = path.indexOf('.');
			if(firstSeparatorIndex == fieldSeparatorIndex) {
				instance = context.getModel().get(instancePath);
			} else {
				BindStatus bindStatus = context.getBindStatus(instancePath);
				instance = bindStatus.getActualValue();
			}
			if(instance != null) {
				// Now we can look up the annotation.
				Field field = ReflectionUtils.findField(instance.getClass(), fieldName);
				if(field == null) {
					log.error("Unable to find field " + path);
				} else {
					MaxLength maxLength = AnnotationUtils.getAnnotation(field, MaxLength.class);
					if(maxLength != null) {
						result = maxLength.value();
					}
					if(result == -1) {
						Size size = AnnotationUtils.getAnnotation(field, Size.class);
						if(size != null) {
							result = size.max();
						}
					}
					if(result == -1) {
						CurrencyFormat currencyFormat = AnnotationUtils.getAnnotation(field, CurrencyFormat.class);
						if(currencyFormat != null) {
							result = currencyFormat.max();
						}
					}
					if(result == -1) {
						Max max = AnnotationUtils.getAnnotation(field, Max.class);
						if(max != null) {
							result = String.valueOf(max.value()).length();
						}
					}
				}
			}
		}
		return result;
	}
}
