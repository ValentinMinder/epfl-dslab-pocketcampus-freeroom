package org.pocketcampus.plugin.food.android;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;

import org.pocketcampus.R;
import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginView;
import org.pocketcampus.android.platform.sdk.ui.labeler.ILabeler;
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
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

/**
 * The Suggestions View of the Food plugin
 * 
 * Displayed a list of choices the user can say he likes or dislikes, for the
 * meals to be filtered
 * 
 * @author Elodie (elodienilane.triponez@epfl.ch)
 * @author Oriane (oriane.rodriguez@epfl.ch)
 * 
 */
public class FoodSuggestionsView extends PluginView {
	/** The controller that does the interface between model and view */
	private FoodController mController;

	/* GUI */
	/** A simple full screen layout */
	private StandardLayout mLayout;

	/** The list to be displayed in the layout */
	private CheckBoxesListViewElement mListView;

	/** The button to validate the choices */
	private Button computeButton;

	/** The ImageButton to represent "I like" */
	private ImageButton mLikeButton;

	/** The ImageButton to represent "I like" */
	private ImageButton mDislikeButton;

	/**
	 * The Meals sent by the MainView, modified here and sent back filtered with
	 * the tags
	 */
	private Collection<Meal> mMeals;

	/** To compute Suggestions based on tags */
	private MealTagger mTagger;

	/** The list of available tags */
	private List<MealTag> mTagsList;

	/** The list of string tags to display in the list */
	private List<String> mTagsToDisplay;

	/** The list of things the user says he likes */
	private List<MealTag> mLikes;

	/** The list of things the user says he doesn't like */
	private List<MealTag> mDislikes;

	@Override
	protected Class<? extends Service> getMainControllerClass() {
		return FoodController.class;
	}

	/**
	 * Initializes the view for the suggestions
	 */
	@Override
	protected void onDisplay(Bundle savedInstanceState,
			PluginController controller) {
		mController = (FoodController) controller;

		/* ===== LAYOUT, VIEWS & DATA ===== */

		// Handle extras from MainView
		handleExtras();

		// Get the tags from the controller
		mTagsList = mController.getMealTags();
		mTagsToDisplay = languageCompatible();

		// The StandardLayout is a RelativeLayout with a TextView in its center.
		mLayout = new StandardLayout(this);

		// List View
		// Filling the ListView
		mListView = new CheckBoxesListViewElement(this, mTagsToDisplay);

		// Compute Suggestions Button
		computeButton = new Button(this);
		computeButton.setId(1);

		// Like Button
		mLikeButton = new ImageButton(this);
		mLikeButton.setId(2);

		// Dislike Button
		mDislikeButton = new ImageButton(this);
		mDislikeButton.setId(3);

		// Instantiate Objects
		mLikes = new ArrayList<MealTag>();
		mDislikes = new ArrayList<MealTag>();

		/* ===== PARAMETERS ===== */
		setParameters();

		// Set onClickListener
		setOnListViewClickListener();

		// Set the layout
		mLayout.addView(computeButton);
		mLayout.addView(mLikeButton);
		mLayout.addView(mDislikeButton);
		mLayout.addView(mListView);

		// The ActionBar is added automatically when you call setContentView
		setContentView(mLayout);
	}

	/**
	 * Sets the parameters for the ListView and the Buttons
	 */
	private void setParameters() {
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
		mLikeButton.setBackgroundResource(R.drawable.food_suggestions_like);
		mLikeButton.setClickable(false);
		mLikeButton.setMinimumHeight(computeButton.getHeight());
		mLikeButton.setMinimumWidth(computeButton.getHeight());
		RelativeLayout.LayoutParams likeParams = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		likeParams.addRule(RelativeLayout.LEFT_OF, mDislikeButton.getId());
		// likeParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		mLikeButton.setLayoutParams(likeParams);

		// Dislike Button
		mDislikeButton
		.setBackgroundResource(R.drawable.food_suggestions_dislike);
		mDislikeButton.setClickable(false);
		RelativeLayout.LayoutParams dislikeParams = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT,
				android.widget.RelativeLayout.LayoutParams.WRAP_CONTENT);
		// dislikeParams.addRule(RelativeLayout.RIGHT_OF, likeButton.getId());
		// dislikeParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		dislikeParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		mDislikeButton.setLayoutParams(dislikeParams);

		// List w.r.t. the Compute Button
		RelativeLayout.LayoutParams listParams = new RelativeLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		listParams.addRule(RelativeLayout.BELOW, computeButton.getId());
		mListView.setLayoutParams(listParams);
	}

	/**
	 * Sets the clickListener of the ListView
	 */
	private void setOnListViewClickListener() {

		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View box,
					int position, long boxId) {
				// boxId = 1 : positiveBox
				// boxId = 0 : negativeBox
				MealTag tag = mTagsList.get(position);
				CheckBox b = (CheckBox) box;

				if(b.isChecked()) {
					if(boxId == 1) { // Added a likeTag

						Log.d("SUGGESTIONS", "Added " + tag + " to likes");
						if (!mLikes.contains(tag)) {
							mLikes.add(tag);
						} 
						mDislikes.remove(tag);
						
					} else if (boxId == 0) { // Added a dislikeTag

						Log.d("SUGGESTIONS", "Added " + tag + " to dislikes");
						if (!mDislikes.contains(tag)) {
							mDislikes.add(tag);
						}
						mLikes.remove(tag);
					}
				} else {
					if(boxId == 1) { // Removed a likeTag
						
						Log.d("SUGGESTIONS", "Removed " + tag + " from likes");
						mLikes.remove(tag);
					} else if (boxId == 0) { // Removed a dislikeTag
						
						Log.d("SUGGESTIONS", "Removed " + tag + " from dislikes");
						mDislikes.remove(tag);
					}
				}

			}

		});

	}

	/**
	 * Sets the clickListener on the Compute Button
	 */
	private void setOnComputeButtonClickListener() {

		computeButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finalizeSuggestions();
			}
		});

	}

	/**
	 * Compute the meals satisfying what the user checked
	 * 
	 * @param meals
	 *            the list of all meals
	 * @param tag
	 *            one tag
	 * @param tagger
	 *            the parser for the meals according to one tag
	 * @return
	 */
	private Collection<Meal> computeSuggestions(Collection<Meal> meals,
			MealTag tag, MealTagger tagger) {

		Collection<Meal> returnedMeals = null;
		returnedMeals = tagger.parseMealsFor(tag, meals);

		if (returnedMeals == null) {
			return new HashSet<Meal>();
		}
		return returnedMeals;
	}

	/**
	 * Finalize and send back the Meals to the MainView
	 */
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

	/**
	 * Handle extras from the MainView
	 */
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

	/**
	 * To display the right language and not english (as it is in the enum)
	 * 
	 * @return the list of tags depending on the language the phone is setup in
	 */
	private ArrayList<String> languageCompatible() {
		ArrayList<String> list = new ArrayList<String>();

		for (MealTag m : mTagsList) {
			list.add(write(m));
		}

		return list;
	}

	/**
	 * Convert from MealTag to String
	 * 
	 * @param tag
	 *            the tag to convert
	 * @return the corresponding string
	 */
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
		case PIZZA:
			string = r.getString(R.string.food_suggestions_pizza);
			break;
		default:
			string = r.getString(R.string.food_suggestions_notag);
		}

		return string;
	}

	ILabeler<MealTag> mTagLabeler = new ILabeler<MealTag>() {

		@Override
		public String getLabel(MealTag obj) {
			return write(obj);
		}

	};

}
