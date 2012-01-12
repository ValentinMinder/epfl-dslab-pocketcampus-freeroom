/* 
 * The MIT License
 * Copyright (c) 2011 Paul Soucy (paul@dev-smart.com)
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 */

package org.pocketcampus.plugin.food.android.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.pocketcampus.plugin.food.shared.Meal;

import android.content.Context;
import android.util.Log;

/**
 * Class that handles caching the food menus for use when the server cannot be
 * reached, but the menus were already downloaded on the same day and are still
 * valid.
 * 
 * @author Elodie <elodienilane.triponez@epfl.ch>
 * @author Oriane <oriane.rodriguez@epfl.ch>
 * 
 */
public class FileCache {

	/** The context of the calling activity */
	private Context mContext;
	/**
	 * The date at which the menus were last successfully cached. Null if never.
	 */
	private Date mValidityDate;
	/** Name of the file that will be written to cache with the menus. */
	private final static String CACHE_NAME = "MenusCache";
	/** Tag corresponding to logs from this class. */
	private final static String LOG_TAG = "FileCache";

	/**
	 * Constructor.
	 * 
	 * @param context
	 *            the context of the calling activity.
	 */
	public FileCache(Context context) {
		mContext = context;
	}

	/**
	 * Returns the date at which the menus were last cached successfully. Null
	 * if never.
	 * 
	 * @return the validity date of the menus cache.
	 */
	public Date getValidityDate() {
		return mValidityDate;
	}

	/**
	 * Writes the menus to a cache file on the phone.
	 * 
	 * @param campusMenu
	 *            the menu to write to cache.
	 */
	public void writeToFile(List<Meal> campusMenu) {
		File menuFile = new File(mContext.getCacheDir(), CACHE_NAME);

		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());

		FileOutputStream fos = null;
		ObjectOutputStream out = null;
		try {
			fos = new FileOutputStream(menuFile);
			out = new ObjectOutputStream(fos);
			out.writeObject(cal.getTime());
			out.writeObject(campusMenu);
			out.close();
		} catch (IOException ex) {
			Log.d(LOG_TAG, "Unable to cache menus");
		}
	}

	/**
	 * Reads the menus from the cache, if there has been a prior successful
	 * writing of the menus to the cache.
	 * 
	 * @return the menus read from the cache; null if cache is not valid or
	 *         empty.
	 */
	@SuppressWarnings("unchecked")
	public List<Meal> readFromFile() {
		List<Meal> menu = null;
		File toGet = new File(mContext.getCacheDir(), CACHE_NAME);
		FileInputStream fis = null;
		ObjectInputStream in = null;
		Date date = null;
		try {
			fis = new FileInputStream(toGet);
			in = new ObjectInputStream(fis);
			date = (Date) in.readObject();
			Log.d("Date", date.toString());
			mValidityDate = date;

			menu = (List<Meal>) in.readObject();

			in.close();
		} catch (IOException ex) {
			ex.printStackTrace();
			return new ArrayList<Meal>();
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
			return new ArrayList<Meal>();
		} catch (ClassCastException ex) {
			ex.printStackTrace();
			return new ArrayList<Meal>();
		}
		if (isValidMenu()) {
			return menu;
		}
		return new ArrayList<Meal>();
	}

	/**
	 * Checks whether the menu written to the cache is valid, that is that it's
	 * not null and it was written on the same day as it has to be used.
	 * 
	 * @return whether the menu written is valid
	 */
	public boolean isValidMenu() {
		Calendar cal = Calendar.getInstance();
		Calendar validity = Calendar.getInstance();
		if (mValidityDate == null || validity == null) {
			return false;
		}
		validity.setTime(mValidityDate);
		if (cal.get(Calendar.DAY_OF_MONTH) == validity
				.get(Calendar.DAY_OF_MONTH)) {
			if (cal.get(Calendar.MONTH) == validity.get(Calendar.MONTH)) {
				if (cal.get(Calendar.YEAR) == validity.get(Calendar.YEAR)) {
					if (getMinutes(validity.getTime(), cal.getTime()) < 10) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * Gets the difference in minutes between two dates.
	 * 
	 * @param then
	 *            the first date.
	 * @param now
	 *            the second date.
	 * @return the difference.
	 */
	private long getMinutes(Date then, Date now) {
		long diff = now.getTime() - then.getTime();

		long diffMinutes = diff / (60 * 1000);

		Log.d(LOG_TAG, "" + diffMinutes);
		return diffMinutes;
	}
}