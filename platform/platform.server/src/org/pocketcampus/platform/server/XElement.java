package org.pocketcampus.platform.server;

import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.Charset;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.*;
import org.xml.sax.InputSource;

/**
 * Simple XML parsing API on top of the horror that is Java's XML API.
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
		try {
			final Document doc = DocumentBuilderFactory
					.newInstance()
					.newDocumentBuilder()
					.newDocument();
			final Element root = doc.createElement(rootElementName);
			doc.appendChild(root);
			return new XElement(root);
		} catch (ParserConfigurationException e) {
			// never happening, since we don't change any config
			throw new RuntimeException("A ParserConfigurationException was thrown while creating an empty document with no configuration.", e);
		}
	}

	/** Parses an XElement from XML. */
	public static XElement parse(final String xml) throws Exception {
		final Document doc = DocumentBuilderFactory
				.newInstance()
				.newDocumentBuilder()
				.parse(new InputSource(new StringReader(xml)));
		return new XElement(doc.getDocumentElement());
	}

	/** Gets the element's content. */
	public String text() {
		return element.getTextContent().trim();
	}

	/** Gets the value of the attribute with the specified name. */
	public String attribute(final String name) {
		return element.getAttribute(name).trim();
	}

	/** Gets the content of the child element with the specified name. */
	public String childText(final String elementName) {
		return element.getElementsByTagName(elementName).item(0).getTextContent().trim();
	}

	/** Gets the child with the specified name. */
	public XElement child(final String name) {
		NodeList children = element.getElementsByTagName(name);
		if (children.getLength() == 0) {
			return null;
		}
		return new XElement((Element) children.item(0));
	}

	/** Gets the children with the specified name. */
	public XElement[] children(final String name) {
		final NodeList elements = element.getElementsByTagName(name);
		final XElement[] xelems = new XElement[elements.getLength()];
		for (int n = 0; n < xelems.length; n++) {
			xelems[n] = new XElement((Element) elements.item(n));
		}
		return xelems;
	}

	/** Adds the specified attribute with the specified value, or sets it if the attribute is already present. Returns this element. */
	public XElement setAttribute(final String key, final String value) {
		element.setAttribute(key, value);
		return this;
	}

	/** Adds a child element with the specified name and returns it. */
	public XElement addElement(final String name) {
		final Element child = element.getOwnerDocument().createElement(name);
		element.appendChild(child);
		return new XElement(child);
	}

	/** Gets the XML as a string, with the specified encoding. */
	public String toString(final Charset charset) {
		try {
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			transformer.setOutputProperty(OutputKeys.ENCODING, charset.name());
			StringWriter writer = new StringWriter();
			transformer.transform(new DOMSource(element), new StreamResult(writer));
			return writer.getBuffer().toString();
		} catch (TransformerException _) {
			// Bad, but toString can't throw exceptions, and this shouldn't ever happen anyway.
			return null;
		}
	}

	/** @deprecated Use toString(Charset) instead. */
	@Override
	@Deprecated
	public String toString() {
		return toString(Charset.defaultCharset());
	}
}