package org.pocketcampus.plugin.events.android;

import java.io.Serializable;

import org.pocketcampus.plugin.events.shared.EventsItem;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Html;

/**
 * Associates a EventsItem with its corresponding image.
 * 
 * @author Elodie <elodienilane.triponez@epfl.ch>
 * 
 */
public class EventsItemWithImage implements Serializable {
	/** Used to pass the EventsItem in an Intent */
	private static final long serialVersionUID = 1L;
	/** The EventsItem */
	private EventsItem mEventsItem;
	/** The Drawable image corresponding */
	private Drawable mDrawable;

	private String mSpannedDescription;

	/**
	 * Constructor
	 * 
	 * @param eventsItem
	 *            the EvensItem to which we want to associate an image
	 */
	public EventsItemWithImage(EventsItem eventsItem) {
		this.mEventsItem = eventsItem;
		this.mDrawable = null;
		setFormattedDescription();
	}

	public EventsItem getEventsItem() {
		return mEventsItem;
	}

	public void setEventsItem(EventsItem mEventsItem) {
		this.mEventsItem = mEventsItem;
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
	 * Returns a well formatted description. With formatted text and without
	 * images.
	 * 
	 * @return the formatted description
	 */
	public void setFormattedDescription() {
		if (mSpannedDescription == null) {
				String s = mEventsItem.getContent();
			if (s != null && !s.equals("")) {
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
			}
			mSpannedDescription = Html.fromHtml(s).toString();
		}
	}

	public String getFormattedDescription() {
		return mSpannedDescription;
	}
}
