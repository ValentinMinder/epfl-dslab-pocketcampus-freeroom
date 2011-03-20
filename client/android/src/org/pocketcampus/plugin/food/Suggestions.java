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
import org.pocketcampus.plugin.food.menu.Meal;
import org.pocketcampus.plugin.food.menu.MealTag;
import org.pocketcampus.plugin.food.menu.MealTagger;
import org.pocketcampus.plugin.mainscreen.MainscreenPlugin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class Suggestions extends Activity{
	private Context context_;

	private Collection<Meal> meals_;
	private Vector<MealTag> likes_ = new Vector<MealTag>();
	private Vector<MealTag> dislikes_ = new Vector<MealTag>();

	private Spinner likeSpinner_;
	private Spinner dislikeSpinner_;

	private TextView likeTextView_;
	private TextView dislikeTextView_;

	private MealTag likeTag_;
	private MealTag dislikeTag_;

	private MealTagger tagger_;

	private ImageButton addButton_like_;
	private ImageButton removeButton_like_;
	private ImageButton addButton_dislike_;
	private ImageButton removeButton_dislike_;

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.food_suggestions);

		// Header
		ActionBar actionBar = (ActionBar) findViewById(R.id.actionbar);
		actionBar.setTitle("PocketCampus EPFL");
		actionBar.addAction(new ActionBar.IntentAction(this, MainscreenPlugin
				.createIntent(this), R.drawable.mini_home));

		context_ = this.getApplicationContext();
		
		TextView t = (TextView)findViewById(R.id.food_suggestions_explanation);
		
		
//		handleExtras();
//		
//		MealTag[] mTags = MealTag.values();
//
//		/*======================================== LIKES =================================================*/
//		
		likeSpinner_ = (Spinner) findViewById(R.id.food_suggestions_spinner_like);
//		ArrayAdapter<MealTag> likeAdapter = new ArrayAdapter<MealTag>(context, R.layout.restaurant_suggestions_list_item, mTags);
//		likeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//
//		likeSpinner.setAdapter(likeAdapter);
//
		likeTextView_ = (TextView) findViewById(R.id.food_suggestions_display_like);
//
//		likeSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
//			@Override
//			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
//				likeTag = (MealTag)arg0.getItemAtPosition(arg2);
//			}
//			@Override
//			public void onNothingSelected(AdapterView<?> arg0) {}
//
//		});
//
		addButton_like_ = (ImageButton) findViewById(R.id.food_suggestions_add_like);
//
//		addButton_like.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				addLikeSuggestion();
//			}
//		});
//
		removeButton_like_ = (ImageButton) findViewById(R.id.food_suggestions_remove_like);
		removeButton_like_.setEnabled(false);
//
//		removeButton_like.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				removeLikeSuggestion();
//			}
//		});
//
//		/*======================================= DISLIKES ================================================*/
//
		dislikeSpinner_ = (Spinner) findViewById(R.id.food_suggestions_spinner_dislike);
//		ArrayAdapter<MealTag> dislikeAdapter = new ArrayAdapter<MealTag>(context, R.layout.restaurant_suggestions_list_item, mTags);
//		dislikeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//
//		dislikeSpinner.setAdapter(dislikeAdapter);
//
		dislikeTextView_ = (TextView) findViewById(R.id.food_suggestions_display_dislike);
//
//		dislikeSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
//			@Override
//			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
//				dislikeTag = (MealTag)arg0.getItemAtPosition(arg2);
//			}
//			@Override
//			public void onNothingSelected(AdapterView<?> arg0) {}
//
//		});
//
		addButton_dislike_ = (ImageButton) findViewById(R.id.food_suggestions_add_dislike);
//
//		addButton_dislike.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				addDislikeSuggestion();
//			}
//		});
//
		removeButton_dislike_ = (ImageButton) findViewById(R.id.food_suggestions_remove_dislike);
		removeButton_dislike_.setEnabled(false);
//		
//		removeButton_dislike.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				removeDislikeSuggestion();
//			}
//		});
//
//		/*======================================= SUGGESTIONS ================================================*/
//
//		ImageButton suggestButton = (ImageButton) findViewById(R.id.food_suggestions_button);
//		suggestButton.setOnClickListener(new OnClickListener(){
//
//			@Override
//			public void onClick(View arg0) {
//
//				MealTagger tagger = new MealTagger();
//				Collection<Meal> computeDislikeMeals = new HashSet<Meal>();
//				Collection<Meal> computeLikeMeals = new HashSet<Meal>();
//
//				if(! likes.isEmpty() && meals != null){
//					for(MealTag tag : likes){
//						computeLikeMeals.addAll(computeSuggestions(meals, tag, tagger));
//					}
//				}else if ((likes.isEmpty()) && (meals != null)){
//					computeLikeMeals.addAll(meals);
//				}
//
//				if(! dislikes.isEmpty() && meals != null){				
//					for(MealTag tag : dislikes){
//						computeDislikeMeals.addAll(computeSuggestions(meals, tag, tagger));
//					}
//				}
//
//				Collection<Meal> computeMeals = new HashSet<Meal>(computeLikeMeals);
//				computeMeals.removeAll(computeDislikeMeals);
//
//				ArrayList<Meal> list = new ArrayList<Meal>();
//				for(Meal meal : computeMeals){
//					list.add(meal);
//				}
//
//				Intent menus = new Intent(context, DailyMenus.class);
//				menus.putExtra("org.pocketcampus.suggestions.meals", list);
//				setResult(Activity.RESULT_OK, menus);
//				finish();
//			}
//		});

	}

	/*private void handleExtras(){
		meals = new Vector<Meal>();
		
		Bundle extras = getIntent().getExtras();
		if(extras != null){
			ArrayList<Meal> m = (ArrayList<Meal>)extras.getSerializable("Meals");
			if(m != null){
				for(Meal meal : m){
					meals.add(meal);
				}
			}
		}
	}
	
	private void addSuggestion(MealTag tag, Vector<MealTag> tagVector, Vector<MealTag> otherTagVector, ImageButton removeButton, ImageButton addButton, TextView text){
		if(tag!=null){
			if((tag instanceof MealTag)
					&& !(tagVector.contains(tag))
					&& (tagVector.size() < 3)){
				if(otherTagVector.contains(tag)){
					Toast.makeText(context, getResources().getString(R.string.resto_suggestions_alreadyAdded), Toast.LENGTH_SHORT).show();
				}else{
					tagVector.add(tag);
					removeButton.setEnabled(true);
					if(tagVector.size() == 1){
						text.setText(write(tag));
					}else{							
						text.setText(text.getText() + write(tag));
					}
				}
			}					
		}
		if(tagVector.size() == 3){
			addButton.setEnabled(false);
		}
	}
	
	private void addLikeSuggestion(){
		addSuggestion(likeTag, likes, dislikes, removeButton_like, addButton_like, likeTextView);
	}
	
	private void addDislikeSuggestion(){
		addSuggestion(dislikeTag, dislikes, likes, removeButton_dislike, addButton_dislike, dislikeTextView);
	}
	
	private void removeSuggestion(Vector<MealTag> tagVector, ImageButton addButton, ImageButton removeButton, TextView text){
		if(!tagVector.isEmpty()){
			tagVector.remove(tagVector.get(tagVector.size()-1));
			addButton.setEnabled(true);
			text.setText(null);
			for(MealTag t : tagVector){
				text.setText(text.getText() + write(t));					
			}
		}
		if(tagVector.size() == 0){
			removeButton.setEnabled(false);
		}
	}
	
	private void removeLikeSuggestion(){
		removeSuggestion(likes, addButton_like, removeButton_like, likeTextView);
	}
	
	private void removeDislikeSuggestion(){
		removeSuggestion(dislikes, addButton_dislike, removeButton_dislike, dislikeTextView);
	}
	
	public static Collection<Meal> computeSuggestions(Collection<Meal> meals, MealTag tag, MealTagger tagger){

		Collection<Meal> returnedMeals = null;
		returnedMeals = tagger.parseMealsFor(tag, meals);

		if(returnedMeals==null){
			return new HashSet<Meal>();
		}
		return returnedMeals;
	}

	private String write(MealTag tag){
		Resources r = getResources();

		String string = "";

		switch (tag){

		case MEAT :
			string = r.getString(R.string.resto_suggestions_meat).concat("\n");
			break;
		case FISH :
			string = r.getString(R.string.resto_suggestions_fish).concat("\n");
			break;
		case VEGETARIAN :
			string = r.getString(R.string.resto_suggestions_vege).concat("\n");
			break;
		case PASTA :
			string = r.getString(R.string.resto_suggestions_pasta).concat("\n");
			break;
		case PORC :
			string = r.getString(R.string.resto_suggestions_porc).concat("\n");
			break;
		case CHICKEN :
			string = r.getString(R.string.resto_suggestions_chicken).concat("\n");
			break;
		case BEEF :
			string = r.getString(R.string.resto_suggestions_beef).concat("\n");
			break;
		case HORSE :
			string = r.getString(R.string.resto_suggestions_horse).concat("\n");
			break;
		default :
			string = "No existing Tag";
		}

		return string;
	}*/

}