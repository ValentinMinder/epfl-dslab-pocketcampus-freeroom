package org.pocketcampus.plugin.events.server.parse;

import java.io.InputStream;
import java.net.URL;
import java.text.DateFormat;
import java.text.Format;
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

		String current = removeBadStuff(this.mText.toString().trim());

		if (localName.equalsIgnoreCase("item")
				|| qName.equalsIgnoreCase("item")) {
			this.mInItem = false;
			if (!mItem.getTitle().contains("To be defined")) {
				this.mRssFeed.addToItems(this.mItem);
			}

		} else if (localName.equalsIgnoreCase("title")
				|| qName.equalsIgnoreCase("title")) {
			if (this.mInItem && this.mItem != null) {
				String title = current;

				if (!title.isEmpty() && title.length() > 1) {
					title = title.substring(0, 1).toUpperCase()
							+ title.substring(1, title.length());
				}
				this.mItem.setTitle(title);
			} else {
				this.mRssFeed.setTitle(current);
				this.mRssFeed.setTitle(mFeedName);
			}
		} else if (localName.equalsIgnoreCase("link")
				|| qName.equalsIgnoreCase("link")) {
			if (this.mInItem && this.mItem != null) {
				this.mItem.setLink(current);
			} else {
				this.mRssFeed.setLink(current);
			}
		} else if (localName.equalsIgnoreCase("description")
				|| qName.equalsIgnoreCase("description")) {
			if (this.mInItem && this.mItem != null) {
				this.mItem.setContent(current);
			} else {
				this.mRssFeed.setDescription(current);
			}
		} else if (localName.equalsIgnoreCase("epfl:urlref")
				|| qName.equalsIgnoreCase("epfl:urlref")) {
			if (this.mInItem && this.mItem != null) {
				String urlref = current;
				this.mItem.setUrlref(urlref);
			}
		} else if (localName.equalsIgnoreCase("epfl:startDate")
				|| qName.equalsIgnoreCase("epfl:startDate")) {
			if (this.mInItem && this.mItem != null) {
				String startDateString = current;
				this.mItem.setStartDate(getPubDate(startDateString));
			}
		} else if (localName.equalsIgnoreCase("epfl:endDate")
				|| qName.equalsIgnoreCase("epfl:endDate")) {
			if (this.mInItem && this.mItem != null) {
				String endDateString = current;
				this.mItem.setEndDate(getPubDate(endDateString));
			}
		} else if (localName.equalsIgnoreCase("epfl:startTime")
				|| qName.equalsIgnoreCase("epfl:startTime")) {
			if (this.mInItem && this.mItem != null) {
				String startTimeString = current;
				long startDate = this.mItem.getStartDate();
				if (startDate != 0) {
					this.mItem.setStartDate(addStartTime(startDate,
							startTimeString));
				}
				this.mItem.setStartTime(startTimeString);
			}
		} else if (localName.equalsIgnoreCase("epfl:speaker")
				|| qName.equalsIgnoreCase("epfl:speaker")) {
			if (this.mInItem && this.mItem != null) {
				String speaker = current;
				this.mItem.setSpeaker(speaker);
			}
		} else if (localName.equalsIgnoreCase("epfl:speaker")
				|| qName.equalsIgnoreCase("epfl:speaker")) {
			if (this.mInItem && this.mItem != null) {
				String speaker = current;
				this.mItem.setSpeaker(speaker);
			}
		} else if (localName.equalsIgnoreCase("epfl:contact")
				|| qName.equalsIgnoreCase("epfl:contact")) {
			if (this.mInItem && this.mItem != null) {
				String contact = current;
				this.mItem.setContact(contact);
			}
		} else if (localName.equalsIgnoreCase("epfl:language")
				|| qName.equalsIgnoreCase("epfl:language")) {
			if (this.mInItem && this.mItem != null) {
				String language = current;
				this.mItem.setLanguage(language);
			}
		} else if (localName.equalsIgnoreCase("epfl:audience")
				|| qName.equalsIgnoreCase("epfl:audience")) {
			if (this.mInItem && this.mItem != null) {
				String audience = current;
				this.mItem.setAudience(audience);
			}
		} else if (localName.equalsIgnoreCase("epfl:expectedPeople")
				|| qName.equalsIgnoreCase("epfl:expectedPeople")) {
			if (this.mInItem && this.mItem != null) {
				String expectedPeople = current;
				this.mItem.setExpectedPeople(expectedPeople);
			}
		} else if (localName.equalsIgnoreCase("epfl:location")
				|| qName.equalsIgnoreCase("epfl:location")) {
			if (this.mInItem && this.mItem != null) {
				String location = current;
				this.mItem.setLocation(location);
			}
		} else if (localName.equalsIgnoreCase("epfl:room")
				|| qName.equalsIgnoreCase("epfl:room")) {
			if (this.mInItem && this.mItem != null) {
				String room = current;
				this.mItem.setRoom(room);
			}
		} else if (localName.equalsIgnoreCase("epfl:category")
				|| qName.equalsIgnoreCase("epfl:category")) {
			if (this.mInItem && this.mItem != null) {
				String category = current;
				this.mItem.setCategory(category);
			}
		} else if (localName.equalsIgnoreCase("epfl:organizer")
				|| qName.equalsIgnoreCase("epfl:organizer")) {
			if (this.mInItem && this.mItem != null) {
				String organizerString = current;
				this.mItem.setOrganizer(organizerString);
			}
		} else if (localName.equalsIgnoreCase("epfl:shortTitle")
				|| qName.equalsIgnoreCase("epfl:shortTitle")) {
			if (this.mInItem && this.mItem != null) {
				String shortTitle = current;
				this.mItem.setShorttitle(shortTitle);
			}
		}
		this.mText = new StringBuilder();
	}

	/** Converts a date string to a long, containing the time in milliseconds */
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

	public static long addStartTime(long startDate, String startTime) {

		Date pubDate = null;
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
		try {
			String date = df.format(new Date(startDate)) + " " + startTime;
			pubDate = df.parse(date);
		} catch (ParseException e) {
			System.out.println("No date");
		}
		if (pubDate != null) {
			return pubDate.getTime();
		}
		return 0;
	}

	private String removeBadStuff(String s) {
		if (s.length() > 0) {
			s = s.replace("′", "'");
			s = s.replace("l?", "l'");
			s = s.replace("d?", "d'");
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

	public void characters(char[] ch, int start, int length) {
		this.mText.append(ch, start, length);
	}
}
