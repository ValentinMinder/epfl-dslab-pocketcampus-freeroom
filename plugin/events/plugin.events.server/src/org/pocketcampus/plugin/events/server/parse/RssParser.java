package org.pocketcampus.plugin.events.server.parse;

import java.io.InputStream;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.pocketcampus.plugin.events.shared.EventsItem;
import org.pocketcampus.plugin.events.shared.Feed;
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
	private EventsItem mItem;
	private boolean mInItem;
	private boolean mInImage;
	private boolean mInTextInput;

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
			this.mItem = new EventsItem();
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
			if (!mItem.getTitle().contains("to be defined"))
				;
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
				this.mItem.setContent(mText.toString().trim());
			} else {
				this.mRssFeed.setDescription(mText.toString().trim());
			}
		} else if (localName.equalsIgnoreCase("epfl:urlref")
				|| qName.equalsIgnoreCase("epfl:urlref")) {
			if (this.mInItem && this.mItem != null) {
				String urlref = mText.toString().trim();
				this.mItem.setUrlref(urlref);
			}
		} else if (localName.equalsIgnoreCase("epfl:startDate")
				|| qName.equalsIgnoreCase("epfl:startDate")) {
			if (this.mInItem && this.mItem != null) {
				String startDateString = mText.toString().trim();
				this.mItem.setStartDate(getPubDate(startDateString));
			}
		} else if (localName.equalsIgnoreCase("epfl:endDate")
				|| qName.equalsIgnoreCase("epfl:endDate")) {
			if (this.mInItem && this.mItem != null) {
				String endDateString = mText.toString().trim();
				this.mItem.setEndDate(getPubDate(endDateString));
			}
		} else if (localName.equalsIgnoreCase("epfl:startTime")
				|| qName.equalsIgnoreCase("epfl:startTime")) {
			if (this.mInItem && this.mItem != null) {
				String startTimeString = mText.toString().trim();
				this.mItem.setStartTime(startTimeString);
			}
		} else if (localName.equalsIgnoreCase("epfl:speaker")
				|| qName.equalsIgnoreCase("epfl:speaker")) {
			if (this.mInItem && this.mItem != null) {
				String speaker = mText.toString().trim();
				this.mItem.setSpeaker(speaker);
			}
		} else if (localName.equalsIgnoreCase("epfl:speaker")
				|| qName.equalsIgnoreCase("epfl:speaker")) {
			if (this.mInItem && this.mItem != null) {
				String speaker = mText.toString().trim();
				this.mItem.setSpeaker(speaker);
			}
		} else if (localName.equalsIgnoreCase("epfl:contact")
				|| qName.equalsIgnoreCase("epfl:contact")) {
			if (this.mInItem && this.mItem != null) {
				String contact = mText.toString().trim();
				this.mItem.setContact(contact);
			}
		} else if (localName.equalsIgnoreCase("epfl:language")
				|| qName.equalsIgnoreCase("epfl:language")) {
			if (this.mInItem && this.mItem != null) {
				String language = mText.toString().trim();
				this.mItem.setLanguage(language);
			}
		} else if (localName.equalsIgnoreCase("epfl:audience")
				|| qName.equalsIgnoreCase("epfl:audience")) {
			if (this.mInItem && this.mItem != null) {
				String audience = mText.toString().trim();
				this.mItem.setAudience(audience);
			}
		} else if (localName.equalsIgnoreCase("epfl:expectedPeople")
				|| qName.equalsIgnoreCase("epfl:expectedPeople")) {
			if (this.mInItem && this.mItem != null) {
				String expectedPeople = mText.toString().trim();
				this.mItem.setExpectedPeople(expectedPeople);
			}
		} else if (localName.equalsIgnoreCase("epfl:location")
				|| qName.equalsIgnoreCase("epfl:location")) {
			if (this.mInItem && this.mItem != null) {
				String location = mText.toString().trim();
				this.mItem.setLocation(location);
			}
		} else if (localName.equalsIgnoreCase("epfl:room")
				|| qName.equalsIgnoreCase("epfl:room")) {
			if (this.mInItem && this.mItem != null) {
				String room = mText.toString().trim();
				this.mItem.setRoom(room);
			}
		} else if (localName.equalsIgnoreCase("epfl:category")
				|| qName.equalsIgnoreCase("epfl:category")) {
			if (this.mInItem && this.mItem != null) {
				String category = mText.toString().trim();
				this.mItem.setCategory(category);
			}
		} else if (localName.equalsIgnoreCase("epfl:organizer")
				|| qName.equalsIgnoreCase("epfl:organizer")) {
			if (this.mInItem && this.mItem != null) {
				String organizerString = mText.toString().trim();
				this.mItem.setOrganizer(organizerString);
			}
		} else if (localName.equalsIgnoreCase("epfl:shortTitle")
				|| qName.equalsIgnoreCase("epfl:shortTitle")) {
			if (this.mInItem && this.mItem != null) {
				String shortTitle = mText.toString().trim();
				this.mItem.setShorttitle(shortTitle);
			}
		}
		this.mText = new StringBuilder();
	}

	public static long getPubDate(String pubDateString) {

		Date pubDate = null;
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		try {
			pubDate = df.parse(pubDateString);
		} catch (ParseException e) {
			System.out.println("No date");
		}
		if (pubDate != null) {
			return pubDate.getTime();
		}
		return 0;
	}

	public void characters(char[] ch, int start, int length) {
		this.mText.append(ch, start, length);
	}
}
