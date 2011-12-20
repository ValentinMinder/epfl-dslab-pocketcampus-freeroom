package org.pocketcampus.plugin.news.server.parse;

import java.io.InputStream;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

	private String mUrlString;
	private String mFeedName;
	private Feed mRssFeed;
	private StringBuilder mText;
	private NewsItem mItem;
	private boolean mInItem;
	private boolean mInImage;
	private boolean mInTextInput;

	/** Used to get an image from the text */
	private final static Pattern imagePattern_ = Pattern
			.compile("<img.*src=\"?(\\S+).*>");

	public RssParser(String feedName, String url) {
		this.mUrlString = url;
		this.mFeedName = feedName;
		this.mText = new StringBuilder();
	}

	/**
	 * Parses the Rss Feed.
	 */
	public void parse() {

		InputStream urlInputStream = null;
		SAXParserFactory spf = null;
		SAXParser sp = null;

		try {
			URL url = new URL(this.mUrlString);
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
		return (this.mRssFeed);
	}

	public void startElement(String uri, String localName, String qName,
			Attributes attributes) {
		// XXX use of qName or localName? may differ on different Android
		// platform
		// (http://mmmyddd.freeshell.net/blog/Computer/Android/saxcompatibility)

		if (localName.equalsIgnoreCase("channel")
				|| qName.equalsIgnoreCase("channel")) {
			this.mRssFeed = new Feed();
		} else if (localName.equalsIgnoreCase("item")
				&& (this.mRssFeed != null) || qName.equalsIgnoreCase("item")) {
			this.mItem = new NewsItem();
			this.mItem.setFeed(mFeedName);
			this.mInItem = true;
		} else if (localName.equalsIgnoreCase("image")
				|| qName.equalsIgnoreCase("image")) {
			mInImage = true;
		} else if (localName.equalsIgnoreCase("textInput")
				|| qName.equalsIgnoreCase("textInput")) {
			mInTextInput = true;
		}
	}

	public void endElement(String uri, String localName, String qName) {
		if (this.mRssFeed == null)
			return;

		// Special cases image and textInput - not managed (yet)
		if (this.mInImage) {
			if (localName.equalsIgnoreCase("image")
					|| qName.equalsIgnoreCase("image"))
				mInImage = false;
			return;
		}
		if (this.mInTextInput) {
			if (localName.equalsIgnoreCase("textInput")
					|| qName.equalsIgnoreCase("textInput"))
				mInTextInput = false;
			return;
		}

		if (localName.equalsIgnoreCase("item")
				|| qName.equalsIgnoreCase("item")) {
			this.mInItem = false;
			// Set the image URL
			if (mItem.getImageUrl() == null && mItem.getContent() != null) {
				Matcher m = imagePattern_.matcher(mItem.getContent());
				if (m.find()) {
					String img = m.group(1);
					if (img.charAt(img.length() - 1) == '\"')
						img = img.substring(0, img.length() - 1);
					mItem.setImageUrl(img);
				}
			}
			String content = this.mItem.getContent();
			content = content.replaceAll("<img[^>]+>", "");
			content = content.replaceAll("(&nbsp;)+", "");
			content = content.replaceAll("(<strong>)+", "<b>");
			content = content.replaceAll("(</strong>)+", "</b>");
			content = content.replaceAll("((<br />)\n)+", "\n<br />");
			content = content.replaceAll("(<p>(&nbsp;)+</p>)+", "");
			
			int carriageReturn = content.indexOf("<br />");

			if (carriageReturn != -1) {
				String firstParagraph = content.substring(0, carriageReturn);
				String rest = content.substring(carriageReturn,
						content.length());

				content = "<b>" + firstParagraph + "</b>" + rest;
			}
			this.mItem.setContent(content);

			this.mRssFeed.addToItems(this.mItem);
		} else if (localName.equalsIgnoreCase("title")
				|| qName.equalsIgnoreCase("title")) {
			if (this.mInItem && this.mItem != null) {
				this.mItem.setTitle(this.mText.toString().trim());
			} else {
				this.mRssFeed.setTitle(this.mText.toString().trim());
				this.mRssFeed.setTitle(mFeedName);
			}
		} else if (localName.equalsIgnoreCase("link")
				|| qName.equalsIgnoreCase("link")) {
			if (this.mInItem && this.mItem != null) {
				this.mItem.setLink(mText.toString().trim());
			} else {
				this.mRssFeed.setLink(mText.toString().trim());
			}
		} else if (localName.equalsIgnoreCase("description")
				|| qName.equalsIgnoreCase("description")) {
			if (this.mInItem && this.mItem != null) {
				String content = mText.toString().trim();

				this.mItem.setContent(content);
			} else {
				this.mRssFeed.setDescription(mText.toString().trim());
			}
		} else if (localName.equalsIgnoreCase("pubDate")
				|| qName.equalsIgnoreCase("pubDate")) {
			if (this.mInItem && this.mItem != null) {
				String pubDateString = mText.toString().trim();
				this.mItem.setPubDate(getPubDate(pubDateString));
			}
		}

		this.mText = new StringBuilder();
	}

	public static long getPubDate(String pubDate) {

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
		this.mText.append(ch, start, length);
	}
}
