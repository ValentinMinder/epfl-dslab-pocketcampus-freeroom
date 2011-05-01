/*
 ********************* [ P O C K E T C A M P U S ] *****************
 * [    LICENCE    ]    see "licence"-file in the root directory
 * [   MAINTAINER  ]    ElodieNilane.Triponez@epfl.ch
 * [     STATUS    ]    Usable
 *
 **************************[ C O M M E N T S ]**********************
 *
 * A class to display a dialog to rate individual menus.  
 *                      
 *******************************************************************
 */

package org.pocketcampus.plugin.food;

import org.pocketcampus.R;
import org.pocketcampus.core.communication.DataRequest;
import org.pocketcampus.core.communication.RequestParameters;
import org.pocketcampus.plugin.food.menu.RatingsReminder;
import org.pocketcampus.shared.plugin.food.Meal;

import android.app.Dialog;
import android.content.Context;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Toast;
import android.widget.RatingBar.OnRatingBarChangeListener;

/**
 * Class will represent a rating dialog, that will be opened when rating a meal.
 * 
 */
public class RatingsDialog extends Dialog {
	private Button okButton;
	private Button cancelButton;
	private RatingBar ratingbar;
	private Meal meal;
	private Context ctx_;

	public RatingsDialog(Context context, Meal meal) {
		super(context);
		this.meal = meal;
		this.ctx_ = context;

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		/** Design the dialog in main.xml file */
		setContentView(R.layout.food_dialog_rating);

		okButton = (Button) findViewById(R.id.food_rating_submit);
		okButton.setEnabled(false);
		okButton.setOnClickListener(new OKListener());

		cancelButton = (Button) findViewById(R.id.food_rating_cancel_button);
		cancelButton.setOnClickListener(new CancelListener());

		ratingbar = (RatingBar) findViewById(R.id.food_rating_ratebar);
		ratingbar.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
			public void onRatingChanged(RatingBar ratingBar, float rating,
					boolean fromUser) {
				okButton.setEnabled(true);
			}
		});

	}

	/**
	 * Called when OK button is clicked
	 * 
	 */
	private class OKListener implements android.view.View.OnClickListener {
		public void onClick(View v) {
			submitRating((double) ratingbar.getRating());
			RatingsDialog.this.dismiss();
		}
	}

	// Submit Rating to the server.
	private void submitRating(double rating) {
		class SubmitRatingRequest extends DataRequest {
			@Override
			protected void doInUiThread(String result) {
				String submitted = "";
				if (result.contains("true")) {
					submitted = ctx_.getResources().getString(
							R.string.food_rating_submitted);
				} else {
					submitted = ctx_.getResources().getString(
							R.string.food_rating_notsubmitted);
				}
				Toast.makeText(ctx_, submitted, Toast.LENGTH_SHORT).show();
				// add the vote to the reminder
				RatingsReminder ratingChecker = new RatingsReminder(
						getContext());
				ratingChecker.addVotedPreferences();

				ratingbar.invalidate();
			}
		}

		String deviceId = Settings.Secure.ANDROID_ID;

		RequestParameters params = new RequestParameters();

		params.addParameter("meal", Integer.toString(meal.hashCode()));
		params.addParameter("rating", Double.toString(rating));
		params.addParameter("deviceId", deviceId);
		
		FoodPlugin.getFoodRequestHandler().execute(new SubmitRatingRequest(),
				"setRating", (RequestParameters) params);
	}

	/**
	 * Called when cancel button is clicked - simply dismiss the dialog.
	 * 
	 */
	private class CancelListener implements android.view.View.OnClickListener {
		public void onClick(View v) {
			RatingsDialog.this.dismiss();
		}
	}
}
