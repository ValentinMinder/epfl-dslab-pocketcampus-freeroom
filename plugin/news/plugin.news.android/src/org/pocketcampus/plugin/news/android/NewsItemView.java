package org.pocketcampus.plugin.news.android;

import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginView;
import org.pocketcampus.android.platform.sdk.ui.layout.FeedInformationLayout;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;

/**
 * Displays the full information for a single NewsItem.
 * 
 * @author Elodie <elodienilane.triponez@epfl.ch>
 */

public class NewsItemView extends PluginView {
	/** The title of the NewsItem. */
	private String mTitle;
	/** The description of the NewsItem. */
	private String mDescription;
	/** The feed the NewsItem is from. */
	private String mFeed;
	/** The image corresponding to the NewsItem. */
	private Bitmap mBitmap;
	/** The page's layout. */
	private FeedInformationLayout mLayout;

	/**
	 * Defines what the main controller is for this view. This is optional, some
	 * view may not need a controller (see for example the dashboard).
	 * 
	 * This is only a shortcut for what is done in
	 * <code>getOtherController()</code> below: if you know you'll need a
	 * controller before doing anything else in this view, you can define it as
	 * you're main controller so you know it'll be ready as soon as
	 * <code>onDisplay()</code> is called.
	 */
	@Override
	protected Class<? extends PluginController> getMainControllerClass() {
		return NewsController.class;
	}

	/**
	 * Called once the view is connected to the controller. If you don't
	 * implement <code>getMainControllerClass()</code> then the controller given
	 * here will simply be <code>null</code>.
	 */
	@Override
	protected void onDisplay(Bundle savedInstanceState,
			PluginController controller) {

		mLayout = new FeedInformationLayout(this, null);

		// The ActionBar is added automatically when you call setContentView
		setContentView(mLayout);

		handleExtras();

		if (mTitle != null) {
			mLayout.setTitle(mTitle);
		}
		if (mDescription != null) {
			mLayout.setDescription(mDescription);
		}
		if (mFeed != null) {
			mLayout.setFeedTitle(mFeed);
		}
		if (mBitmap != null) {
			mLayout.setImage(mBitmap);
		}
	}

	/**
	 * Handle extras from the MainView. Retrieves the title, description, image
	 * and feed for the NewsItem passed in the Intent Extras.
	 */
	private void handleExtras() {
		Bundle extras = getIntent().getExtras();
		System.out.println("Handling extra: " + extras);
		if (extras != null) {
			mTitle = (String) extras
					.getSerializable("org.pocketcampus.news.newsitem.title");
			mDescription = (String) extras
					.getSerializable("org.pocketcampus.news.newsitem.description");
			mBitmap = (Bitmap) this.getIntent().getParcelableExtra(
					"org.pocketcampus.news.newsitem.bitmap");
			mFeed = (String) extras
					.getSerializable("org.pocketcampus.news.newsitem.feed");

		} else {
			Log.d("NEWSITEMVIEW", "No extras received!");
		}
	}
}
