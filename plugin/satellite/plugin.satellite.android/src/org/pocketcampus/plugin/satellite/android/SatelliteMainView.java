package org.pocketcampus.plugin.satellite.android;

import org.pocketcampus.R;
import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginView;
import org.pocketcampus.android.platform.sdk.ui.element.ImageTextView;
import org.pocketcampus.android.platform.sdk.ui.labeler.IFeedViewLabeler;
import org.pocketcampus.android.platform.sdk.ui.labeler.ISubtitledFeedViewLabeler;
import org.pocketcampus.android.platform.sdk.ui.layout.StandardTitledScrollableDoubleLayout;
import org.pocketcampus.android.platform.sdk.utils.LoaderImageView;
import org.pocketcampus.plugin.satellite.android.iface.ISatelliteMainView;
import org.pocketcampus.plugin.satellite.android.ui.AffluenceImageView;
import org.pocketcampus.plugin.satellite.shared.Affluence;
import org.pocketcampus.plugin.satellite.shared.Beer;

import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;

/**
 * The Main View of the Satellite plugin, first displayed when accessing
 * Satellite.
 * 
 * Displays the beer of the month and the affluence at Satellite.
 * 
 * @author Oriane <oriane.rodriguez@epfl.ch>
 * 
 */
public class SatelliteMainView extends PluginView implements ISatelliteMainView {
	/** The Plugin Model */
	private SatelliteModel mModel;
	/** The Plugin Controller */
	private SatelliteController mController;
	/** A Standard Titled Layout */
	private StandardTitledScrollableDoubleLayout mLayout;

	/**
	 * Defines what the main controller is for this view.
	 */
	@Override
	protected Class<? extends PluginController> getMainControllerClass() {
		return SatelliteController.class;
	}

	/**
	 * Called once the view is connected to the controller. If you don't
	 * implement <code>getMainControllerClass()</code> then the controller given
	 * here will simply be <code>null</code>.
	 */
	@Override
	protected void onDisplay(Bundle savedInstanceState,
			PluginController controller) {
		mController = (SatelliteController) controller;
		mModel = (SatelliteModel) mController.getModel();

		mLayout = new StandardTitledScrollableDoubleLayout(this);
		mLayout.setTitle(getResources().getString(
				R.string.satellite_menu_main_page));

		setContentView(mLayout);
		showMainPage();
	}

	/**
	 * Displays the main Page of the Plugin, and refresh the data which has to
	 * be refreshed.
	 */
	public void showMainPage() {
		// Refreshed each time, since this information can change very fast
		mController.getAffluence();
		// To Do : test if the month is the same or not
		mController.getBeerOfMonth();
	}

	/**
	 * Main Satellite Options menu contains access to Beers, Events, Sandwiches
	 * and back to Main Page
	 */
	// @Override
	// public boolean onCreateOptionsMenu(Menu menu) {
	// MenuInflater inflater = getMenuInflater();
	// inflater.inflate(R.menu.satellite_menu, menu);
	// return true;
	// }

	/**
	 * Decides what happens when the options menu is opened and an option is
	 * chosen (what view to display)
	 */
	// @Override
	// public boolean onOptionsItemSelected(android.view.MenuItem item) {
	// /*
	// * if (item.getItemId() == R.id.satellite_main_page) {
	// * mLayout.removeFillerView(); mLayout.hideText();
	// * mLayout.setTitle(getResources
	// * ().getString(R.string.satellite_menu_main_page)); showMainPage(); }
	// * else if (item.getItemId() == R.id.satellite_beers) {
	// * mLayout.removeFillerView(); mLayout.hideText();
	// * mLayout.setTitle(getResources
	// * ().getString(R.string.satellite_menu_beers)); showBeers(); } else
	// */
	// if (item.getItemId() == R.id.satellite_events) {
	// Log.d("SATELLITE", "Starting Events Activity");
	// Intent events = new Intent(this, SatelliteEventsView.class);
	// startActivity(events);
	// } else if (item.getItemId() == R.id.satellite_sandwiches) {
	// Log.d("SATELLITE", "Starting Sandwiches Activity");
	// Intent sandwiches = new Intent(this, SatelliteSandwichesView.class);
	// startActivity(sandwiches);
	// }
	//
	// return true;
	// }

	/**
	 * Called when the beer of the month has been updated in the model.
	 * Refreshes the view with the new beer of the month.
	 */
	@Override
	public void beerUpdated() {
		Beer beer = mModel.getBeerOfMonth();
		if (beer != null) {
			ImageTextView t = new ImageTextView(beer, getApplicationContext(),
					mBeerLabeler, 0);
			mLayout.addSecondLayoutFillerView(t);
		}
	}

	/**
	 * Called when the affluence has been updated in the model. Refreshes the
	 * view with the new affluence at Satellite.
	 */
	@Override
	public void affluenceUpdated() {
		Log.d("SATELLITE", "Affluence updated (View)");

		Affluence a = mModel.getAffluence();

		if (a != null) {
			AffluenceImageView view = new AffluenceImageView(a, chooseImage(a),
					this, mAffluenceLabeler);

			mLayout.addFirstLayoutFillerView(view);
		}
	}

	/**
	 * Displays a message when an error happens upon contacting the server
	 */
	@Override
	public void networkErrorHappened() {
		mLayout.removeFirstLayoutFillerView();
		mLayout.removeSecondLayoutFillerView();
		mLayout.setText(getResources().getString(
				R.string.satellite_nothing_to_display));
	}

	/**
	 * Matches the right description with the right Affluence
	 * 
	 * @param affluence
	 * @return dscr the description corresponding to the affluence
	 */
	private String chooseDescription(Affluence affluence) {
		String dscr = "";

		switch (affluence) {
		case EMPTY:
			dscr = getResources().getString(R.string.satellite_affluence_empty);
			break;
		case MEDIUM:
			dscr = getResources()
					.getString(R.string.satellite_affluence_medium);
			break;
		case CROWDED:
			dscr = getResources().getString(
					R.string.satellite_affluence_crowded);
			break;
		case FULL:
			dscr = getResources().getString(R.string.satellite_affluence_full);
			break;
		case CLOSED:
			dscr = getResources()
					.getString(R.string.satellite_affluence_closed);
			break;
		case ERROR:
			dscr = getResources().getString(R.string.satellite_affluence_error);
			break;
		default:
			dscr = getResources().getString(R.string.satellite_affluence_error);
			break;
		}

		return dscr;
	}

	/**
	 * Returns the corresponding image resource for the current affluence at
	 * Satellite.
	 * 
	 * @param affluence
	 *            The current affluence
	 * @return The corresponding image resource
	 */
	private int chooseImage(Affluence affluence) {
		int img = R.drawable.satellite_affluence_empty;

		switch (affluence) {
		case EMPTY:
			img = R.drawable.satellite_affluence_empty;
			break;
		case MEDIUM:
			img = R.drawable.satellite_affluence_empty;
			break;
		case CROWDED:
			img = R.drawable.satellite_affluence_empty;
			break;
		case FULL:
			img = R.drawable.satellite_affluence_empty;
			break;
		case CLOSED:
			img = R.drawable.satellite_affluence_closed;
			break;
		case ERROR:
			img = R.drawable.satellite_affluence_closed;
			break;
		default:
			img = R.drawable.satellite_affluence_closed;
			break;
		}

		return img;
	}

	/**
	 * The labeler for the affluence, to tell how it has to be displayed in a
	 * generic view.
	 */
	IFeedViewLabeler<Affluence> mAffluenceLabeler = new IFeedViewLabeler<Affluence>() {

		/**
		 * Returns the affluence title
		 * 
		 * @param affluence
		 *            The affluence to be displayed
		 * @return The affluence title
		 */
		@Override
		public String getTitle(Affluence affluence) {
			return getResources().getString(R.string.satellite_affluence);
		}

		/**
		 * Returns the affluence description
		 * 
		 * @param affluence
		 *            The affluence to be displayed
		 * @return The affluence description
		 */
		@Override
		public String getDescription(Affluence affluence) {
			return chooseDescription(affluence);
		}

		/**
		 * Returns the affluence picture
		 * 
		 * @param affluence
		 *            The affluence to be displayed
		 * @return The affluence picture as a LinearLayout
		 */
		@Override
		public LinearLayout getPictureLayout(Affluence obj) {
			return null;
		}

	};

	/**
	 * The labeler for a Beer, to tell how it has to be displayed in a generic
	 * view.
	 */
	ISubtitledFeedViewLabeler<Beer> mBeerLabeler = new ISubtitledFeedViewLabeler<Beer>() {

		/**
		 * Returns the beer title
		 * 
		 * @param beer
		 *            The beer to be displayed
		 * @return The beer title
		 */
		@Override
		public String getTitle(Beer beer) {
			return getResources().getString(R.string.satellite_beer_of_month);
		}

		/**
		 * Returns the beer name
		 * 
		 * @param beer
		 *            The beer to be displayed
		 * @return The beer name
		 */
		@Override
		public String getSubtitle(Beer beer) {
			return beer.getName();
		}

		/**
		 * Returns the beer description
		 * 
		 * @param beer
		 *            The beer to be displayed
		 * @return The beer description
		 */
		@Override
		public String getDescription(Beer beer) {
			return beer.getDescription();
		}

		/**
		 * Returns the beer picture
		 * 
		 * @param beer
		 *            The beer to be displayed
		 * @return The beer picture as a LinearLayout
		 */
		@Override
		public LinearLayout getPictureLayout(Beer beer) {
			return new LoaderImageView(getApplicationContext(),
					beer.getPictureUrl());
		}

	};
}