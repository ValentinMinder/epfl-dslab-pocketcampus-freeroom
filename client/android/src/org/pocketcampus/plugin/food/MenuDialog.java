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
import org.pocketcampus.plugin.logging.Tracker;
import org.pocketcampus.shared.plugin.food.Meal;
import org.pocketcampus.shared.plugin.food.Rating;
import org.pocketcampus.shared.plugin.food.Restaurant;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.view.View;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;

public class MenuDialog extends Dialog {
	private final Meal displayedMeal_;
	private RatingBar ratingBar_;
	private TextView numbVotes_;
	private ProgressDialog progressDialog_;
	private FoodPlugin context_;

	public MenuDialog(final Meal meal, final FoodPlugin menus) {
		super(menus);
		this.displayedMeal_ = meal;
		this.context_ = menus;
		/**
		 * No title for dialog Else there is indeed space for the title.
		 **/
		requestWindowFeature(Window.FEATURE_NO_TITLE);


		setContentView(R.layout.food_dialog_menu);

		// Make the dialog box fit the width of the phone.
		getWindow().setLayout(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT);

		// Dialog box is closed when we touch outside.
		setCanceledOnTouchOutside(true);

		Tracker.getInstance().trackPageView("food/menuDialog");
		
		setDialogContent();
	}

	private void setDialogContent() {
		progressDialog_ = ProgressDialog.show(context_, "Please Wait",
				"Loading menus...", true, false);

		// Set the title, description, rating and number of votes.
		TextView title = (TextView) findViewById(R.id.food_menudialog_title);
		TextView description = (TextView) findViewById(R.id.food_menudialog_description);

		numbVotes_ = (TextView) findViewById(R.id.food_menudialog_nbvotes);

		// Set title of dialog box to Meal @ Restaurant
		title.setText(displayedMeal_.getName_() + " @ "
				+ displayedMeal_.getRestaurant_().getName());
		description.setText(displayedMeal_.getDescription_());

		// Retrieve the meal's rating from the server to display it.
		ratingBar_ = (RatingBar) findViewById(R.id.food_menudialog_ratingBarIndicator);
		paintRatingBar();

		// Chose a menu
		ImageButton rateIt = (ImageButton) findViewById(R.id.food_menudialog_rateIt);
		rateIt.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				RatingsDialog r = new RatingsDialog(displayedMeal_, context_);
				r.setOnDismissListener(new OnDismissRatingsListener());
				r.show();
			}

		});

		// Route to the Restaurant
//		ImageButton goThere = (ImageButton) findViewById(R.id.food_menudialog_goThere);
//		goThere.setOnClickListener(new View.OnClickListener() {
//
//			public void onClick(View v) {
//				// Restaurant resto = meal.getRestaurant();
//				// callNavigationActivity(resto);
//				// menusActivity.finish();
//			}
//		});
		/**
		 * Here is the pictures option, we can take and see pictures of -the
		 * meal -the queue (for the restaurant corresponding to this meal)
		 */
		// Take a picture of the meal
//		ImageButton takePic = (ImageButton) findViewById(R.id.food_menudialog_Pictures);
//		takePic.setEnabled(photoButtonsEnabled_);
//		takePic.setOnClickListener(new View.OnClickListener() {
//
//			public void onClick(View v) {
//				PictureTaker pictureTaker = new PictureTaker(context_,
//						displayedMeal_, PictureType.Meal);
//
//				pictureTaker.takePicture();
////				Intent seePicture = new Intent(getContext(),
////						FoodPictureActivity.class);
////				context_.startActivity(seePicture);
////				PictureFetcher fetcher = new PictureFetcher(PictureType.Meal, displayedMeal_);
////				Drawable[] pictures = fetcher.getPictures();
//			}
//		});

		progressDialog_.dismiss();

	}

	private String getVoteString(int numbVotes) {
		Resources r = getContext().getResources();
		String votes = r.getString(R.string.food_menulist_votesPlural);
		if (numbVotes == 1)
			votes = r.getString(R.string.food_menulist_votesSingular);
		return votes;
	}

	// private Meal getMeal() {
	// return this.displayedMeal_;
	// }

	private class OnDismissRatingsListener implements
			RatingsDialog.OnDismissListener {

		@Override
		public void onDismiss(DialogInterface dialogInt) {
			paintRatingBar();
		}
	}

	private void paintRatingBar() {
		ratingBar_ = (RatingBar) findViewById(R.id.food_menudialog_ratingBarIndicator);

		Rating rating = displayedMeal_.getRating();
		ratingBar_.setRating((float) Restaurant.starRatingToDouble(rating
				.getValue()));

		// Retrieve the number of votes from the server.
		int numbVote = rating.getNumberOfVotes();
		String votes = getVoteString(numbVote);
		numbVotes_.setText("(" + numbVote + " " + votes + ")");
	}
}
