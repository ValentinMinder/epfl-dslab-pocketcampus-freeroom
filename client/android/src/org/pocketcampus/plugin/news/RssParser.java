package org.pocketcampus.plugin.news;

import java.io.InputStream;
import java.net.URL;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;

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
			Log.e(TAG, e.toString());
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
	 * @return the parsed feed.
	 */
	public Feed getFeed() {
		return (this.rssFeed_);
	}

	public void startElement(String uri, String localName, String qName,
			Attributes attributes) {
		// XXX use of qName or localName?

		if (localName.equalsIgnoreCase("channel")) {
			this.rssFeed_ = new Feed();
		} else if (localName.equalsIgnoreCase("item") && (this.rssFeed_ != null)) {
			this.item_ = new NewsItem();
			this.inItem_ = true;
		} else if (localName.equalsIgnoreCase("image")) {
			inImage_ = true;
		} else if (localName.equalsIgnoreCase("textInput")) {
			inTextInput_ = true;
		}
	}

	public void endElement(String uri, String localName, String qName) {
		if (this.rssFeed_ == null)
			return;

		// Special cases image and textInput - not managed (yet)
		if (this.inImage_) {
			if (localName.equalsIgnoreCase("image"))
				inImage_ = false;
			return;
		}
		if (this.inTextInput_) {
			if (localName.equalsIgnoreCase("textInput"))
				inTextInput_ = false;
			return;
		}

		if (localName.equalsIgnoreCase("item")) {
			this.inItem_ = false;
			this.rssFeed_.addItem(this.item_);
		} else if (localName.equalsIgnoreCase("title")) {
			if (this.inItem_ && this.item_ != null) {
				this.item_.setTitle(this.text_.toString().trim());
			} else {
				this.rssFeed_.setTitle(this.text_.toString().trim());
			}
		} else if (localName.equalsIgnoreCase("link")) {
			if (this.inItem_ && this.item_ != null) {
				this.item_.setLink(text_.toString().trim());
			} else {
				this.rssFeed_.setLink(text_.toString().trim());
			}
		} else if (localName.equalsIgnoreCase("description")) {
			if (this.inItem_ && this.item_ != null) {
				this.item_.setDescription(text_.toString().trim());
			} else {
				this.rssFeed_.setDescription(text_.toString().trim());
			}
		} else if (localName.equalsIgnoreCase("pubDate")) {
			if (this.inItem_ && this.item_ != null) {
				this.item_.setPubDate(text_.toString().trim());
			}
		}

		this.text_ = new StringBuilder();
	}

	public void characters(char[] ch, int start, int length) {
		this.text_.append(ch, start, length);
	}
}
