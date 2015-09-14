package com.psu.hpa.messagesource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

/**
 * HPA message source. 
 */
public class HPAMessageSource implements MessageSource {
	/** The message source. */
	private ReloadableResourceBundleMessageSource messageSource;
	
	/** Pattern to match an array index. */
	private Pattern arrayIndexPattern = Pattern.compile("\\[(\\d+)\\]");
	
	/**
	 * Instantiates a new HPA message source.
	 */
	public HPAMessageSource() {
		this.messageSource = new ReloadableResourceBundleMessageSource();
	}

	/**
	 * Sets the basename.
	 *
	 * @param basename the new basename
	 */
	public void setBasename(String basename) {
		messageSource.setBasename(basename);
	}
	
	/**
	 * Sets the basenames.
	 *
	 * @param basenames the new basenames
	 */
	public void setBasenames(String... basenames) {
		messageSource.setBasenames(basenames);
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	// Overrides
    ////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public String getMessage(MessageSourceResolvable resolvable, Locale locale) throws NoSuchMessageException {		
		resolvable = indexifyResolvable(resolvable, locale);
		// Indexify the arguments also.
		if(resolvable.getArguments() != null) {
			Object[] arguments = resolvable.getArguments();
			for(int i = 0; i < arguments.length; i++) {
				if(arguments[i] instanceof MessageSourceResolvable) {
					MessageSourceResolvable originalArgument = (MessageSourceResolvable)arguments[i];
					MessageSourceResolvable indexedArgument = indexifyResolvable(originalArgument, locale);
					if(originalArgument != indexedArgument) {
						arguments[i] = indexedArgument;
					}
				}
			}
		}
		String result = messageSource.getMessage(resolvable, locale); 
		return result;
	}

	@Override
	public String getMessage(String code, Object[] args, Locale locale) throws NoSuchMessageException {
		String result = getIndexedMessage(code, locale);
		if(result == null) {
			result = messageSource.getMessage(code, args, locale);
		}
		return result;
	}

	@Override
	public String getMessage(String code, Object[] args, String defaultMessage, Locale locale) {
		String result = getIndexedMessage(code, locale);
		if(result == null) {
			result = messageSource.getMessage(code, args, defaultMessage, locale);
		}
		return result;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	// Label lookup
    ////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Gets the label for a path.
	 *
	 * @param path the path
	 * @param prefix the prefix to filter out of resolvable codes, or null if no filtering necessary
 	 * @param resolvable the resolvable codes, if given
	 * @param locale the locale
	 * @return the label, or null if no label could be found
	 */
	public String getLabel(String path, String prefix, MessageSourceResolvable resolvable, Locale locale) {
		String result = null;

		// Use the resolvable codes if available, otherwise check only the given path.
		String[] codes;
		if(resolvable == null || resolvable.getCodes() == null) {
			codes = new String[] { path };
		} else {
			codes = resolvable.getCodes();
		}
		for(String fieldCode : codes) {
			// Filter out prefix (e.g. NotNull.) from resolvable code before prepending label.
			if(prefix != null && fieldCode.startsWith(prefix)) {
				fieldCode = fieldCode.substring(prefix.length());
			}
			//String code = AbstractBaseValidator.LABEL_PREFIX + fieldCode;
			String LABEL_PREFIX = "";
			String code = LABEL_PREFIX + fieldCode;
			result = messageSource.getMessage(code, null, null, locale);
			if(result == null) {
				// Check for an indexed version.
				result = getIndexedMessage(code, locale);
			}
			if(result != null) {
				break;
			}
		}
		return result;
	}
	
	/**
	 * Looks up a message replacing the first [NUMBER] in the code with [n] where NUMBER is an integer.
	 * The message is expected to take one argument, the ordinal.
	 *
	 * @param code the code
	 * @param locale the locale
	 * @return the message, or null if not found
	 */
	public String getIndexedMessage(String code, Locale locale) {
		String result = null;
		Matcher m = arrayIndexPattern.matcher(code); 
		if(m.find()) {
			// Replace the index with [n] to generate the code.
			String indexedCode = code.substring(0, m.start()) + "[n]" + code.substring(m.end());
			int index = Integer.valueOf(m.group(1));
			String printableIndex = String.valueOf(index + 1); // Covert to 1-based.
			result = messageSource.getMessage(indexedCode, new Object[] { printableIndex }, null, locale);
		}
		return result;
	}
	
	/**
	 * Modify a MessageSourceResolvable to use the [n] versions of the code.
	 *
	 * @param resolvable the resolvable
	 * @param locale the locale
	 * @return the new message source resolvable
	 */
	public MessageSourceResolvable indexifyResolvable(MessageSourceResolvable resolvable, Locale locale) {
		MessageSourceResolvable result = resolvable;
		
		for(String code : resolvable.getCodes()) {
			if(messageSource.getMessage(code, resolvable.getArguments(), null, locale) != null) {
				// Found a non-indexed version.  No need.
				break;
			} else {
				Matcher m = arrayIndexPattern.matcher(code); 
				if(m.find()) {
					// Replace the index with [n] to generate the code.
					String indexedCode = code.substring(0, m.start()) + "[n]" + code.substring(m.end());
					int index = Integer.valueOf(m.group(1));
					String printableIndex = String.valueOf(index + 1); // Covert to 1-based.
					Object[] args = prependArgument(printableIndex, resolvable.getArguments());
					if(messageSource.getMessage(indexedCode, args, null, locale) != null) {
						result = new DefaultMessageSourceResolvable(new String[] { indexedCode }, args, resolvable.getDefaultMessage());
						break;
					}
				}
			}
		}
		
		return result;
	}
			
	/**
	 * Return a new array with this value inserted at the beginning.
	 *
	 * @param value the value to prepend
	 * @param args the args
	 * @return the updated args
	 */
	private Object[] prependArgument(Object value, Object[] args) {
		Object[] result;
		
		if(args == null || args.length == 0) {
			result = new Object[] { value };
		} else {
			ArrayList<Object> list = new ArrayList<Object>(Arrays.asList(args));
			list.add(0, value);
			result = list.toArray(new Object[list.size()]);
		}
		
		return result;
	}
}
