package org.pocketcampus.plugin.food.android;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;

import org.pocketcampus.R;
import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginView;
import org.pocketcampus.android.platform.sdk.ui.layout.StandardLayout;
import org.pocketcampus.android.platform.sdk.ui.list.CheckBoxesListViewElement;
import org.pocketcampus.plugin.food.android.utils.MealTag;
import org.pocketcampus.plugin.food.android.utils.MealTagger;
import org.pocketcampus.plugin.food.shared.Meal;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

public class FoodSuggestionsView extends PluginView {
	private FoodController mController;

	/* GUI */
	private StandardLayout mLayout;
	private CheckBoxesListViewElement mListView;
	private Button computeButton;

	/*
	 * The Meals sent by the MainView, modififed here and sent back filtered
	 * with the tags
	 */
	private Collection<Meal> mMeals;

	/* To compute Suggestions based on tags */
	private MealTagger mTagger;

	private List<MealTag> mTagsList;
	private List<String> mTagsToDisplay;

	private List<MealTag> mLikes;
	private List<MealTag> mDislikes;

	@Override
	protected Class<? extends Service> getMainControllerClass() {
		return FoodController.class;
	}

	@Override
	protected void onDisplay(Bundle savedInstanceState,
			PluginController controller) {
		mController = (FoodController) controller;

		/* ===== LAYOUT, VIEWS & DATA ===== */

		// The StandardLayout is a RelativeLayout with a TextView in its center.
		mLayout = new StandardLayout(this);

		// Compute Suggestions Button
		computeButton = new Button(this);
		computeButton.setId(1);

		// Like Button
		ImageButton likeButton = new ImageButton(this);
		likeButton.setId(2);

		// Dislike Button
		ImageButton dislikeButton = new ImageButton(this);
		dislikeButton.setId(3);

		// Instantiate Objects
		mLikes = new ArrayList<MealTag>();
		mDislikes = new ArrayList<MealTag>();

		// Handle extras from MainView
		handleExtras();

		// Get the tags from the controller
		mTagsList = mController.getMealTags();
		mTagsToDisplay = languageCompatible();

		/* ===== PARAMETERS ===== */

		// Layout
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		mLayout.setLayoutParams(layoutParams);

		// Compute Button
		computeButton.setText(R.string.food_suggestions_ok);
		RelativeLayout.LayoutParams buttonParams = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		// buttonParams.addRule(RelativeLayout.ABOVE, mListView.getId());
		buttonParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		buttonParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
		computeButton.setLayoutParams(buttonParams);

		// Set onClickListener
		setOnComputeButtonClickListener();

		// Like Button
		likeButton.setBackgroundResource(R.drawable.food_suggestions_like);
		likeButton.setClickable(false);
		likeButton.setMinimumHeight(computeButton.getHeight());
		likeButton.setMinimumWidth(computeButton.getHeight());
		RelativeLayout.LayoutParams likeParams = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		likeParams.addRule(RelativeLayout.LEFT_OF, dislikeButton.getId());
		// likeParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		likeButton.setLayoutParams(likeParams);

		// Dislike Button
		dislikeButton
				.setBackgroundResource(R.drawable.food_suggestions_dislike);
		dislikeButton.setClickable(false);
		RelativeLayout.LayoutParams dislikeParams = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT,
				android.widget.RelativeLayout.LayoutParams.WRAP_CONTENT);
		// dislikeParams.addRule(RelativeLayout.RIGHT_OF, likeButton.getId());
		// dislikeParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		dislikeParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		dislikeButton.setLayoutParams(dislikeParams);

		// List View
		// Filling the ListView
		mListView = new CheckBoxesListViewElement(this, mTagsToDisplay);

		// List w.r.t. the Compute Button
		RelativeLayout.LayoutParams listParams = new RelativeLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		listParams.addRule(RelativeLayout.BELOW, computeButton.getId());
		mListView.setLayoutParams(listParams);

		// Set onClickListener
		setOnListViewClickListener();

		// Set the layout
		mLayout.addView(computeButton);
		mLayout.addView(likeButton);
		mLayout.addView(dislikeButton);
		mLayout.addView(mListView);

		// The ActionBar is added automatically when you call setContentView
		setContentView(mLayout);
	}

	/* Sets the clickLIstener of the listView */
	private void setOnListViewClickListener() {

		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long boxId) {
				// boxId = 1 : positiveBox
				// boxId = 0 : negativeBox
				MealTag tag = mTagsList.get(position);

				if (boxId == 1) {
					Log.d("SUGGESTIONS", "Added " + tag);
					if (!mLikes.contains(tag)) {
						mLikes.add(tag);
					}
					mDislikes.remove(tag);
				} else if (boxId == 0) {
					Log.d("SUGGESTIONS", "Removed " + tag);
					if (!mDislikes.contains(tag)) {
						mDislikes.add(tag);
					}
					mLikes.remove(tag);
				}
			}

		});

	}

	/* Sets the clickLsitener on the Compute Button */
	private void setOnComputeButtonClickListener() {

		computeButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finalizeSuggestions();
			}
		});

	}

	/* Only keep Meals satisfying what the user checked */
	private Collection<Meal> computeSuggestions(Collection<Meal> meals,
			MealTag tag, MealTagger tagger) {

		Collection<Meal> returnedMeals = null;
		returnedMeals = tagger.parseMealsFor(tag, meals);

		if (returnedMeals == null) {
			return new HashSet<Meal>();
		}
		return returnedMeals;
	}

	/* Finalize and send back the Meals to the MainView */
	private void finalizeSuggestions() {

		mTagger = new MealTagger();
		Collection<Meal> computeDislikeMeals = new HashSet<Meal>();
		Collection<Meal> computeLikeMeals = new HashSet<Meal>();

		if (!mLikes.isEmpty() && mMeals != null) {
			for (MealTag tag : mLikes) {
				computeLikeMeals
						.addAll(computeSuggestions(mMeals, tag, mTagger));
			}
		} else if ((mLikes.isEmpty()) && (mMeals != null)) {
			computeLikeMeals.addAll(mMeals);
		}

		if (!mDislikes.isEmpty() && mMeals != null) {
			for (MealTag tag : mDislikes) {
				computeDislikeMeals.addAll(computeSuggestions(mMeals, tag,
						mTagger));
			}
		}

		Collection<Meal> computeMeals = new HashSet<Meal>(computeLikeMeals);
		computeMeals.removeAll(computeDislikeMeals);

		ArrayList<Meal> list = new ArrayList<Meal>();
		for (Meal meal : computeMeals) {
			list.add(meal);
		}

		Intent menus = new Intent(getApplicationContext(), FoodMainView.class);
		menus.putExtra("org.pocketcampus.suggestions.meals", list);
		setResult(Activity.RESULT_OK, menus);
		finish();
	}

	/* Handle extras from the MainView */
	private void handleExtras() {
		mMeals = new Vector<Meal>();

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			@SuppressWarnings("unchecked")
			ArrayList<Meal> m = (ArrayList<Meal>) extras
					.getSerializable("org.pocketcampus.suggestions.meals");
			if (m != null && !m.isEmpty()) {
				for (Meal meal : m) {
					mMeals.add(meal);
				}
			}
		} else {
			Log.d("SUGGESTIONS", "No extras rceived!");
		}
	}

	/* To display the right language and not english (as it is in the enum) */
	private ArrayList<String> languageCompatible() {
		ArrayList<String> list = new ArrayList<String>();

		for (MealTag m : mTagsList) {
			list.add(write(m));
		}

		return list;
	}

	/* From MealTag to String */
	private String write(MealTag tag) {
		Resources r = getApplicationContext().getResources();

		String string = "";

		switch (tag) {
		case MEAT:
			string = r.getString(R.string.food_suggestions_meat);
			break;
		case FISH:
			string = r.getString(R.string.food_suggestions_fish);
			break;
		case VEGETARIAN:
			string = r.getString(R.string.food_suggestions_vege);
			break;
		case PASTA:
			string = r.getString(R.string.food_suggestions_pasta);
			break;
		case RICE:
			string = r.getString(R.string.food_suggestions_rice);
			break;
		case PORC:
			string = r.getString(R.string.food_suggestions_porc);
			break;
		case CHICKEN:
			string = r.getString(R.string.food_suggestions_chicken);
			break;
		case BEEF:
			string = r.getString(R.string.food_suggestions_beef);
			break;
		case HORSE:
			string = r.getString(R.string.food_suggestions_horse);
			break;
		case PIZZA:
			string = r.getString(R.string.food_suggestions_pizza);
			break;
		default:
			string = r.getString(R.string.food_suggestions_notag);
		}

		return string;
	}

}
