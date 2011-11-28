package org.pocketcampus.plugin.news.android;

import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginView;
import org.pocketcampus.android.platform.sdk.ui.layout.FeedInformationLayout;
import org.pocketcampus.plugin.news.android.iface.INewsModel;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

/**
 * 
 * @author Elodie <elodienilane.triponez@epfl.ch>
 */

public class NewsItemView extends PluginView {
	private NewsController mController;
	private INewsModel mModel;
	private String mTitle;
	private String mDescription;
	private String mFeed;
	private Bitmap mBitmap;
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

		// Get and cast the controller and model
		mController = (NewsController) controller;
		mModel = (NewsModel) controller.getModel();

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
			Toast.makeText(this, mFeed, Toast.LENGTH_SHORT).show();
			mLayout.setDescription(mDescription);
		}
		if (mBitmap != null) {
			mLayout.setImage(mBitmap);
		}
		// } else {
		// mLayout.setTitle("I\'m not done yet!");
		// mLayout.setDescription("So why did you click on a news?");
		// }
	}

	/**
	 * Handle extras from the MainView
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
