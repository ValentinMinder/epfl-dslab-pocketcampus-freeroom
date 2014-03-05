package org.pocketcampus.platform.sdk.server;

import java.io.ByteArrayInputStream;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.*;

/**
 * Super-simple XML parsing API on top of the horror that is Java's XML API.
 * 
 * @author Solal Pirelli <solal.pirelli@epfl.ch>
 */
public final class XElement {
	private final Element _element;

	private XElement(Element element) {
		_element = element;
	}

	public String attribute(String key) {
		return _element.getAttribute(key);
	}

	public String elementText(String elementName) {
		return _element.getElementsByTagName(elementName).item(0).getTextContent().trim();
	}

	public XElement child(String childName) {
		Element element = (Element) _element.getElementsByTagName(childName).item(0);
		return new XElement(element);
	}

	public XElement[] children(String childrenName) {
		NodeList elements = _element.getElementsByTagName(childrenName);
		XElement[] xelems = new XElement[elements.getLength()];
		for (int n = 0; n < xelems.length; n++) {
			xelems[n] = new XElement((Element) elements.item(n));
		}
		return xelems;
	}

	/** Parses an XElement from XML. */
	public static XElement parse(String xml) {
		try {
			Element element =
					DocumentBuilderFactory.newInstance()
							.newDocumentBuilder()
							.parse(new ByteArrayInputStream(xml.getBytes()))
							.getDocumentElement();
			return new XElement(element);
		} catch (Exception e) {
			// Pokémon! Gotta catch'em all!
			return null;
		}
	}
}