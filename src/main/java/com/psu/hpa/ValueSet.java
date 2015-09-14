package com.psu.hpa;

import java.util.Map;

public interface ValueSet {
	/**
	 * Gets an ordered map of codes to descriptions for this data type.
	 *
	 * @param type data type name
	 * @return the map of codes to descriptions
	 */
	public Map<String, String> getOrderedMapForType(String type);
	
	/**
	 * Gets the description of a code of this data type.
	 * 
	 * @param type the data type of the value
	 * @param code the code to get a description for
	 * @return the description of the code, or null if not found
	 */
	public String getDescriptionForCode(String type, String code);
	
	/**
	 * Gets the code for a description string of this data type.
	 * 
	 * @param type the data type of the value
	 * @param code the code to get a description for
	 * @return the code for the description, or null if not found
	 */
	public String getCodeForDescription(String type, String description);
}
