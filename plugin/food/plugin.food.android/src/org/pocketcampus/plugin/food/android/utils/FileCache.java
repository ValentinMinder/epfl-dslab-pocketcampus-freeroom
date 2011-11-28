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

public class FileCache {

	private Context mContext;
	private Date mValidityDate;
	private final static String CACHE_NAME = "MenusCache";
	private final static String LOG_TAG = "FileCache";

	public FileCache(Context ctx_) {
		mContext = ctx_;
	}

	public Date getValidityDate() {
		return mValidityDate;
	}

	public void writeToFile(List<Meal> campusMenu_) {
		File menuFile = new File(mContext.getCacheDir(), CACHE_NAME);

		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());

		FileOutputStream fos = null;
		ObjectOutputStream out = null;
		try {
			fos = new FileOutputStream(menuFile);
			out = new ObjectOutputStream(fos);
			out.writeObject(cal.getTime());
			out.writeObject(campusMenu_);
			out.close();
		} catch (IOException ex) {
			// Toast.makeText(mContext, "Writing IO Exception",
			// Toast.LENGTH_SHORT)
			// .show();
		}
	}

	@SuppressWarnings("unchecked")
	public List<Meal> restoreFromFile() {
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

	public boolean isValidMenu() {
		Calendar cal = Calendar.getInstance();
		Calendar validity = Calendar.getInstance();
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

	private long getMinutes(Date then, Date now) {
		long diff = now.getTime() - then.getTime();

		long diffMinutes = diff / (60 * 1000);

		Log.d(LOG_TAG, "" + diffMinutes);
		return diffMinutes;
	}

}