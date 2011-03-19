package org.pocketcampus.plugin.news;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.graphics.drawable.Drawable;
import android.util.Log;

/**
 * A class that describes a news item, to be displayed by the News plugins
 * 
 * @status complete, some attributes can be added.
 * @author jonas, johan
 * @license 
 *
 */

public class NewsItem implements Serializable, Comparable<NewsItem> {

	private static final long serialVersionUID = 1443922113610132660L;

	private String title_;
	private String description_;
	private String link_;
	private String pubDate_;
	private String image_;

	private Drawable imageDrawable_;

	private final static Pattern imagePattern_ = Pattern.compile("<img.*src=\"?(\\S+).*>");

	public NewsItem() { }



	public NewsItem(String title_, String description_, String link_,
			String pubDate_, String image_) {
		super();
		this.title_ = title_;
		this.description_ = description_;
		this.link_ = link_;
		this.pubDate_ = pubDate_;
		this.image_ = image_;
	}



	public NewsItem clone() {
		NewsItem fd = new NewsItem();
		fd.title_ = this.title_;
		fd.description_ = this.description_;
		fd.link_ = this.link_;
		fd.pubDate_ = this.pubDate_;
		fd.image_ = this.image_;
		return fd;
	}

	public String getImageUri() {
		//if we don't have any images, we try to find an <img> tag inside the description
		if(image_ == null && description_ != null) {
			Matcher m = imagePattern_.matcher(description_);
			if(m.find()) {
				String img = m.group(1);
				if(img.charAt(img.length() - 1) == '\"')
					img = img.substring(0, img.length() - 1);
				image_ = img;
			}
		}
		return image_;
	}

	public void setImage(String image) {
		image_ = image;
	}

	public String getTitle() {
		return title_;
	}

	public void setTitle(String title) {
		this.title_ = title;
	}

	public String getDescription() {
		return description_;
	}

	public String getDescriptionNoHtml() { //XXX maybe we want to have only a subsequence of the descritpion
		return htmlToText(description_);
	}

	/**
	 * Convert the String containing html tags and characters into text only string.
	 * @param s the string in html format.
	 * @return the string in text format.
	 */
	private String htmlToText(String s) {
		//XXX the function should be somewhere else
		//XXX maybe there is a more efficient way to do this
		s = s.replaceAll("\\<.*?\\>", "");
		s = s.replaceAll("&amp;", "&");
		s = s.replaceAll("&amp;", "&");
		s = s.replaceAll("&rsquo;", "'");
		s = s.replaceAll("&eacute;", "é");
		s = s.replaceAll("&egrave;", "è");
		s = s.replaceAll("&acirc;", "â");
		s = s.replaceAll("&agrave;", "à");
		s = s.replaceAll("&icirc;", "î");
		s = s.replaceAll("&raquo;", "»");
		s = s.replaceAll("&laquo;", "«");
		s = s.replaceAll("&ucirc;", "û");
		s = s.replaceAll("&ecirc;", "ê");
		s = s.replaceAll("&oelig;", "œ");
		s = s.replaceAll("&ocirc;", "ô");
		s = s.replaceAll("&ccedil;", "ç");
		s = s.replaceAll("&nbsp;", " ");
		s = s.replaceAll("&ugrave;", "ù");
		s = s.replaceAll("&ndash;", "-");
		s = s.replaceAll("&iuml;", "ï");
		s = s.replaceAll("&quot;", "\"");
		s = s.replaceAll("&hellip;", "...");
		s = s.replaceAll("&uuml;", "ü");
		s = s.replaceAll("&euml;", "ë");
		s = s.replaceAll("&deg;", "°");
		s = s.replaceAll("&ldquo;", "\"");
		s = s.replaceAll("&rdquo;", "\"");
		s = s.replaceAll("&auml;", "ä");
		s = s.replaceAll("&lt;", "<");
		s = s.replaceAll("&gt;", ">");
		s = s.replaceAll("&aacute;", "á");
		s = s.replaceAll("&bull;", "·");
		s = s.replaceAll("&Agrave;", "À");
		s = s.replaceAll("&ouml;", "ö");
		s = s.replaceAll("&copy;", "©");
		s = s.replaceAll("&thinsp;", " ");
		s = s.replaceAll("&Eacute;", "É");		
		s = s.replaceAll("\\<.*?\\>", "");
		return s;
	}

	public void setDescription(String description) {
		this.description_ = description;
	}

	public String getLink() {
		return link_;
	}

	public void setLink(String link) {
		this.link_ = link;
	}

	public String getPubDate() {
		return pubDate_;
	}

	public void setPubDate(String pubDate) {
		this.pubDate_ = pubDate;
	}

	public Drawable getImageDrawable() {
		return imageDrawable_;
	}

	public void setImageDrawable(Drawable imageDrawable) {
		this.imageDrawable_ = imageDrawable;
	}



	@Override
	public int compareTo(NewsItem another) {
		
		Log.d(this.getClass().toString(), "Compare");
		
		try {
			return this.getPubDate().compareTo(another.getPubDate());
		} catch(NullPointerException e) {
			return 0;
		}
	}

}
