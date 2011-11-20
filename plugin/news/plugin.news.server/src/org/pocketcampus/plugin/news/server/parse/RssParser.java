package org.pocketcampus.plugin.news.server.parse;

import java.io.InputStream;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.pocketcampus.plugin.news.shared.Feed;
import org.pocketcampus.plugin.news.shared.NewsItem;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

/**
 * RssParser class. <br />
 * It allows to parse a Rss feed. <br />
 * Example: <br />
 * <code>
 * RssParser parser = new RssParser("http://... .xml"); <br />
 * parser.parse(); <br />
 * Feed feed = parser.getFeed(); <br />
 * </code>
 * 
 * @status working, not complete
 * 
 * @author Johan
 * 
 */
public class RssParser extends DefaultHandler {
	private static final String TAG = "RssParser";

	private String urlString_;
	private Feed rssFeed_;
	private StringBuilder text_;
	private NewsItem item_;
	private boolean inItem_;
	private boolean inImage_;
	private boolean inTextInput_;

	public RssParser(String url) {
		this.urlString_ = url;
		this.text_ = new StringBuilder();
	}

	/**
	 * Parses the Rss Feed.
	 */
	public void parse() {

		InputStream urlInputStream = null;
		SAXParserFactory spf = null;
		SAXParser sp = null;

		try {
			URL url = new URL(this.urlString_);
			urlInputStream = url.openConnection().getInputStream();
			spf = SAXParserFactory.newInstance();
			if (spf != null) {
				sp = spf.newSAXParser();
				sp.parse(urlInputStream, this);
			}
		} catch (Exception e) {
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
	 * Returns the feed once it has been parsed.
	 * 
	 * @return the parsed feed.
	 */
	public Feed getFeed() {
		return (this.rssFeed_);
	}

	public void startElement(String uri, String localName, String qName,
			Attributes attributes) {
		// XXX use of qName or localName? may differ on different Android
		// platform
		// (http://mmmyddd.freeshell.net/blog/Computer/Android/saxcompatibility)

		if (localName.equalsIgnoreCase("channel")
				|| qName.equalsIgnoreCase("channel")) {
			this.rssFeed_ = new Feed();
		} else if (localName.equalsIgnoreCase("item")
				&& (this.rssFeed_ != null) || qName.equalsIgnoreCase("item")) {
			this.item_ = new NewsItem();
			this.inItem_ = true;
		} else if (localName.equalsIgnoreCase("image")
				|| qName.equalsIgnoreCase("image")) {
			inImage_ = true;
		} else if (localName.equalsIgnoreCase("textInput")
				|| qName.equalsIgnoreCase("textInput")) {
			inTextInput_ = true;
		}
	}

	public void endElement(String uri, String localName, String qName) {
		if (this.rssFeed_ == null)
			return;

		// Special cases image and textInput - not managed (yet)
		if (this.inImage_) {
			if (localName.equalsIgnoreCase("image")
					|| qName.equalsIgnoreCase("image"))
				inImage_ = false;
			return;
		}
		if (this.inTextInput_) {
			if (localName.equalsIgnoreCase("textInput")
					|| qName.equalsIgnoreCase("textInput"))
				inTextInput_ = false;
			return;
		}

		if (localName.equalsIgnoreCase("item")
				|| qName.equalsIgnoreCase("item")) {
			this.inItem_ = false;
			this.rssFeed_.addToItems(this.item_);
			System.out.println("Added " + item_.getPubDateDate());
		} else if (localName.equalsIgnoreCase("title")
				|| qName.equalsIgnoreCase("title")) {
			if (this.inItem_ && this.item_ != null) {
				this.item_.setTitle(this.text_.toString().trim());
			} else {
				this.rssFeed_.setTitle(this.text_.toString().trim());
				System.out.println("Set RSS feed title to "
						+ rssFeed_.getTitle());
			}
		} else if (localName.equalsIgnoreCase("link")
				|| qName.equalsIgnoreCase("link")) {
			if (this.inItem_ && this.item_ != null) {
				this.item_.setLink(text_.toString().trim());
			} else {
				this.rssFeed_.setLink(text_.toString().trim());
				System.out
						.println("Set RSS feed link to " + rssFeed_.getLink());
			}
		} else if (localName.equalsIgnoreCase("description")
				|| qName.equalsIgnoreCase("description")) {
			if (this.inItem_ && this.item_ != null) {
				this.item_.setDescription(text_.toString().trim());
			} else {
				this.rssFeed_.setDescription(text_.toString().trim());
				System.out.println("Set RSS feed description to "
						+ rssFeed_.getDescription());

			}
		} else if (localName.equalsIgnoreCase("pubDate")
				|| qName.equalsIgnoreCase("pubDate")) {
			if (this.inItem_ && this.item_ != null) {
				String pubDate = text_.toString().trim();
				this.item_.setPubDate(pubDate);
				this.item_.setPubDateDate(getPubDateDate(pubDate));
			}
		}

		this.text_ = new StringBuilder();
	}

	public static long getPubDateDate(String pubDate) {

		Date pubDateDate = null;
		// Try to parse the following format: Thu, 24 Mar 2011 06:17:28
		// +0100
		// Date and time specification RFC 822
		SimpleDateFormat sdf = new SimpleDateFormat(
				"EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
		try {
			pubDateDate = sdf.parse(pubDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		if (pubDateDate != null) {
			return pubDateDate.getTime();
		}
		return 0;
	}

	public void characters(char[] ch, int start, int length) {
		this.text_.append(ch, start, length);
	}
}
