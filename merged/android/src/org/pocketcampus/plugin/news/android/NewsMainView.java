package org.pocketcampus.plugin.news.android;

import java.util.List;

import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginView;
import org.pocketcampus.android.platform.sdk.ui.layout.StandardLayout;
import org.pocketcampus.plugin.news.android.iface.INewsModel;
import org.pocketcampus.plugin.news.android.iface.INewsView;
import org.pocketcampus.plugin.news.gui.FeedListViewElement;
import org.pocketcampus.plugin.news.shared.NewsItem;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AbsListView.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

public class NewsMainView extends PluginView implements INewsView {
	private NewsController mController;
	private INewsModel mModel;

	private StandardLayout mLayout;
	private FeedListViewElement mListView;

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

		// The StandardLayout is a RelativeLayout with a TextView in its center.
		mLayout = new StandardLayout(this, null);

		LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		mLayout.setLayoutParams(layoutParams);
		mLayout.setGravity(Gravity.CENTER_VERTICAL);

		// The ActionBar is added automatically when you call setContentView
		setContentView(mLayout);

		mController.loadNews();

		// We need to force the display before asking the controller for the
		// data,
		// as the controller may take some time to get it.
		displayData();
	}

	private void displayData() {

		List<NewsItem> newsList = mModel.getNews();
		if (newsList != null) {
			/** -----------StartCopy----------- **/

			// Add them to the listView
			mListView = new FeedListViewElement(this, newsList);

			// Set onClickListener
			setOnListViewClickListener();

			// Set the layout
			mLayout.addView(mListView);

			/** -----------End copy----------- */
			mLayout.setText("");
		} else {
			mLayout.setText("No news");
		}
	}

	@Override
	public void newsUpdated() {
		displayData();
	}

	@Override
	public void networkErrorHappened() {
		// TODO Auto-generated method stub

	}

	/* Sets the clickLIstener of the listView */
	private void setOnListViewClickListener() {

		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
			}

		});

	}
}
