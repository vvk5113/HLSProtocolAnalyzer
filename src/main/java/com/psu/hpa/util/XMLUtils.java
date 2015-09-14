package com.psu.hpa.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.xml.transform.StringSource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.SAXException;

import com.psu.hpa.XMLException;

public class XMLUtils {
	private static Logger log = LoggerFactory.getLogger(XMLUtils.class);
	
	public static final String W3C_XML_SCHEMA_NS_URI = "http://www.w3.org/2001/XMLSchema";
	
	public static final String VALIDATION_WRAPPER_SCHEMA = "/xsd/validationWrapper.xsd";
	
	/**
	 * Parses an XML document.
	 *
	 * @param xmlPath path to the XML document
	 * @return the document
	 * @throws XMLException the XML exception
	 */
	public static Document parse(String xmlPath) throws XMLException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		
		DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			throw new XMLException("Unable to create parser", e);
		}
		
		Document doc;
		try {
			doc = builder.parse(xmlPath);
		} catch (SAXException e) {
			throw new XMLException("Unable to parse " + xmlPath, e);
		} catch (IOException e) {
			throw new XMLException("Unable to read " + xmlPath, e);
		}
		
		return doc;
	}
	
	/**
	 * Parses an XML document.
	 *
	 * @param input input stream to the XML document
	 * @return the document
	 * @throws XMLException the XML exception
	 */
	public static Document parse(InputStream input) throws XMLException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		
		DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			throw new XMLException("Unable to create parser", e);
		}
		
		Document doc;
		try {
			doc = builder.parse(input);
		} catch (SAXException e) {
			throw new XMLException("Unable to parse " + input, e);
		} catch (IOException e) {
			throw new XMLException("Unable to read " + input, e);
		}
		
		return doc;
	}

	public static Document makeDocument(Element rootElement) throws XMLException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		
		DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
		} catch(ParserConfigurationException e) {
			throw new XMLException("Unable to create parser", e);
		}

		Document result = builder.newDocument();
		result.appendChild(result.importNode(rootElement, true));
		return result;
	}
	
	/**
	 * Finds the first child element with this name, and returns its text.
	 *
	 * @param parent the parent element
	 * @param tagName the tag name to search for in parent, or null to return any element
	 * @return the first element with this tag name, or null if not found
	 */
	public static Element getFirstElement(Element parent, String tagName) {
		Element result = null;
		
		NodeList nodes = parent.getChildNodes();
		for(int i = 0; i < nodes.getLength(); i++) {
			Node node = nodes.item(i);
			if(node.getNodeType() == Node.ELEMENT_NODE) {
				if(tagName != null && !tagName.equals(node.getLocalName())) {
					continue;
				}
				if(result != null) {
					// Warn that we found another one, but keep the original result.
					log.warn(String.format("Multiple <%s> nodes found in %s", tagName, parent.getOwnerDocument().getDocumentURI()));
					break;
				}
				result = (Element)node;
			}
		}
		
		return result;
	}

	/**
	 * Finds the first child element with this name, and returns its text.
	 *
	 * @param parent the parent element
	 * @param tagName the tag name to search for in parent
	 * @return the text content, or null if not found
	 */
	public static String getNodeText(Element parent, String tagName) {
		String text = null;
		
		Element element = getFirstElement(parent, tagName);
		if(element != null) {
			text = element.getTextContent();
		}
				
		return text;
	}
	
	/**
	 * Add new Node before given Node in XML Document and returns the updated Document.
	 *
	 * @param rootElement the root element
	 * @param newNodeName the new node name
	 * @param newNodeText the new node text value
	 * @param existingNodeName the existing node before which new node is inserted
	 * @return the updated document
	 * @throws XMLException 
	 */
	public static Document insertNodeBefore(Element rootElement, String newNodeName, String newNodeText, String existingNodeName) throws XMLException {
		Document txlifeDocument = makeDocument(rootElement);
		NodeList existingNodes = txlifeDocument.getElementsByTagNameNS("http://ACORD.org/Standards/Life/2", existingNodeName);
		NodeList newNodes = txlifeDocument.getElementsByTagNameNS("http://ACORD.org/Standards/Life/2", newNodeName);
		
		if((existingNodes != null && existingNodes.getLength() > 0)) {
			if(!(newNodes != null && newNodes.getLength() > 0)) {
				Element node = txlifeDocument.createElementNS("http://ACORD.org/Standards/Life/2", newNodeName);
				Text nodeText = txlifeDocument.createTextNode(newNodeText); 
				node.appendChild(nodeText); 
				node.setPrefix(rootElement.getPrefix());
				existingNodes.item(0).getParentNode().insertBefore(node, existingNodes.item(0));
			}
		}
		
		return txlifeDocument;
	}
	
	/**
	 * Convert a DOM Document to a string.
	 *
	 * @param document the document
	 * @return the string
	 * @throws XMLException 
	 */
	public static String documentToString(Document document) throws XMLException {
	    DOMImplementationLS domImplementation = (DOMImplementationLS)document.getImplementation();
	    LSSerializer lsSerializer = domImplementation.createLSSerializer();
	    return lsSerializer.writeToString(document);   
	}
	
	/**
	 * Validate XML.
	 *
	 * @param xml the XML to validate
	 * @param schemaFile the schema file
	 * @return true, if valid
	 * @throws SAXException
	 * @throws IOException
	 */
	public static void validate(String xml, URL schemaFile) throws SAXException, IOException {
		Source source = new StringSource(xml);
		Schema schema = SchemaFactory.newInstance(W3C_XML_SCHEMA_NS_URI).newSchema(schemaFile);
		Validator validator = schema.newValidator();
		validator.validate(source);
	}
	
	/**
	 * Validate a TXLife element.
	 *
	 * @param txlifeXML the txlife xml
	 * @throws SAXException the sAX exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void validateTXLifeElement(String txlifeXML) throws SAXException, IOException {
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
		             "<SchemaValidation xmlns=\"http://ACORD.org/Standards/Life/2\">\n" +
		             txlifeXML +
		             "</SchemaValidation>";
		Source source = new StringSource(xml);
		URL validationWrapperSchema = XMLUtils.class.getResource(VALIDATION_WRAPPER_SCHEMA);
		Schema schema = SchemaFactory.newInstance(W3C_XML_SCHEMA_NS_URI).newSchema(validationWrapperSchema);
		Validator validator = schema.newValidator();
		validator.validate(source);
	}
}