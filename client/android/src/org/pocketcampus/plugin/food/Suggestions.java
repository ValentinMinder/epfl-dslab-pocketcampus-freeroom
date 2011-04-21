/**
 * Handling and computing suggestions for meals
 * 
 * @author oriane
 * 
 */
package org.pocketcampus.plugin.food;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Vector;

import org.pocketcampus.R;
import org.pocketcampus.core.ui.ActionBar;
import org.pocketcampus.plugin.food.menu.MealTag;
import org.pocketcampus.plugin.food.menu.MealTagger;
import org.pocketcampus.plugin.mainscreen.MainscreenPlugin;
import org.pocketcampus.shared.plugin.food.Meal;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;

public class Suggestions extends Activity{
	private Context context_;
	private ListView l_;
	private SuggestionsListSection sls_;
	private FoodListAdapter fla_;
	
	private Collection<Meal> meals_;
	private Vector<MealTag> tags_;
	private Vector<MealTag> likes_ = new Vector<MealTag>();
	private Vector<MealTag> dislikes_ = new Vector<MealTag>();

	private MealTag likeTag_;
	private MealTag dislikeTag_;

	private MealTagger tagger_;

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);

		setContentView(R.layout.food_suggestions);
		
		// Header
		ActionBar actionBar = (ActionBar) findViewById(R.id.actionbar);
		actionBar.setTitle("PocketCampus EPFL");
		actionBar.addAction(new ActionBar.IntentAction(this, MainscreenPlugin
				.createIntent(this), R.drawable.mini_home));

		context_ = this.getApplicationContext();
		l_ = (ListView) findViewById(R.id.food_suggestions_list);
		handleExtras();
		
		MealTag[] mTags = MealTag.values();
		tags_ = new Vector<MealTag>();
		for(int i = 0; i<mTags.length; i++){
			tags_.add(mTags[i]);
		}
		
		fla_ = new FoodListAdapter(context_);
		sls_ = new SuggestionsListSection(tags_,this,context_);
		fla_.addSection("Suggestions", sls_);
		l_.setAdapter(fla_);
		
		Button b = (Button)findViewById(R.id.food_suggestions_button);
		b.setText(R.string.food_suggestions_ok);
		
		b.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				likes_ = sls_.getLikeTags();
				dislikes_ = sls_.getDislikeTags();
				
				finalizeSuggestions();				
			}
		});
		

	}

	private void handleExtras(){
		meals_ = new Vector<Meal>();
		
		Bundle extras = getIntent().getExtras();
		if(extras != null){
			ArrayList<Meal> m = (ArrayList<Meal>)extras.getSerializable("org.pocketcampus.suggestions.meals");
			if(m != null && !m.isEmpty()){
				for(Meal meal : m){
					meals_.add(meal);
				}
			}
		}else{
			Log.d("SUGGESTIONS", "Pas reçu d'extras à l'aller !");
		}
	}
	
	public Collection<Meal> computeSuggestions(Collection<Meal> meals, MealTag tag, MealTagger tagger){

		Collection<Meal> returnedMeals = null;
		returnedMeals = tagger.parseMealsFor(tag, meals);

		if(returnedMeals==null){
			return new HashSet<Meal>();
		}
		return returnedMeals;
	}

	private void finalizeSuggestions(){
		
		tagger_ = new MealTagger();
		Collection<Meal> computeDislikeMeals = new HashSet<Meal>();
		Collection<Meal> computeLikeMeals = new HashSet<Meal>();

		if(! likes_.isEmpty() && meals_ != null){
			for(MealTag tag : likes_){
				computeLikeMeals.addAll(computeSuggestions(meals_, tag, tagger_));
			}
		}else if ((likes_.isEmpty()) && (meals_ != null)){
			computeLikeMeals.addAll(meals_);
		}

		if(! dislikes_.isEmpty() && meals_ != null){				
			for(MealTag tag : dislikes_){
				computeDislikeMeals.addAll(computeSuggestions(meals_, tag, tagger_));
			}
		}

		Collection<Meal> computeMeals = new HashSet<Meal>(computeLikeMeals);
		computeMeals.removeAll(computeDislikeMeals);

		ArrayList<Meal> list = new ArrayList<Meal>();
		for(Meal meal : computeMeals){
			list.add(meal);
		}
		
		Intent menus = new Intent(context_, FoodPlugin.class);
		menus.putExtra("org.pocketcampus.suggestions.meals", list);
		setResult(Activity.RESULT_OK, menus);
		finish();
		
	}
	
	

}