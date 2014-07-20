package org.pocketcampus.platform.server;

import java.io.ByteArrayInputStream;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.*;

/**
 * Super-simple XML parsing API on top of the horror that is Java's XML API.
 * 
 * @author Solal Pirelli <solal@pocketcampus.org>
 */
public final class XElement {
	private final Element _element;

	private XElement(Element element) {
		_element = element;
	}

	/** Parses an XElement from XML. */
	public static XElement parse(String xml) throws Exception {
		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
				.parse(new ByteArrayInputStream(xml.getBytes()));
		return new XElement(doc.getDocumentElement());
	}
	
	/** Gets the element's content. */
	public String text() {
		return _element.getTextContent().trim();
	}

	/** Gets the value of the attribute with the specified name. */
	public String attribute(String name) {
		return _element.getAttribute(name).trim();
	}

	/** Gets the content of the child element with the specified name. */
	public String elementText(String elementName) {
		return _element.getElementsByTagName(elementName).item(0).getTextContent().trim();
	}

	/** Gets the child with the specified name. */
	public XElement child(String name) {
		return new XElement((Element) _element.getElementsByTagName(name).item(0));
	}

	/** Gets the children with the specified name. */
	public XElement[] children(String name) {
		NodeList elements = _element.getElementsByTagName(name);
		XElement[] xelems = new XElement[elements.getLength()];
		for (int n = 0; n < xelems.length; n++) {
			xelems[n] = new XElement((Element) elements.item(n));
		}
		return xelems;
	}
}