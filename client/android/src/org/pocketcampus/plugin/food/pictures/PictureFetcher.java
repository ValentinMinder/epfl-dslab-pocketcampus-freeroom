package org.pocketcampus.plugin.food.pictures;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.Iterator;

import org.pocketcampus.R;
import org.pocketcampus.shared.plugin.food.Meal;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

public class PictureFetcher {

	private Collection<String> imagesURLs_;
	private Meal meal_;
	private boolean isMealGallery_;
	private String[] myRemotePictures_;
	private Drawable[] pictures_;

	public PictureFetcher(Bundle bundle) {
		if (bundle == null) {
			throw new IllegalArgumentException("Bundle cannot be null.");
		}
		meal_ = null;
		if (bundle.containsKey("meal")) {
			meal_ = (Meal) bundle.get("meal");
			if (bundle.containsKey("isMealGallery")) {
				isMealGallery_ = true;
			} else {
				isMealGallery_ = false;
			}
			fetchPictureUrls();
		}
	}

	/**
	 * Fetch pictures corresponding to meal from the server.
	 */
	private void fetchPictureUrls() {
		// Get all url of a meal
		// ServerAPI server = new ServerAPI();
		try {
			if (isMealGallery_) {
				// imagesURLs_ = server.getMealPictures(meal_);
			} else {
				// String queue = server.getQueuePicture(meal_.getRestaurant());
				// imagesURLs_ = new Vector<String>();
				// imagesURLs_.add(queue);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (imagesURLs_.size() == 0) {
			// mean there is no picture for now
			// printNoPictureFound(this);
		} else {
			myRemotePictures_ = new String[imagesURLs_.size()];
			Iterator<String> iter = imagesURLs_.iterator();
			int index = 0;
			while (iter.hasNext()) {
				myRemotePictures_[index] = iter.next();
				index++;
			}
		}

	}

	/**
	 * Returns the array of pictures that have been fetched from the server.
	 */
	public Drawable[] getPictures() {
		return pictures_;
	}

	private void makePictureArray() {
		pictures_ = new Drawable[imagesURLs_.size()];
		for (int picIndex = 0; picIndex < myRemotePictures_.length; picIndex++) {
			try {
				pictures_[picIndex] = getDrawableFromUrl(myRemotePictures_[picIndex]);
			} catch (IOException e) {
			}
		}
	}

	/**
	 * Pass in an image url to get a drawable object
	 * 
	 * @return a drawable object
	 * @throws IOException
	 * @throws MalformedURLException
	 */
	private static Drawable getDrawableFromUrl(final String url)
			throws IOException, MalformedURLException {
		return Drawable
				.createFromStream(((java.io.InputStream) new java.net.URL(url)
						.getContent()), url);
	}
}
