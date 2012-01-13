package org.pocketcampus.plugin.bikes.android.ui;

import org.pocketcampus.R;
import org.pocketcampus.plugin.bikes.shared.BikeEmplacement;

import android.app.Dialog;
import android.content.Context;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;

/**
 * Custom dialog to show the details of a <code>BikeEmplacement</code>
 * @author Pascal <pascal.scheiben@gmail.com>
 *
 */
public class BikesStationDialog extends Dialog implements OnClickListener {

	/** Context of this Dialog*/
	Context ctx_;

	/** The <code>BikeEmplacament</code> that will be displayed*/
	BikeEmplacement displayedStation_;

	/** Textview containing the title*/
	TextView title_;
	/** Textview containing the number of empty bikes slots*/
	TextView empty_;
	/** Textview containing the number of available bikes*/
	TextView available_;
	/** Textview containing the link to the page containing more infos about the velopass system*/
	TextView link_;

	/**
	 * Constructor of the dialog. Calls the methods to set the layouts parameters and fill the view. 
	 * @param context Context of this dialog
	 * @param bikeEmplacement <code>BikeEmplacement</code> to be displayed.
	 */
	public BikesStationDialog(Context context, BikeEmplacement bikeEmplacement) {
		
		super(context);

		ctx_ = context;
		displayedStation_ = bikeEmplacement;

		build();
		setContent(bikeEmplacement);

	}

	/**
	 * Sets the features of the dialog's window
	 */
	private void build() {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.bikes_details_dialog);
		getWindow().setLayout(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		setCanceledOnTouchOutside(true);
	}

	/**
	 * Fill the textViews with the corresponding informations from the BikeEmplacement
	 * @param be <code>BikeEmplacement</code> to be displayed.
	 */
	private void setContent(BikeEmplacement be) {
		title_ = (TextView) findViewById(R.id.bikes_details_title_dialog);
		title_.setText(be.name);

		String thereIsAre;
		String emptySlots;
		if (be.numberOfEmptySpaces == 1) {
			thereIsAre = getString(R.string.bikes_there_is);
			emptySlots = getString(R.string.bikes_empty_slot);
		} else {
			thereIsAre = getString(R.string.bikes_there_are);
			emptySlots = getString(R.string.bikes_empty_slots);
		}

		String availableBikes;
		if (be.numberOfAvailableBikes == 1)
			availableBikes = getString(R.string.bikes_available_bike);
		else
			availableBikes = getString(R.string.bikes_available_bikes);

		available_ = (TextView) findViewById(R.id.bikes_textView_available);
		available_.setText(be.numberOfAvailableBikes + " " + availableBikes);
		//TODO what happens if there is one bike?
		available_.setText(Html.fromHtml(thereIsAre + " <b>"
				+ be.numberOfAvailableBikes + "</b> " + availableBikes + " <br>"
				+ getString(R.string.bikes_and) + " <b>"
				+ be.numberOfEmptySpaces + "</b> " + emptySlots + ".<br><br>"
				+ getString(R.string.bikes_infos_1)));

		link_ = (TextView) findViewById(R.id.bikes_textView_link);
		link_.setText(Html.fromHtml(" <a href=\""
				+ getString(R.string.bikes_website) + "\">"
				+ getString(R.string.bikes_infos_2) + "</a>"));
		
		link_.setMovementMethod(LinkMovementMethod.getInstance());

	}

	/**
	 * Get a displayed text via his ressource id
	 * @param resId The id of the wanted ressource.
	 * @return A <code>CharSequence</code> of the specified ressource
	 */
	private String getString(int resId) {
		return ctx_.getString(resId) + "";
	}

	/**
	 * clicklistener that does nothing
	 */
	@Override
	public void onClick(View v) {
		// do nothing

	}
}
