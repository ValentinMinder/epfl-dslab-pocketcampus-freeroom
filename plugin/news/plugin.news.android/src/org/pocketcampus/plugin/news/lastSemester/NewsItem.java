//package org.pocketcampus.plugin.news.lastSemester;
//
//import java.io.Serializable;
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.Locale;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//import android.graphics.drawable.Drawable;
//import android.text.Html;
//import android.text.Spanned;
//
///**
// * A class that describes a news item, to be displayed by the News plugins
// * 
// * @status complete, some attributes can be added.
// * @author Jonas, Johan
// *
// */
//
//public class NewsItem implements Serializable, Comparable<NewsItem> {
//
//	private static final long serialVersionUID = 1443922113610132660L;
//
//	private String title_;
//	private String description_;
//	private String link_;
//	private String pubDate_;
//	private Date pubDateDate_ = null;
//	private String image_;
//	private Drawable imageDrawable_;
//	private Spanned spannedDescription_;
//
//	// Used to get an image from the text
//	private final static Pattern imagePattern_ = Pattern.compile("<img.*src=\"?(\\S+).*>");
//
//	public NewsItem() { }
//	
//	public NewsItem(String title_, String description_, String link_,
//			String pubDate_, String image_) {
//		super();
//		this.title_ = title_;
//		this.description_ = description_;
//		this.link_ = link_;
//		this.pubDate_ = pubDate_;
//		this.image_ = image_;
//	}
//
//
//	public NewsItem clone() {
//		NewsItem fd = new NewsItem();
//		fd.title_ = this.title_;
//		fd.description_ = this.description_;
//		fd.link_ = this.link_;
//		fd.pubDate_ = this.pubDate_;
//		fd.image_ = this.image_;
//		return fd;
//	}
//
//	/**
//	 * Get a uri to show as content from the news item.
//	 * If we don't have any images, we try to find an <img> tag inside the description
//	 * @return an image URI
//	 */
//	public String getImageUri() {
//		//if we don't have any images, we try to find an <img> tag inside the description
//		if(image_ == null && description_ != null) {
//			Matcher m = imagePattern_.matcher(description_);
//			if(m.find()) {
//				String img = m.group(1);
//				if(img.charAt(img.length() - 1) == '\"')
//					img = img.substring(0, img.length() - 1);
//				image_ = img;
//			}
//		}
//		return image_;
//	}
//
//	public void setImage(String image) {
//		image_ = image;
//	}
//
//	public String getTitle() {
//		return title_;
//	}
//
//	public void setTitle(String title) {
//		this.title_ = title;
//	}
//
//	public String getDescription() {
//		return description_;
//	}
//
//	public void setDescription(String description) {
//		this.spannedDescription_ = null;
//		this.description_ = description;
//	}
//
//	public String getLink() {
//		return link_;
//	}
//
//	public void setLink(String link) {
//		this.link_ = link;
//	}
//
//	public String getPubDate() {
//		return pubDate_;
//	}
//	
//	public Date getPubDateDate() {
//		if(pubDateDate_ == null) {
//			//Try to parse the following format: Thu, 24 Mar 2011 06:17:28 +0100
//			//Date and time specification RFC 822
//			SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
//			try {
//				this.pubDateDate_ = sdf.parse(pubDate_);
//			} catch (ParseException e) {
//				//nothing to do
//			}
//		}
//		return pubDateDate_;
//	}
//
//	public void setPubDate(String pubDate) {
//		this.pubDateDate_ = null;
//		this.pubDate_ = pubDate;
//	}
//
//	public Drawable getImageDrawable() {
//		return imageDrawable_;
//	}
//
//	public void setImageDrawable(Drawable imageDrawable) {
//		this.imageDrawable_ = imageDrawable;
//	}
//	
//	/**
//	 * Get a unique ID for the news (based on the url)
//	 * @return
//	 */
//	public int getID() {
//		return this.link_.hashCode();
//	}
//	
//	@Override
//	public int compareTo(NewsItem another) {
//		try {
//			return another.getPubDateDate().compareTo(this.getPubDateDate());
//		} catch(NullPointerException e) {
//			return 0;
//		}
//	}
//	
//	/**
//	 * Returns a well formatted description. With formatted text and
//	 * without images.
//	 * @return the formatted description
//	 */
//	public Spanned getFormatedDescription() {
//		if(spannedDescription_ == null) {
//			String s = description_;
//			//convert the < and >
//			s = s.replaceAll("&lt;", "<");
//			s = s.replaceAll("&gt;", ">");
//			//remove the img tags
//			s = s.replaceAll("<img[^>]+>", "");
//			//trim
//			while(s.charAt(0) == ' ' || s.charAt(0) == '\r' || s.charAt(0) == '\n') {
//				s = s.substring(1);
//			}
//			spannedDescription_ = Html.fromHtml(s);
//		}
//		return spannedDescription_;
//	}
//
//}
