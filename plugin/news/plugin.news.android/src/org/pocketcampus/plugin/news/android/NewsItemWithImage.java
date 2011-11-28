package org.pocketcampus.plugin.news.android;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.pocketcampus.plugin.news.shared.NewsItem;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Html;

/**
 * Associates a NewsItem with its corresponding image.
 * 
 * @author Elodie <elodienilane.triponez@epfl.ch>
 * 
 */
public class NewsItemWithImage implements Serializable {
	/** Used to pass the NewsItem in an Intent */
	private static final long serialVersionUID = 1L;
	/** The NewsItem */
	private NewsItem mNewsItem;
	/** The Drawable image corresponding */
	private Drawable mDrawable;

	private String mSpannedDescription;

	/** Used to get an image from the text */
	private final static Pattern imagePattern_ = Pattern
			.compile("<img.*src=\"?(\\S+).*>");

	/**
	 * Constructor
	 * 
	 * @param newsItem
	 *            the NewsItem to which we want to associate an image
	 */
	public NewsItemWithImage(NewsItem newsItem) {
		this.mNewsItem = newsItem;
		this.mDrawable = null;
		setImageUri();
		setFormattedDescription();
	}

	public NewsItem getNewsItem() {
		return mNewsItem;
	}

	public void setNewsItem(NewsItem mNewsItem) {
		this.mNewsItem = mNewsItem;
	}

	public Drawable getDrawable() {
		return mDrawable;
	}

	public Bitmap getBitmapDrawable() {
		if (mDrawable != null) {
			return ((BitmapDrawable) mDrawable).getBitmap();
		} else
			return null;
	}

	public void setDrawable(Drawable mDrawable) {
		this.mDrawable = mDrawable;
	}

	/**
	 * Get a uri to show as content from the news item. If we don't have any
	 * images, we try to find an <img> tag inside the description
	 * 
	 * @return an image URI
	 */
	private void setImageUri() {
		// if we don't have any images, we try to find an <img> tag inside the
		// description
		if (mNewsItem.getImageUrl() == null
				&& mNewsItem.getDescription() != null) {
			Matcher m = imagePattern_.matcher(mNewsItem.getDescription());
			if (m.find()) {
				String img = m.group(1);
				if (img.charAt(img.length() - 1) == '\"')
					img = img.substring(0, img.length() - 1);
				mNewsItem.setImageUrl(img);
			}
		}
	}

	/**
	 * Returns a well formatted description. With formatted text and without
	 * images.
	 * 
	 * @return the formatted description
	 */
	public void setFormattedDescription() {
		if (mSpannedDescription == null) {
			String s = mNewsItem.getDescription();
			// convert the < and >
			s = s.replaceAll("&lt;", "<");
			s = s.replaceAll("&gt;", ">");
			// remove the img tags
			s = s.replaceAll("<img[^>]+>", "");
			// trim
			while (s.charAt(0) == ' ' || s.charAt(0) == '\r'
					|| s.charAt(0) == '\n') {
				s = s.substring(1);
			}
			mSpannedDescription = Html.fromHtml(s).toString();
		}
	}

	public String getFormattedDescription() {
		return mSpannedDescription;
	}
}
