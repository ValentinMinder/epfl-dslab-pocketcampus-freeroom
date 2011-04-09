package org.pocketcampus.plugin.food;

import org.pocketcampus.R;
import org.pocketcampus.plugin.food.FoodListSection.ViewHolder;
import org.pocketcampus.shared.plugin.food.Meal;
import org.pocketcampus.shared.plugin.food.Rating;
import org.pocketcampus.shared.plugin.food.Restaurant;
import org.pocketcampus.shared.plugin.food.StarRating;

import android.app.Activity;
import android.os.AsyncTask;

/**
 * Class used to fetch the meal ratings from the server in background so
 * that the list view lags less. Adds the rating and the number of votes to
 * the holder, where appropriate.
 * 
 * @author Elodie
 * 
 */
public class RatingsDownloader extends AsyncTask<Meal, Integer, Rating> {
	private Rating mealRating_;
	private ViewHolder holder_;
	private Activity menusActivity_;

	/**
	 * 
	 * @param m
	 *            current meal to be displayed on the list view.
	 * @param holder
	 *            view holder for that list element.
	 */
	public RatingsDownloader(Meal m, Activity menusActivity, ViewHolder holder) {
		this.holder_ = holder;
		this.menusActivity_ = menusActivity;
		this.execute(m);
	}

	protected Rating doInBackground(Meal... meal) {
		// Get the rating from the server to display next to the menu in the
		// list.
		// progressDialog_ = ProgressDialog.show(context, "Please wait",
		// "Charging ratings...", true, false);
		mealRating_ = null;
		Meal currentMeal = meal[0];
		/*ConnexionHandler connHandler = new ConnexionHandler(context_);
		mealRating = connHandler.getRating(currentMeal);
		connHandler.close();*/
		mealRating_ = new Rating(StarRating.STAR_3_0, 12);

		return mealRating_;
	}

	protected void onPostExecute(Rating mealRating) {
		if (mealRating != null) {
			holder_.ratingLine.setRating((float) Restaurant
					.starRatingToDouble(mealRating.getValue()));

			// Get the number of votes from the server to display next to
			// the menu in the list.
			int numbVotes = mealRating.getNumberOfVotes();
			if (numbVotes != 1 && numbVotes != 0) {
				holder_.votesLine.setText(numbVotes
						+ " "
						+ menusActivity_.getResources().getString(
								R.string.food_menulist_votesPlural));
			} else {
				holder_.votesLine.setText(numbVotes
						+ " "
						+ menusActivity_.getResources().getString(
								R.string.food_menulist_votesSingular));
			}

		}
	}
}