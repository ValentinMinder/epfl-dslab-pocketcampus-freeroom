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

import java.lang.reflect.Type;

import org.pocketcampus.R;
import org.pocketcampus.core.communication.DataRequest;
import org.pocketcampus.core.communication.RequestParameters;
import org.pocketcampus.core.parser.Json;
import org.pocketcampus.core.parser.JsonException;
import org.pocketcampus.plugin.logging.Tracker;
import org.pocketcampus.shared.plugin.food.Meal;
import org.pocketcampus.shared.plugin.food.Rating.SubmitStatus;

import android.app.Dialog;
import android.provider.Settings.Secure;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.Toast;

import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

/**
 * Class will represent a rating dialog, that will be opened when rating a meal.
 * 
 */
public class RatingsDialog extends Dialog {
	private Button okButton;
	private Button cancelButton;
	private RatingBar ratingbar;
	private Meal meal;
	private FoodPlugin menusActivity_;

	public RatingsDialog(Meal meal, FoodPlugin menusActivity) {
		super(menusActivity);
		this.meal = meal;
		this.menusActivity_ = menusActivity;
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

		Tracker.getInstance().trackPageView("food/ratingsDialog");
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
		menusActivity_.menuRefreshing();
		class SubmitRatingRequest extends DataRequest {
			@Override
			protected void doInUiThread(String result) {
				
				String submitted = "";
				if (result == null) {
					submitted = menusActivity_.getResources().getString(
							R.string.food_rating_notsubmitted);
				} else {
					Type ratingStatusType = new TypeToken<SubmitStatus>() {
					}.getType();

					SubmitStatus status = null;

					try {
						status = Json.fromJson(result, ratingStatusType);
					} catch (JsonSyntaxException e) {
						Log.d("SERVER", "Jsonsyntax");
						e.printStackTrace();
						return;
					} catch (JsonException e) {
						e.printStackTrace();
						return;
					}

					if (status == SubmitStatus.Valid) {
						submitted = menusActivity_.getResources().getString(
								R.string.food_rating_submitted);
						menusActivity_.getFoodDisplayHandler().refreshRatings();
					} else if (status == SubmitStatus.AlreadyVoted) {
						submitted = menusActivity_.getResources().getString(
								R.string.food_rating_alreadyvoted);
					} else if (status == SubmitStatus.Error) {
						submitted = menusActivity_.getResources().getString(
								R.string.food_rating_notsubmitted);
					} else if (status == SubmitStatus.TooEarly) {
						submitted = menusActivity_.getResources().getString(
								R.string.food_rating_tooearly);
					}
				}
				Toast.makeText(menusActivity_, submitted, Toast.LENGTH_SHORT)
						.show();
				menusActivity_.refreshed();
				ratingbar.invalidate();
			}
		}

		String deviceId = Secure.getString(getContext().getContentResolver(),
				Secure.ANDROID_ID);

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
