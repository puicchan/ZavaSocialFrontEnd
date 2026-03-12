package com.zava.util;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * DOM parsing utilities for handling SOAP XML responses from the .NET backend.
 * 
 * All methods are static -- this is a utility class, not meant to be instantiated.
 * Used throughout the SOAP client layer to extract values from the XML response
 * documents returned by ZavaService.asmx.
 * 
 * NOTE: This class does NOT handle namespaces properly. It works because
 * the .NET ASMX service returns elements in the default namespace and we
 * just match on local names. If the service ever changes its namespace
 * handling, this will break silently. - JK 2008
 * 
 * @author shuri
 * @version 1.0
 * @since 2007-04-20
 * @see com.zava.service.ZavaServiceClient
 */
public final class XmlHelper {

    /**
     * Private constructor to prevent instantiation.
     */
    private XmlHelper() {
        // utility class
    }

    // TODO: add namespace-aware parsing (doesn't matter for now, all responses use default ns)

    /**
     * Gets the text content of the first child element with the given tag name.
     * Returns an empty string if the element is not found or has no text content.
     * 
     * <p>This is the workhorse method -- called for every field on every bean
     * we parse out of a SOAP response.</p>
     * 
     * @param parent the parent element to search within
     * @param tagName the tag name to look for
     * @return the text content of the first matching element, or empty string
     * @deprecated Use {@link #getFirstChildElement(Element, String)} and read text
     *             directly for better null handling. Nobody has time to refactor
     *             all the call sites though. - TM 2009
     */
    public static String getElementText(Element parent, String tagName) {
        if (parent == null || tagName == null) {
            return "";
        }
        NodeList nodes = parent.getElementsByTagName(tagName);
        if (nodes.getLength() > 0) {
            Node firstNode = nodes.item(0);
            if (firstNode != null && firstNode.getTextContent() != null) {
                return firstNode.getTextContent().trim();
            }
        }
        return "";
    }

    /**
     * Gets all child elements with the given tag name.
     * 
     * <p>Used to iterate over arrays in SOAP responses, e.g. all ShoePost
     * elements inside an ArrayOfShoePost.</p>
     * 
     * @param parent the parent element to search within
     * @param tagName the tag name to look for
     * @return a list of matching elements, never null
     */
    public static List<Element> getChildElements(Element parent, String tagName) {
        List<Element> elements = new ArrayList<Element>();
        if (parent == null || tagName == null) {
            return elements;
        }
        NodeList nodes = parent.getElementsByTagName(tagName);
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node instanceof Element) {
                elements.add((Element) node);
            }
        }
        return elements;
    }

    /**
     * Gets the first child element with the given tag name, or null if not found.
     * 
     * @param parent the parent element to search within
     * @param tagName the tag name to look for
     * @return the first matching element, or null
     */
    public static Element getFirstChildElement(Element parent, String tagName) {
        if (parent == null || tagName == null) {
            return null;
        }
        NodeList nodes = parent.getElementsByTagName(tagName);
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node instanceof Element) {
                return (Element) node;
            }
        }
        return null;
    }

    /**
     * Parses the text content of a child element as an integer.
     * Returns the default value if the element is not found, is empty,
     * or cannot be parsed as an integer.
     * 
     * @param parent the parent element to search within
     * @param tagName the tag name to look for
     * @param defaultValue the value to return on failure
     * @return the parsed int value, or defaultValue
     */
    public static int getIntValue(Element parent, String tagName, int defaultValue) {
        String text = getElementText(parent, tagName);
        if (text.length() == 0) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            // bad data from the service -- log it and move on
            System.err.println("WARN: Could not parse int value for <" + tagName + ">: " + text);
            return defaultValue;
        }
    }

    /**
     * Parses an XML document from an input stream.
     * 
     * <p>Wraps the {@link DocumentBuilderFactory} boilerplate that you have to
     * write every single time you want to parse XML in Java.</p>
     * 
     * // NOTE: DocumentBuilderFactory is NOT thread-safe. Create a new one each time.
     * 
     * @param input the input stream containing XML data
     * @return the parsed Document
     * @throws RuntimeException if parsing fails for any reason
     */
    public static Document parseDocument(InputStream input) {
        // NOTE: DocumentBuilderFactory is NOT thread-safe. Create a new one each time.
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            return builder.parse(input);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse XML document: " + e.getMessage(), e);
        }
    }
}
