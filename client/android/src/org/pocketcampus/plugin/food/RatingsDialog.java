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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.pocketcampus.R;
import org.pocketcampus.core.communication.RequestParameters;
import org.pocketcampus.core.communication.ServerRequest;
import org.pocketcampus.plugin.food.menu.RatingsReminder;
import org.pocketcampus.shared.plugin.food.Meal;
import org.pocketcampus.shared.plugin.food.Rating;
import org.pocketcampus.shared.plugin.food.Restaurant;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Toast;
import android.widget.RatingBar.OnRatingBarChangeListener;

/**
 * Class will represent a rating dialog, that will be opened
 * when rating a meal.
 *
 */
public class RatingsDialog extends Dialog {
	private Button okButton;
	private Button cancelButton;
	private RatingBar ratingbar;
	private Meal meal;
	
	public RatingsDialog(Context context, Meal meal) {
		super(context);
		this.meal = meal;
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
			submitRating((double)ratingbar.getRating());
			RatingsDialog.this.dismiss();
		}
	}
	
	// Load menu from server
	private void submitRating(double rating) {
		class SubmitRatingRequest extends ServerRequest {
			@Override
			protected void onPostExecute(String result) {
				//add the vote to the reminder
				RatingsReminder ratingChecker = new RatingsReminder(getContext());
				ratingChecker.addVotedPreferences();
				
				ratingbar.invalidate();
			}
		}
		
		RequestParameters params = new RequestParameters();
		Gson gson = new Gson();
		params.addParameter("meal", gson.toJson(meal));
		params.addParameter("rating", Double.toString(rating));
		FoodPlugin.getFoodRequestHandler().execute(new SubmitRatingRequest(), "setRating",
				(RequestParameters) params);
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
