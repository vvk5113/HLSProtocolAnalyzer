package com.psu.hpa.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

/**
 * HPA properties access.
 */
public class HPAProperties {
	
	/** Properties key for the tax ID encryption key. */
	public static final String ENCRYPTION_KEY_TAXID = "encryption.key.taxid"; 

	/** Properties key for the email from address. */
	public static final String EMAIL_FROM = "email.from";

	/** Properties key for the email to address. */
	public static final String EMAIL_TO = "email.to";
	
	/** Properties key for whether XML schema validation should occur on web service calls. */
	public static final String WSCLIENT_VALIDATE_XML = "wsclient.validate.xml";
	
	/** The instance. */
	private static volatile HPAProperties instance;
	
	/** The properties. */
	private Properties properties;
	
	/**
	 * Instantiates a new HPA properties.
	 */
	private HPAProperties() {
		// Open the properties file.
		InputStream input = getClass().getResourceAsStream("/hpa.properties");
		if(input == null) {
			throw new RuntimeException("Missing hpa.properties");
		}
		
		// Parse it.
		properties = new Properties();
		try {
			properties.load(input);
		} catch(Exception e) {
			throw new RuntimeException("Failed to load hpa.properties", e);
		} finally {
			try {
				input.close();
			} catch (IOException e) {
				throw new RuntimeException("Failed to close hpa.properties", e);
			}
		}
		
		// Validate it.
		String govtIdEncryptionKey = properties.getProperty(ENCRYPTION_KEY_TAXID);
		if(StringUtils.isEmpty(govtIdEncryptionKey)) {
			throw new RuntimeException("Missing " + ENCRYPTION_KEY_TAXID + " in hpa.properties");
		}
	}
	
	/**
	 * Gets the single instance of HPATransferProperties.
	 *
	 * @return single instance of HPATransferProperties
	 */
	private static HPAProperties getInstance() {
		if(instance == null) {
			instance = new HPAProperties();
		}
		return instance;
	}
	
	/**
	 * Gets the value for a key.
	 *
	 * @param key the key
	 * @return the value, or NULL if not found
	 */
	public static String get(String key) {
		return getInstance().properties.getProperty(key);
	}
	
	public static boolean getBoolean(String key) {
		String value = get(key);
		boolean result = BooleanUtils.toBoolean(value);
		return result;
	}
}
