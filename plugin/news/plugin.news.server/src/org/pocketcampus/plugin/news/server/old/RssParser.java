package org.pocketcampus.plugin.news.server.old;

import org.pocketcampus.plugin.news.shared.Feed;
import org.pocketcampus.plugin.news.shared.NewsItem;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.InputStream;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

	/** The Url to the RSS Feed to be parsed */
	private String mUrlString;
	/** The resulting RSS Feed */
	private String mFeedName;
	/** The Feed to save the items in */
	private Feed mRssFeed;
	/** The item in which each parsed menu will be stored */
	private NewsItem mItem;
	/** The list of News descriptions */
	private HashMap<Long, String> mNewsContents;
	/** String with the content of the current news */
	private String mCurrentContent;
	/** Mutable sequence of Characters */
	private StringBuilder mText;
	/** Remembers whether a new item is being created */
	private boolean mInItem;
	/** Remembers whether an image is being created */
	private boolean mInImage;
	/** Remembers whether the input is text */
	private boolean mInTextInput;

	/** Used to get an image from the text. */
	private final static Pattern imagePattern_ = Pattern
			.compile("<img.*src=\"?(\\S+).*>");

	/**
	 * Constructor.
	 * 
	 * @param feedName
	 *            The name of the feed to parse.
	 * @param url
	 *            The Url to the feed to parse.
	 */
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
	 * @return The Feed corresponding to the Url that was parsed.
	 */
	public Feed getFeed() {
		return (this.mRssFeed);
	}

	/**
	 * @return The contents of the news items in the feeds.
	 */
	public HashMap<Long, String> getNewsContents() {
		return (this.mNewsContents);
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
				|| qName.equalsIgnoreCase("channel")) {
			this.mRssFeed = new Feed();
			this.mNewsContents = new HashMap<Long, String>();
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
			if (mItem.getImageUrl() == null && mCurrentContent != null) {
				Matcher m = imagePattern_.matcher(mCurrentContent);
				if (m.find()) {
					String img = m.group(1);
					if (img.charAt(img.length() - 1) == '\"')
						img = img.substring(0, img.length() - 1);
					mItem.setImageUrl(img);
				}
			}
			String content = mCurrentContent;

			mItem.setNewsItemId(generateNewsItemId(mItem.getTitle(),
					mCurrentContent, mItem.getImageUrl()));

			if (content != null) {
				content = content.replaceAll("<img[^>]+>", "");
				content = content.replaceAll("(&nbsp;)+", "");
				content = content.replaceAll("(<strong>)+", "<b>");
				content = content.replaceAll("(</strong>)+", "</b>");
				content = content.replaceAll("((<br />)\n)+", "\n<br />");
				content = content.replaceAll("(<p>(&nbsp;)+</p>)+", "");

				int carriageReturn = content.indexOf("<br />");

				if (carriageReturn != -1) {
					String firstParagraph = content
							.substring(0, carriageReturn);
					String rest = content.substring(carriageReturn,
							content.length());

					content = "<b>" + firstParagraph + "</b>" + rest;
				}
				this.mNewsContents.put(mItem.getNewsItemId(), content);
			}

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
				mCurrentContent = mText.toString().trim();

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

	/**
	 * Generates unique item Id for a news
	 * 
	 * @param title
	 *            The title of the News item
	 * @param content
	 *            The content of the News item
	 * @param imageUrl
	 *            the imageUrl of the News item
	 * @return The generated item Id
	 */
	private long generateNewsItemId(String title, String content,
			String imageUrl) {
		final long prime = 31;
		long result = 1;
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		result = prime * result + ((content == null) ? 0 : content.hashCode());
		result = prime * result
				+ ((imageUrl == null) ? 0 : imageUrl.hashCode());
		return result;
	}

	/**
	 * Converts a String date to a long.
	 * 
	 * @param pubDate
	 *            The String date to convert.
	 * @return the long corresponding to the String date.
	 */
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

	/**
	 * Appends the characters from an array to the StringBuilder text.
	 */
	public void characters(char[] ch, int start, int length) {
		this.mText.append(ch, start, length);
	}
}
