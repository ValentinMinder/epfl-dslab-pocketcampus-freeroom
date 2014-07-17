package org.pocketcampus.plugin.satellite.android;

import org.pocketcampus.platform.android.core.PluginController;
import org.pocketcampus.platform.android.core.PluginView;
import org.pocketcampus.platform.android.ui.element.ImageTextView;
import org.pocketcampus.platform.android.ui.labeler.IFeedViewLabeler;
import org.pocketcampus.platform.android.ui.labeler.ISubtitledFeedViewLabeler;
import org.pocketcampus.platform.android.ui.layout.StandardTitledScrollableDoubleLayout;
import org.pocketcampus.platform.android.utils.LoaderImageView;
import org.pocketcampus.plugin.satellite.R;
import org.pocketcampus.plugin.satellite.android.iface.ISatelliteMainView;
import org.pocketcampus.plugin.satellite.android.ui.AffluenceImageView;
import org.pocketcampus.plugin.satellite.shared.Affluence;
import org.pocketcampus.plugin.satellite.shared.Beer;

import android.os.Bundle;
import android.widget.LinearLayout;

/**
 * The main view of the Satellite plugin, first displayed when accessing
 * Satellite. Displays the beer of the month and the current affluence at
 * Satellite.
 * 
 * @author Oriane <oriane.rodriguez@epfl.ch>
 */
public class SatelliteMainView extends PluginView implements ISatelliteMainView {
	/** The plugin model. */
	private SatelliteModel mModel;
	/** The plugin controller. */
	private SatelliteController mController;
	/** The main titled layout. */
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
	 * implement the <code>getMainControllerClass()</code> method, then the
	 * controller given here will simply be <code>null</code>.
	 */
	@Override
	protected void onDisplay(Bundle savedInstanceState,
			PluginController controller) {

		// MVC
		mController = (SatelliteController) controller;
		mModel = (SatelliteModel) mController.getModel();

		// Initialize the main layout
		mLayout = new StandardTitledScrollableDoubleLayout(this);
		mLayout.setTitle(getResources().getString(
				R.string.satellite_menu_main_page));
		mLayout.setText(getResources().getString(R.string.satellite_loading));

		setContentView(mLayout);
		loadData();
		setActionBarTitle(getString(R.string.satellite_plugin_name));
	}
	
	@Override
	protected String screenName() {
		return "/satellite";
	}

	/**
	 * Asks the controller for the affluence and the beer of the month.
	 */
	public void loadData() {
		mController.getAffluence();
		mController.getBeerOfMonth();
	}

	/**
	 * Called when the beer of the month has been updated in the model.
	 * Refreshes the view with the new beer of the month at Satellite.
	 */
	@Override
	public void beerUpdated() {
		Beer beer = mModel.getBeerOfMonth();
		if (beer != null) {
			mLayout.hideText();
			ImageTextView t = new ImageTextView(beer, getApplicationContext(),
					mBeerLabeler);
			mLayout.addSecondLayoutFillerView(t);
		} else {
			mLayout.setText(getResources().getString(
					R.string.satellite_no_beer_to_display));
		}
	}

	/**
	 * Called when the affluence has been updated in the model. Refreshes the
	 * view with the new affluence at Satellite.
	 */
	@Override
	public void affluenceUpdated() {
		Affluence a = mModel.getAffluence();
		if (a != null) {
			// mLayout.hideText();
			AffluenceImageView view = new AffluenceImageView(a, chooseImage(a),
					this, mAffluenceLabeler);

			mLayout.addFirstLayoutFillerView(view);
		} else {
			mLayout.setText(getResources().getString(
					R.string.satellite_nothing_to_display));
		}
	}

	/**
	 * Displays a message when an error happens when contacting the server.
	 */
	@Override
	public void networkErrorHappened() {
		mLayout.removeFirstLayoutFillerView();
		mLayout.removeSecondLayoutFillerView();
		mLayout.setText(getResources().getString(
				R.string.satellite_nothing_to_display));
	}

	/**
	 * Matches the right description with the right affluence.
	 * 
	 * @param affluence
	 *            The affluence for which we want the description.
	 * @return description The description corresponding to the affluence.
	 */
	private String chooseDescription(Affluence affluence) {
		String description = "";

		switch (affluence) {
		case EMPTY:
			description = getResources().getString(
					R.string.satellite_affluence_empty);
			break;
		case MEDIUM:
			description = getResources().getString(
					R.string.satellite_affluence_medium);
			break;
		case CROWDED:
			description = getResources().getString(
					R.string.satellite_affluence_crowded);
			break;
		case FULL:
			description = getResources().getString(
					R.string.satellite_affluence_full);
			break;
		case CLOSED:
			description = getResources().getString(
					R.string.satellite_affluence_closed);
			break;
		case ERROR:
			description = getResources().getString(
					R.string.satellite_affluence_error);
			break;
		default:
			description = getResources().getString(
					R.string.satellite_affluence_error);
			break;
		}

		return description;
	}

	/**
	 * Returns the corresponding image resource for the current affluence at
	 * Satellite.
	 * 
	 * @param affluence
	 *            The current affluence.
	 * @return The corresponding image resource.
	 */
	private int chooseImage(Affluence affluence) {
		int img;

		switch (affluence) {
		case EMPTY:
			img = R.drawable.satellite_affluence_empty;
			break;
		case MEDIUM:
			img = R.drawable.satellite_affluence_medium;
			break;
		case CROWDED:
			img = R.drawable.satellite_affluence_crowded;
			break;
		case FULL:
			img = R.drawable.satellite_affluence_full;
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
	 * The labeler for an <code>Affluence</code> object, telling how it has to
	 * be displayed in a generic view.
	 */
	IFeedViewLabeler<Affluence> mAffluenceLabeler = new IFeedViewLabeler<Affluence>() {

		/**
		 * Returns the affluence title.
		 * 
		 * @param affluence
		 *            The affluence to be displayed.
		 * @return The affluence title.
		 */
		@Override
		public String getTitle(Affluence affluence) {
			return getResources().getString(R.string.satellite_affluence);
		}

		/**
		 * Returns the affluence description.
		 * 
		 * @param affluence
		 *            The affluence to be displayed.
		 * @return The affluence description.
		 */
		@Override
		public String getDescription(Affluence affluence) {
			return chooseDescription(affluence);
		}

		/**
		 * Returns the affluence picture.
		 * 
		 * @param affluence
		 *            The affluence to be displayed.
		 * @return The affluence picture as a LinearLayout.
		 */
		@Override
		public LinearLayout getPictureLayout(Affluence obj) {
			return null;
		}

	};

	/**
	 * The labeler for a <code>Beer</code> object, telling how it has to be
	 * displayed in a generic view.
	 */
	ISubtitledFeedViewLabeler<Beer> mBeerLabeler = new ISubtitledFeedViewLabeler<Beer>() {

		/**
		 * Returns the beer title.
		 * 
		 * @param beer
		 *            The beer to be displayed.
		 * @return The beer title.
		 */
		@Override
		public String getTitle(Beer beer) {
			return getResources().getString(R.string.satellite_beer_of_month);
		}

		/**
		 * Returns the beer name.
		 * 
		 * @param beer
		 *            The beer to be displayed.
		 * @return The beer name.
		 */
		@Override
		public String getSubtitle(Beer beer) {
			return beer.getName();
		}

		/**
		 * Returns the beer description.
		 * 
		 * @param beer
		 *            The beer to be displayed.
		 * @return The beer description.
		 */
		@Override
		public String getDescription(Beer beer) {
			return beer.getDescription();
		}

		/**
		 * Returns the beer picture.
		 * 
		 * @param beer
		 *            The beer to be displayed.
		 * @return The beer picture as a LinearLayout.
		 */
		@Override
		public LinearLayout getPictureLayout(Beer beer) {
			if(!beer.getPictureUrl().equals("")) {
				
			return new LoaderImageView(getApplicationContext(),
					beer.getPictureUrl());
			} else {
				return null;
			}
		}

	};
}