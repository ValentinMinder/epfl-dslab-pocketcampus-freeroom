package org.pocketcampus.plugin.news.android;

import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginView;
import org.pocketcampus.android.platform.sdk.ui.layout.FeedInformationLayout;
import org.pocketcampus.plugin.news.android.iface.INewsModel;

import android.os.Bundle;
import android.util.Log;

public class NewsItemView extends PluginView {
	private NewsController mController;
	private INewsModel mModel;
	private NewsItemWithImage mNewsItem;

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

		if (mNewsItem != null) {
			mLayout.setTitle(mNewsItem.getNewsItem().getTitle());
			mLayout.setImage(mNewsItem.getDrawable());
			mLayout.setDescription(mNewsItem.getNewsItem().getDescription());
		} else {
			mLayout.setTitle("I\'m not done yet!");
			mLayout.setDescription("So why did you click on a news?");
		}
	}

	/**
	 * Handle extras from the MainView
	 */
	private void handleExtras() {
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			mNewsItem = (NewsItemWithImage) extras
					.getSerializable("org.pocketcampus.news.newsItem");
		} else {
			Log.d("NEWSITEMVIEW", "No extras received!");
		}
	}
}
