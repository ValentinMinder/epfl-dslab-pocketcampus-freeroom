package org.pocketcampus.plugin.news.android;

import java.io.Serializable;

import org.pocketcampus.plugin.news.shared.NewsItem;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

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

	/**
	 * Constructor
	 * 
	 * @param newsItem
	 *            the NewsItem to which we want to associate an image
	 */
	public NewsItemWithImage(NewsItem newsItem) {
		this.mNewsItem = newsItem;
		this.mDrawable = null;
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

}
