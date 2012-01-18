package org.pocketcampus.plugin.food.android;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;

import org.pocketcampus.R;
import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginView;
import org.pocketcampus.android.platform.sdk.tracker.Tracker;
import org.pocketcampus.android.platform.sdk.ui.element.ButtonElement;
import org.pocketcampus.android.platform.sdk.ui.labeler.ILabeler;
import org.pocketcampus.android.platform.sdk.ui.layout.StandardTitledDoubleLayout;
import org.pocketcampus.android.platform.sdk.ui.list.CheckBoxListViewElement;
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
import android.widget.CheckBox;
import android.widget.RelativeLayout;

/**
 * The Suggestions View of the Food plugin.
 * 
 * Displays a list of choices the user can say he likes or dislikes, for the
 * meals to be filtered.
 * 
 * @author Elodie <elodienilane.triponez@epfl.ch>
 * @author Oriane <oriane.rodriguez@epfl.ch>
 * 
 */
public class FoodSuggestionsView extends PluginView {
	/** The controller that does the interface between model and view. */
	private FoodController mController;

	/* GUI */
	/** A double full screen layout. */
	private StandardTitledDoubleLayout mLayout;
	/** The list to be displayed in the layout. */
	private CheckBoxListViewElement mListView;
	/** The button to validate the choices. */
	private ButtonElement mComputeButton;

	/**
	 * The Meals sent by the MainView, modified here and sent back filtered with
	 * the tags.
	 */
	private Collection<Meal> mMeals;
	/** Object used to compute Suggestions based on tags. */
	private MealTagger mTagger;
	/** The list of available tags. */
	private List<MealTag> mTagsList;
	/** The list of things the user says he likes. */
	private List<MealTag> mLikes;

	/**
	 * Defines what the main controller is for this view.
	 */
	@Override
	protected Class<? extends Service> getMainControllerClass() {
		return FoodController.class;
	}

	/**
	 * Initializes the view for the suggestions.
	 */
	@Override
	protected void onDisplay(Bundle savedInstanceState,
			PluginController controller) {
		// Tracker
		Tracker.getInstance().trackPageView("food/suggestions");

		mController = (FoodController) controller;

		/* ===== LAYOUT, VIEWS & DATA ===== */

		// Handle extras from MainView
		handleExtras();

		// Get the tags from the controller
		mTagsList = mController.getMealTags();

		// The StandardLayout is a RelativeLayout with a TextView in its center
		// and/or two RelativeLayout one above the other
		mLayout = new StandardTitledDoubleLayout(this);
		mLayout.setTitle(getResources().getString(R.string.food_by_suggestions));

		// ListView
		mListView = new CheckBoxListViewElement(this, mTagsList, mTagLabeler);

		// Compute Suggestions Button
		mComputeButton = new ButtonElement(this, getResources().getString(
				R.string.food_suggestions_ok));

		// Instantiate Objects
		mLikes = new ArrayList<MealTag>();

		/* ===== PARAMETERS ===== */
		setParameters();

		// Set onClickListener
		setOnListViewClickListener();

		// Set the layout
		mLayout.addFirstLayoutFillerView(mListView);
		mLayout.addSecondLayoutFillerView(mComputeButton);

		setContentView(mLayout);
	}

	/**
	 * Sets the parameters for the ListView and the Buttons.
	 */
	private void setParameters() {
		// List
		RelativeLayout.LayoutParams listParams = new RelativeLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		mListView.setLayoutParams(listParams);

		// Compute Button
		RelativeLayout.LayoutParams buttonParams = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		buttonParams.addRule(RelativeLayout.CENTER_IN_PARENT);
		mComputeButton.setLayoutParams(buttonParams);
		mComputeButton.setEnabled(false);

		// Set onClickListener
		mComputeButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// Tracker
				Tracker.getInstance().trackPageView("food/suggestions/confirm");
				finalizeSuggestions();
			}
		});
	}

	/**
	 * Sets the clickListener of the ListView.
	 */
	private void setOnListViewClickListener() {

		mListView.setOnCheckBoxClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View box,
					int position, long arg3) {
				MealTag tag = mTagsList.get(position);
				CheckBox b = (CheckBox) box;

				if (b.isChecked()) {
					// Tracker
					Tracker.getInstance().trackPageView(
							"food/suggestions/add/" + tag);
					if (!mComputeButton.isEnabled()) {
						mComputeButton.setEnabled(true);
					}
					if (!mLikes.contains(tag)) {
						mLikes.add(tag);
					}
				} else {
					// Tracker
					Tracker.getInstance().trackPageView(
							"food/suggestions/remove/" + tag);
					mLikes.remove(tag);
					if (mLikes.isEmpty()) {
						mComputeButton.setEnabled(false);
					}
				}
			}

		});
	}

	/**
	 * Compute the meals satisfying what the user checked.
	 * 
	 * @param meals
	 *            the list of all meals.
	 * @param tag
	 *            one tag.
	 * @param tagger
	 *            the parser for the meals according to one tag.
	 * @return the resulting suggestions.
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
	 * Finalize and send back the Meals to the MainView.
	 */
	private void finalizeSuggestions() {

		mTagger = new MealTagger();
		Collection<Meal> computeLikeMeals = new HashSet<Meal>();

		if (!mLikes.isEmpty() && mMeals != null) {
			for (MealTag tag : mLikes) {
				computeLikeMeals
						.addAll(computeSuggestions(mMeals, tag, mTagger));
			}
		} else if ((mLikes.isEmpty()) && (mMeals != null)) {
			computeLikeMeals.addAll(mMeals);
		}

		Collection<Meal> computeMeals = new HashSet<Meal>(computeLikeMeals);

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
	 * Handle extras from the MainView.
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
	 * Convert from MealTag to String.
	 * 
	 * @param tag
	 *            the tag to convert.
	 * @return the corresponding string.
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

	/**
	 * A ILabeler to tell the View what to display for the MealTag.
	 */
	ILabeler<MealTag> mTagLabeler = new ILabeler<MealTag>() {

		/**
		 * Returns the name associated with the MealTag to be displayed.
		 * 
		 * @param mealTag
		 *            . The MealTag to be displayed.
		 * @return The name of the MealTag
		 */
		@Override
		public String getLabel(MealTag mealTag) {
			return write(mealTag);
		}

	};

}
