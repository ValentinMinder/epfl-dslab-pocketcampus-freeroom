package org.pocketcampus.utils;

import java.io.IOException;
import java.net.MalformedURLException;

import android.graphics.drawable.Drawable;

public class ImageUtil {

	/**
	 * Pass in an image url to get a drawable object
	 * @return a drawable object
	 * @throws IOException
	 * @throws MalformedURLException
	 */
	public static Drawable getDrawableFromUrl(final String url) throws IOException, MalformedURLException {
		return Drawable.createFromStream(((java.io.InputStream)new java.net.URL(url).getContent()), url);
	}
}
