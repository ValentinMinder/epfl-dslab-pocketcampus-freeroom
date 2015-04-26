package org.pocketcampus.platform.server;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.Charset;

/**
 * Simple XML API on top of the horror that is Java's XML API.
 * 
 * @author Solal Pirelli <solal@pocketcampus.org>
 */
public final class XElement {
	private final Element element;

	private XElement(final Element element) {
		this.element = element;
	}

	/** Creates an XElement from scratch, with the specified name for the root element. */
	public static XElement create(final String rootElementName) {
		ensureNotBlank(rootElementName, "rootElementName");

		try {
			final Document doc =
					DocumentBuilderFactory
							.newInstance()
							.newDocumentBuilder()
							.newDocument();
			final Element root = doc.createElement(rootElementName);
			doc.appendChild(root);
			return new XElement(root);
		} catch (ParserConfigurationException e) {
			// should never happen
			throw new RuntimeException("A ParserConfigurationException was thrown while creating an XML document builder with no configuration.", e);
		}
	}

	/** Parses an XElement from XML, or returns null if the document cannot be parsed as XML. */
	public static XElement parse(final String xml) {
		ensureNotBlank(xml, "xml");

		try {
			final Document doc =
					DocumentBuilderFactory
							.newInstance()
							.newDocumentBuilder()
							.parse(new InputSource(new StringReader(xml)));
			return new XElement(doc.getDocumentElement());
		} catch (ParserConfigurationException e) {
			// should never happen
			throw new RuntimeException("A ParserConfigurationException was thrown while creating an XML document builder with no configuration.", e);
		} catch (IOException e) {
			// also should never happen
			throw new RuntimeException("An IOException was thrown while reading from a String.", e);
		} catch (SAXException e) {
			throw new IllegalArgumentException("Invalid XML.");
		}
	}

	/** Gets the element's content. */
	public String text() {
		return element.getTextContent().trim();
	}

	/** Gets the value of the attribute with the specified name. */
	public String attribute(final String name) {
		ensureNotBlank(name, "name");

		return element.getAttribute(name).trim();
	}

	/** Gets the child with the specified name. */
	public XElement child(final String name) {
		ensureNotBlank(name, "name");

		NodeList children = element.getElementsByTagName(name);
		if (children.getLength() == 0) {
			return null;
		}
		return new XElement((Element) children.item(0));
	}

	/** Gets the children with the specified name. */
	public XElement[] children(final String name) {
		ensureNotBlank(name, "name");

		final NodeList elements = element.getElementsByTagName(name);
		final XElement[] xelems = new XElement[elements.getLength()];
		for (int n = 0; n < xelems.length; n++) {
			xelems[n] = new XElement((Element) elements.item(n));
		}
		return xelems;
	}

	/** Adds the specified attribute with the specified value, or sets it if the attribute is already present. Returns this element. */
	public XElement setAttribute(final String key, final String value) {
		ensureNotBlank(key, "key");

		element.setAttribute(key, value);
		return this;
	}

	/** Adds a child element with the specified name and returns it. */
	public XElement addChild(final String name) {
		ensureNotBlank(name, "name");

		final Element child = element.getOwnerDocument().createElement(name);
		element.appendChild(child);
		return new XElement(child);
	}

	/** Gets the XML as a byte array, with the specified encoding. */
	public byte[] toBytes(final Charset charset) {
		try {
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.ENCODING, charset.name());
			ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
			transformer.transform(new DOMSource(element), new StreamResult(byteStream));
			return byteStream.toByteArray();
		} catch (TransformerException e) {
			// Bad, but this should never happen.
			return null;
		}
	}
	

	/** Validation method to ensure the specified parameter is neither null nor blank. */
	private static void ensureNotBlank(final String parameter, final String parameterName) {
		if (parameter == null || parameter.trim().length() == 0) {
			throw new IllegalArgumentException(parameterName + " cannot be null or blank.");
		}
	}
}