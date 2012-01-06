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
	/** Used to pass the NewsItem in an Intent. */
	private static final long serialVersionUID = 1L;
	/** The NewsItem. */
	private NewsItem mNewsItem;
	/** The Drawable image corresponding to the NewsItem. */
	private Drawable mDrawable;

	/**
	 * Constructor.
	 * 
	 * @param newsItem
	 *            the NewsItem to which we want to associate an image.
	 */
	public NewsItemWithImage(NewsItem newsItem) {
		this.mNewsItem = newsItem;
		this.mDrawable = null;
	}

	/**
	 * @return the NewsItem encapsulated by this object
	 */
	public NewsItem getNewsItem() {
		return mNewsItem;
	}

	/**
	 * Specify the NewsItem.
	 * 
	 * @param mNewsItem
	 *            the new NewsItem.
	 */
	public void setNewsItem(NewsItem mNewsItem) {
		this.mNewsItem = mNewsItem;
	}

	/**
	 * @return The Drawable associated to the NewsItem
	 */
	public Drawable getDrawable() {
		return mDrawable;
	}

	/**
	 * @return the Bitmap corresponding to the Drawable
	 */
	public Bitmap getBitmapDrawable() {
		if (mDrawable != null) {
			return ((BitmapDrawable) mDrawable).getBitmap();
		} else
			return null;
	}

	/**
	 * Specify the Drawable associated to the NewsItem.
	 * 
	 * @param mDrawable
	 */
	public void setDrawable(Drawable mDrawable) {
		this.mDrawable = mDrawable;
	}

}
