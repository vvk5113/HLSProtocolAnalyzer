package com.psu.hpa;

import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.psu.hpa.util.XMLUtils;

/**
 * Store patches for value lookups.
 */
public class ValuePatch {
	/** XML tag name for patch set. */
	private static final String TAG_PATCH = "patch";

	/** XML attribute for referencing another value set. */
	private static final String ATTR_REF = "ref";

	/** XML tag name for patch item. */
	private static final String TAG_ITEM = "item";

	/** XML tag name for delete item. */
	private static final String TAG_DELETE = "delete";

	/** XML attribute for an item name (ACORD type code). */
	private static final String ATTR_NAME = "name";

	/** Map from a valueset id to a map of its items. */
	private Map<String, BiMap<String, String>> patches;

	/** Set of value keys to delete. */
	private Map<String, Set<String>> deletes;

	/**
	 * Instantiates a new value data store.
	 */
	public ValuePatch() {
		patches = new HashMap<String, BiMap<String, String>>();
		deletes = new HashMap<String, Set<String>>();
	}

	public ValuePatch(String path) throws XMLException {
		this(ValuePatch.class.getResourceAsStream(path));
	}

	/**
	 * Instantiates a new value data store from an XML resource.
	 *
	 * @param path the resource path
	 * @throws XMLException the XML exception
	 */
	public ValuePatch(InputStream input) throws XMLException {
		this();

		Document document = null;
		try {
			document = XMLUtils.parse(input);
		} catch (XMLException e) {
			e.printStackTrace();
		}
		if(document == null) {
			throw new XMLException("Parser failure: null document");
		}
		Element root = document.getDocumentElement();

		NodeList valueSetElements = root.getElementsByTagName(TAG_PATCH);
		for(int i = 0; i < valueSetElements.getLength(); i++) {
			Element valueset = (Element)valueSetElements.item(i);
			if(!valueset.hasAttribute(ATTR_REF)) {
				throw new IllegalArgumentException(String.format("Patch is missing ref: %s", valueset));
			}

			NodeList itemElements = valueset.getElementsByTagName(TAG_ITEM);
			BiMap<String, String> items = HashBiMap.create();
			for(int j = 0; j < itemElements.getLength(); j++) {
				Element item = (Element)itemElements.item(j);
				if(!item.hasAttribute(ATTR_NAME)) {
					throw new IllegalArgumentException(String.format("Patch item missing name: %s", item));
				}
				items.put(item.getAttribute(ATTR_NAME), item.getTextContent());
			}

			Set<String> deleteCodes = new HashSet<String>();
			NodeList deleteElements = valueset.getElementsByTagName(TAG_DELETE);
			for(int j = 0; j < deleteElements.getLength(); j++) {
				Element item = (Element)deleteElements.item(j);
				if(!item.hasAttribute(ATTR_NAME)) {
					throw new IllegalArgumentException(String.format("Delete item missing name: %s", item));
				}
				deleteCodes.add(item.getAttribute(ATTR_NAME));
			}

			String ref = valueset.getAttribute(ATTR_REF);
			patches.put(ref, items);
			deletes.put(ref, deleteCodes);
		}
	}

	/**
	 * Gets the patches.
	 *
	 * @return the patches
	 */
	public Map<String, BiMap<String, String>> getPatches() {
		return patches;
	}

	/**
	 * Gets the deletes.
	 *
	 * @return the deletes
	 */
	public Map<String, Set<String>> getDeletes() {
		return deletes;
	}
}