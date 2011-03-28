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
import org.pocketcampus.plugin.food.menu.Meal;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
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
			/*ServerAPI sapi = new ServerAPI();
			try {
				sapi.addRating(meal, Restaurant.doubleToStarRating((double)ratingbar.getRating()));
				
				//add the vote to the reminder
				RatingsReminder ratingChecker = new RatingsReminder(getContext());
				ratingChecker.addVotedPreferences();
				
				ratingbar.invalidate();
			} catch (ServerException e) {
				Log.d("RatingsDialog" , "Unable to add rating. Details : "+ e.toString());
				Toast.makeText(getContext(), R.string.server_does_not_answer, Toast.LENGTH_SHORT);
			}*/
			
			RatingsDialog.this.dismiss();
		}
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
