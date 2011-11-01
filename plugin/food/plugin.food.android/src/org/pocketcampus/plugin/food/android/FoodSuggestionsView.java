package org.pocketcampus.plugin.food.android;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;

import org.pocketcampus.R;
import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginView;
import org.pocketcampus.android.platform.sdk.ui.element.CheckBoxesListViewElement;
import org.pocketcampus.android.platform.sdk.ui.layout.StandardLayout;
import org.pocketcampus.plugin.food.android.iface.IFoodModel;
import org.pocketcampus.plugin.food.android.iface.IFoodView;
import org.pocketcampus.plugin.food.android.utils.MealTag;
import org.pocketcampus.plugin.food.android.utils.MealTagger;
import org.pocketcampus.plugin.food.shared.Meal;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.Toast;

public class FoodSuggestionsView extends PluginView implements IFoodView {
	private FoodController mController;
	private IFoodModel mModel;

	/*GUI*/
	private StandardLayout mLayout;
	private CheckBoxesListViewElement mListView;
	private Button mButton;
	
	/*The Meals sent by the MainView, modififed here and sent back filtered with the tags*/
	private Collection<Meal> mMeals;
	
	/*To compute Suggestions based on tags*/
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
	protected void onDisplay(Bundle savedInstanceState,	PluginController controller){
		mController = (FoodController)controller;
		mModel = (FoodModel)controller.getModel();

		// The StandardLayout is a RelativeLayout with a TextView in its center.
		mLayout = new StandardLayout(this);
		LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		mLayout.setLayoutParams(layoutParams);
		mLayout.setGravity(Gravity.CENTER_VERTICAL);
		
		//Instantiate Objects
		mLikes = new ArrayList<MealTag>();
		mDislikes = new ArrayList<MealTag>();
		
		//Handle extras from MainView
		handleExtras();
		
		//Get the tags from the controller
		mTagsList = mController.getMealTags();
		mTagsToDisplay = languageCompatible();

		//Add them to the listView
		mListView = new CheckBoxesListViewElement(this, mTagsToDisplay);

		//Set onClickListener
		setOnListViewClickListener();
		
		//Create a button
		mButton = new Button(this);
		mButton.setText(R.string.food_suggestions_ok);
		mButton.setClickable(true);
		
		//Set the params
		LayoutParams buttonParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		mButton.setLayoutParams(buttonParams);
		
		//Set onClickListener
		setOnComputeButtonClickListener();
		
		//Set the layout
		mLayout.addView(mListView, 0);
		mLayout.addView(mButton, 1);

		// The ActionBar is added automatically when you call setContentView
		setContentView(mLayout);
	}

	@Override
	public void networkErrorHappened() {
		Toast.makeText(getApplicationContext(), "Network error!", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void restaurantsUpdated() {
		// not used
	}

	@Override
	public void menusUpdated() {
		// not used
	}

	/*Sets the clickLIstener of the listView*/
	private void setOnListViewClickListener() {

		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				//arg2 : position;
				//arg3 = 1 : positiveBox
				//arg3 = 0 : negativeBox
				MealTag tag = mTagsList.get(arg2);
				
				if(arg3 == 1) {
					Log.d("SUGGESTIONS", "Added " + tag);
					if(! mLikes.contains(tag)) {						
						mLikes.add(tag);
					}
					mDislikes.remove(tag);
				} else if(arg3 == 0) {
					Log.d("SUGGESTIONS", "Removed " + tag);
					if(! mDislikes.contains(tag)) {						
						mDislikes.add(tag);
					}
					mLikes.remove(tag);
				}
			}
			
		});

	}
	
	/*Sets the clickLsitener on the Compute Button*/
	private void setOnComputeButtonClickListener() {
		
		mButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finalizeSuggestions();
			}
		});
		
	}

	/*Only keep Meals satisfying what the user checked*/
	private Collection<Meal> computeSuggestions(Collection<Meal> meals, MealTag tag, MealTagger tagger){

		Collection<Meal> returnedMeals = null;
		returnedMeals = tagger.parseMealsFor(tag, meals);

		if(returnedMeals==null){
			return new HashSet<Meal>();
		}
		return returnedMeals;
	}

	/*Finalize and send back the Meals to the MainView*/
	private void finalizeSuggestions(){
		
		mTagger = new MealTagger();
		Collection<Meal> computeDislikeMeals = new HashSet<Meal>();
		Collection<Meal> computeLikeMeals = new HashSet<Meal>();

		if(! mLikes.isEmpty() && mMeals != null){
			for(MealTag tag : mLikes){
				computeLikeMeals.addAll(computeSuggestions(mMeals, tag, mTagger));
			}
		} else if ((mLikes.isEmpty()) && (mMeals != null)){
			computeLikeMeals.addAll(mMeals);
		}

		if(! mDislikes.isEmpty() && mMeals != null){				
			for(MealTag tag : mDislikes){
				computeDislikeMeals.addAll(computeSuggestions(mMeals, tag, mTagger));
			}
		}

		Collection<Meal> computeMeals = new HashSet<Meal>(computeLikeMeals);
		computeMeals.removeAll(computeDislikeMeals);

		ArrayList<Meal> list = new ArrayList<Meal>();
		for(Meal meal : computeMeals){
			list.add(meal);
		}
		
		Intent menus = new Intent(getApplicationContext(), FoodMainView.class);
		menus.putExtra("org.pocketcampus.suggestions.meals", list);
		setResult(Activity.RESULT_OK, menus);
		finish();
	}
	
	/*Handle extras from the MainView*/
	private void handleExtras(){
		mMeals = new Vector<Meal>();
		
		Bundle extras = getIntent().getExtras();
		if(extras != null){
			@SuppressWarnings("unchecked")
			ArrayList<Meal> m = (ArrayList<Meal>)extras.getSerializable("org.pocketcampus.suggestions.meals");
			if(m != null && !m.isEmpty()){
				for(Meal meal : m){
					mMeals.add(meal);
				}
			}
		}else{
			Log.d("SUGGESTIONS", "No extras rceived!");
		}
	}
	
	/*To display the right language and not english (as it is in the enum)*/
	private ArrayList<String> languageCompatible() {
		ArrayList<String> list = new ArrayList<String>();
		
		for(MealTag m : mTagsList) {
			list.add(write(m));
		}
		
		return list;
	}
	
	/*From MealTag to String*/
	private String write(MealTag tag){
		Resources r = getApplicationContext().getResources();

		String string = "";

		switch (tag){
		case MEAT :
			string = r.getString(R.string.food_suggestions_meat);
			break;
		case FISH :
			string = r.getString(R.string.food_suggestions_fish);
			break;
		case VEGETARIAN :
			string = r.getString(R.string.food_suggestions_vege);
			break;
		case PASTA :
			string = r.getString(R.string.food_suggestions_pasta);
			break;
		case RICE :
			string = r.getString(R.string.food_suggestions_rice);
			break;
		case PORC :
			string = r.getString(R.string.food_suggestions_porc);
			break;
		case CHICKEN :
			string = r.getString(R.string.food_suggestions_chicken);
			break;
		case BEEF :
			string = r.getString(R.string.food_suggestions_beef);
			break;
		case HORSE :
			string = r.getString(R.string.food_suggestions_horse);
			break;
		default :
			string = r.getString(R.string.food_suggestions_notag);
		}

		return string;
	}
	
}
