/*
 ********************* [ P O C K E T C A M P U S ] *****************
 * [    LICENCE    ]    see "licence"-file in the root directory
 * [   MAINTAINER  ]    ElodieNilane.Triponez@epfl.ch
 * [     STATUS    ]    Usable
 *
 **************************[ C O M M E N T S ]**********************
 *
 * A class to display a dialog with the individual menu information.  
 *                      
 *******************************************************************
 */
package org.pocketcampus.plugin.food;

import org.pocketcampus.R;
import org.pocketcampus.shared.plugin.food.Meal;
import org.pocketcampus.shared.plugin.food.Rating;
import org.pocketcampus.shared.plugin.food.Restaurant;
import org.pocketcampus.shared.plugin.food.StarRating;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.view.View;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;

public class MenuDialog extends Dialog {
	private final Meal meal;
	private final Activity menusActivity;
	private RatingBar rateIt;
	private TextView numbVotes;
	// private final ConnexionHandler ch;
	private ProgressDialog progressDialog_;
	private Context context;
	private boolean photoButtonsEnabled;

	public MenuDialog(final Context context, final Meal meal,
			final Activity menus, boolean photoButtonsEnabled) {
		super(context);
		this.meal = meal;
		this.context = context;
		menusActivity = menus;

		/**
		 * No title for dialog Else there is indeed space for the title.
		 **/
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		this.photoButtonsEnabled = photoButtonsEnabled;
		// ch = new ConnexionHandler(menusActivity);

		setContentView(R.layout.food_dialog_menu);

		// Make the dialog box fit the width of the phone.
		getWindow().setLayout(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT);

		// Dialog box is closed when we touch outside.
		setCanceledOnTouchOutside(true);

		setDialogContent();
	}

	private void setDialogContent() {
		progressDialog_ = ProgressDialog.show(context, "Please Wait",
				"Loading menus...", true, false);

		// Set the title, description, rating and number of votes.
		TextView title = (TextView) findViewById(R.id.food_menudialog_title);
		TextView description = (TextView) findViewById(R.id.food_menudialog_description);
//		TextView rateItYourself = (TextView) findViewById(R.id.food_menudialog_rateityourself);
		numbVotes = (TextView) findViewById(R.id.food_menudialog_nbvotes);

		// Set title of dialog box to Meal @ Restaurant
		title.setText(meal.getName_() + " @ " + meal.getRestaurant_().getName());
		description.setText(meal.getDescription_());

		// Clicking on the star rating will open the rating dialog
		/*rateItYourself.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				RatingsDialog r = new RatingsDialog(getContext(), getMeal());
				r.setOnDismissListener(new OnDismissRatingsListener());
				r.show();
			}
		});*/

		// Retrieve the meal's rating from the server to display it.
		rateIt = (RatingBar) findViewById(R.id.food_menudialog_ratingBarIndicator);
		paintRatingBar();

		// Chose a menu
		ImageButton rateIt = (ImageButton) findViewById(R.id.food_menudialog_rateIt);
		rateIt.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// give me the feault MealTagger
				// MealTagger tagger = new MealTagger();
				// Collection<Meal> oneMealCollection = new HashSet<Meal>();
				// oneMealCollection.add(meal);
				// give me all Tags for this Meal
				// Collection<MealTag> allTags = tagger
				// .extractTagsFrom(oneMealCollection);

				// update the statistics
				/*
				 * MealStatsManagement mealsManagement = MealStatsManagement
				 * .getInstance(menusActivity.getApplicationContext());
				 * 
				 * // first increment the MealTags
				 * mealsManagement.incrementMealTagCounter(allTags);
				 * 
				 * // then the number of visits in this Restaurant Restaurant
				 * resto = meal.getRestaurant();
				 * mealsManagement.incrementRestaurantCounter(resto);
				 * 
				 * callStatisticsActivity();
				 */
			}

		});

		// Route to the Restaurant
		ImageButton goThere = (ImageButton) findViewById(R.id.food_menudialog_goThere);
		goThere.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// Restaurant resto = meal.getRestaurant();
				// callNavigationActivity(resto);
				// menusActivity.finish();
			}
		});
		/**
		 * Here is the pictures option, we can take and see pictures of -the
		 * meal -the queue (for the restaurant corresponding to this meal)
		 */
		// Take a picture of the meal
		ImageButton takePic = (ImageButton) findViewById(R.id.food_menudialog_Pictures);
		takePic.setEnabled(photoButtonsEnabled);
		takePic.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				/*
				 * TakePicture cameraMachin = new TakePicture(menusActivity,
				 * meal); DailyMenus.setMealPicture(true);
				 * cameraMachin.takePicture();
				 */
			}
		});

		progressDialog_.dismiss();

	}

	private void seePicturesQueue() {
		/*
		 * Intent seePicture = new Intent(getContext(),
		 * SlidingImageActivity.class); seePicture.putExtra("meal", meal);
		 * ch.startActivity(seePicture);
		 */
	}

	private void callNavigationActivity(Restaurant selectedRestaurant) {
		/*
		 * Intent startNavActivity = new Intent(getContext(),
		 * NavigationActivity.class); startNavActivity.putExtra("Restaurant",
		 * selectedRestaurant); ch.startActivity(startNavActivity); dismiss();
		 */
	}

	private void callStatisticsActivity() {
		/*
		 * Intent startStatActivity = new Intent(getContext(),
		 * RestaurantStats.class);
		 * menusActivity.startActivity(startStatActivity);
		 */
	}

	private String getVoteString(int numbVotes) {
		Resources r = getContext().getResources();
		String votes = r.getString(R.string.food_menulist_votesPlural);
		if (numbVotes == 1)
			votes = r.getString(R.string.food_menulist_votesSingular);
		return votes;
	}

	private Meal getMeal() {
		return this.meal;
	}

	private class OnDismissRatingsListener implements
			RatingsDialog.OnDismissListener {

		@Override
		public void onDismiss(DialogInterface dialogInt) {
			paintRatingBar();
		}
	}

	private void paintRatingBar() {
		rateIt = (RatingBar) findViewById(R.id.food_menudialog_ratingBarIndicator);

//		ConnexionHandler ch = new ConnexionHandler(getContext());
//		Rating rating = ch.getRating(meal);
		
		/**
		 * Exemple rating:
		 */
		Rating rating = new Rating(StarRating.STAR_4_0, 12);
		rateIt.setRating((float) Restaurant.starRatingToDouble(rating
				.getValue()));
		
		// Retrieve the number of votes from the server.
		int numbVote = rating.getNumberOfVotes();
		String votes = getVoteString(numbVote);
		numbVotes.setText("("+ numbVote + " " + votes + ")");
	}
}
