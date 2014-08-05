package org.pocketcampus.plugin.food.server.old;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Old RSS-based meal parser.
 * Do not touch.
 * 
 * @author Elodie <elodienilane.triponez@epfl.ch>
 * @author Oriane <oriane.rodriguez@epfl.ch>
 * 
 */
public class OldRssParser extends DefaultHandler {
	/** The Url to the RSS Feed to be parsed. */
	private String urlString;

	/** The resulting RSS Feed. */
	private RssFeed rssFeed;

	/** Mutable sequence of Characters. */
	private StringBuilder text;

	/** The item in which each parsed menu will be stored. */
	private Item item;

	/** Tells whether the object being parsed is an image. */
	private boolean imgStatus;

	/**
	 * Constructor for the Parser.
	 * 
	 * @param url
	 *            The Url to the feed to parse.
	 */
	public OldRssParser(String url) {
		this.urlString = url;
		this.text = new StringBuilder();
	}

	/**
	 * Initiates the parsing of the Rss Feed page.
	 */
	public void parse() {
		InputStream urlInputStream = null;
		SAXParserFactory spf = null;
		SAXParser sp = null;

		try {
			URL url = new URL(this.urlString);
			urlInputStream = url.openConnection().getInputStream();
			spf = SAXParserFactory.newInstance();
			if (spf != null) {
				sp = spf.newSAXParser();
				sp.parse(urlInputStream, this);
			}
		}

		/*
		 * Exceptions need to be handled MalformedURLException
		 * ParserConfigurationException IOException SAXException
		 */
		catch (IOException ioe) {
			System.out.println("<RssParser>: IOException.");
		} catch (Exception e) {
			System.out.println("Exception: " + e);
			e.printStackTrace();
		} finally {
			try {
				if (urlInputStream != null)
					urlInputStream.close();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * @return The Feed corresponding to the Url that was parsed.
	 */
	public RssFeed getFeed() {
		return (this.rssFeed);
	}

	/**
	 * Receives notification of the start of a new element
	 * 
	 * @param uri
	 *            The Namespace URI, or the empty string if the element has no
	 *            Namespace URI or if Namespace processing is not being
	 *            performed.
	 * @param localName
	 *            The local name (without prefix), or the empty string if
	 *            Namespace processing is not being performed.
	 * @param qName
	 *            The qualified name (with prefix), or the empty string if
	 *            qualified names are not available.
	 * @param attributes
	 *            The attributes attached to the element. If there are no
	 *            attributes, it shall be an empty Attributes object.
	 */
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) {
		if (localName.equalsIgnoreCase("channel")
				|| qName.equalsIgnoreCase("channel"))
			this.rssFeed = new RssFeed();
		else if ((localName.equalsIgnoreCase("item") || qName
				.equalsIgnoreCase("item")) && (this.rssFeed != null)) {
			this.item = new Item();
			this.rssFeed.addItem(this.item);
		} else if ((localName.equalsIgnoreCase("image") || qName
				.equalsIgnoreCase("image")) && (this.rssFeed != null))
			this.imgStatus = true;
	}

	/**
	 * Receives notification of the end of a new element.
	 * 
	 * @param uri
	 *            The Namespace URI, or the empty string if the element has no
	 *            Namespace URI or if Namespace processing is not being
	 *            performed.
	 * @param localName
	 *            The local name (without prefix), or the empty string if
	 *            Namespace processing is not being performed.
	 * @param qName
	 *            The qualified name (with prefix), or the empty string if
	 *            qualified names are not available.
	 */
	public void endElement(String uri, String localName, String qName) {
		if (this.rssFeed == null)
			return;

		if (localName.equalsIgnoreCase("item")
				|| qName.equalsIgnoreCase("item"))
			this.item = null;

		else if (localName.equalsIgnoreCase("image")
				|| qName.equalsIgnoreCase("image"))
			this.imgStatus = false;

		else if (localName.equalsIgnoreCase("title")
				|| qName.equalsIgnoreCase("title")) {
			if (this.item != null) {
				this.item.title = removeBadStuff(this.text.toString().trim());
//				this.item.title = capitalize(this.item.title);
			} else if (this.imgStatus)
				this.rssFeed.imageTitle = this.text.toString().trim();
			else
				this.rssFeed.title = this.text.toString().trim();
		}

		else if (localName.equalsIgnoreCase("link")
				|| qName.equalsIgnoreCase("link")) {
			if (this.item != null)
				this.item.link = this.text.toString().trim();
			else if (this.imgStatus)
				this.rssFeed.imageLink = this.text.toString().trim();
			else
				this.rssFeed.link = this.text.toString().trim();
		}

		else if (localName.equalsIgnoreCase("description")
				|| qName.equalsIgnoreCase("description")) {
			if (this.item != null) {
				this.item.description = removeBadStuff(this.text.toString()
						.trim());
				//this.item.description = capitalize(this.item.description);
				this.item.description = removeExtraLineBreaks(this.item.description);
				
			} else
				this.rssFeed.description = this.text.toString().trim();
		}

		else if ((localName.equalsIgnoreCase("url") || qName
				.equalsIgnoreCase("url")) && this.imgStatus)
			this.rssFeed.imageUrl = this.text.toString().trim();

		else if (localName.equalsIgnoreCase("language")
				|| qName.equalsIgnoreCase("language"))
			this.rssFeed.language = this.text.toString().trim();

		else if (localName.equalsIgnoreCase("generator")
				|| qName.equalsIgnoreCase("generator"))
			this.rssFeed.generator = this.text.toString().trim();

		else if (localName.equalsIgnoreCase("copyright")
				|| qName.equalsIgnoreCase("copyright"))
			this.rssFeed.copyright = this.text.toString().trim();

		else if ((localName.equalsIgnoreCase("category") || qName
				.equalsIgnoreCase("category")) && (this.item != null))
			this.rssFeed.addItem(this.text.toString().trim(), this.item);

		this.text.setLength(0);
	}
	
	/**
	 * Removes unneeded line breaks and spaces.
	 * @param description
	 * @return
	 */
	private String removeExtraLineBreaks(String description) {
		description = description.replaceAll("[\\xA0]+", " "); // replace non-breaking spaces (code 160) with normal spaces (code 32)
		description = description.replaceAll("[\\t\\r\\v\\f]+", ""); // remove some weird characters
		description = description.replaceAll("[\\n][ ]+", "\n"); // remove spaces at the beginning of a line
		description = description.replaceAll("[ ]+[\\n]", "\n"); // remove spaces at the end of a line
		description = description.replaceAll("[ ]+", " "); // remove consecutive spaces
		description = description.replaceAll("[\\n]+", "\n"); // remove consecutive new-lines
		description = description.replaceAll("^[\\n]+", ""); // remove new-line characters at the beginning
		description = description.replaceAll("[\\n]+$", ""); // remove new-line characters at the end
		
		return description;
	}
	
	/**
	 * Remove unwanted characters, such as apostrophes question marks, carrier
	 * line feeds, html tags.
	 * 
	 * @param s
	 *            the string to format.
	 * @return the formatted string.
	 */
	private String removeBadStuff(String s) {
		if (s.length() > 0) {
			s = s.replace("′", "'");
			s = s.replace("l?", "l'");
			s = s.replace("Ã©", "é");
			s = s.replace("<p>", "");
			s = s.replace("<em>", "");
			s = s.replace("</em>", "");
			s = s.replace("</p>", "");
			s = s.replace("<br>", "");
			s = s.replace("<br />", "");
			if (!(s.charAt(s.length() - 1) == ('\n'))) {
				s += "\n";
			}
		}
		return s.trim();
	}
	

	/**
	 * Represents an RSS Feed.
	 * 
	 * @author Elodie <elodienilane.triponez@epfl.ch>
	 * 
	 */
	public static class RssFeed {
		/** The title of the feed */
		public String title;
		/** The description of the feed */
		public String description;
		/** The link to the feed */
		public String link;
		/** The language of the feed */
		public String language;
		/** The generator of the feed */
		public String generator;
		/** The copyright of the feed */
		public String copyright;
		/** The Url to the image of the feed */
		public String imageUrl;
		/** The Title of the image of the feed */
		public String imageTitle;
		/** The link the image of the feed leeds to */
		public String imageLink;

		/** The items in the feed */
		public ArrayList<Item> items;
		/** The items sorted according to their category */
		public HashMap<String, ArrayList<Item>> category;

		/** Add an item to the Feed list */
		public void addItem(Item item) {
			if (this.items == null)
				this.items = new ArrayList<Item>();
			this.items.add(item);
		}

		/** Add an item to the list sorted by category */
		public void addItem(String category, Item item) {
			if (this.category == null)
				this.category = new HashMap<String, ArrayList<Item>>();
			if (!this.category.containsKey(category))
				this.category.put(category, new ArrayList<Item>());
			this.category.get(category).add(item);
		}
	}

	/**
	 * An item in a feed.
	 */
	public static class Item {
		/** The title of the Item */
		public String title;
		/** The description of the Item */
		public String description;
		/** The link of the Item */
		public String link;
		
		/**
		 * Returns the string representation of the Item
		 */
		public String toString() {
			return (this.title + ": " + this.description);
		}
	}

	/**
	 * Appends the characters from an array to the StringBuilder text.
	 */
	public void characters(char[] ch, int start, int length) {
		this.text.append(ch, start, length);
	}

}