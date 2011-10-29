package org.pocketcampus.plugin.food.android;

import java.util.List;
import java.util.Vector;

import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginView;
import org.pocketcampus.android.platform.sdk.ui.element.CheckBoxesListViewElement;
import org.pocketcampus.android.platform.sdk.ui.layout.StandardLayout;
import org.pocketcampus.plugin.food.android.iface.IFoodModel;
import org.pocketcampus.plugin.food.android.iface.IFoodView;
import org.pocketcampus.plugin.food.android.utils.MealTag;

import android.app.Service;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class FoodSuggestionsView extends PluginView implements IFoodView {
	private FoodController mController;
	private IFoodModel mModel;

	private StandardLayout mLayout;
	private CheckBoxesListViewElement mListView;

	private List<MealTag> mTagsList;
	private Vector<MealTag> mLikes = new Vector<MealTag>();
	private Vector<MealTag> dislikes_ = new Vector<MealTag>();
	private MealTag mLikeTag;
	private MealTag dislikeTag_;

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

		//Get the tags from the controller
		mTagsList = mController.getMealTags();

		//Add them to the listView
		mListView = new CheckBoxesListViewElement(this, mTagsList);

		//Set onClickListeners
		setOnListViewClickListener();

		//Set the layout
		mLayout.addView(mListView);

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

	private void setOnListViewClickListener() {

		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				//arg2 = position;
				Toast.makeText(getApplicationContext(), mTagsList.get(arg2).toString(), 
						Toast.LENGTH_SHORT).show();
				mLikeTag = mTagsList.get(arg2);
				
			}
			
		});
		
//		mListView.setOnPositiveBoxCheckedChangeListener(new OnCheckedChangeListener() {
//
//			@Override
//			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//				
//				TextView t = (TextView)buttonView.getRootView().findViewById(R.id.sdk_list_checkbox_entry_text);
//				if(t != null)
//					Toast.makeText(getApplicationContext(), t.getText(), Toast.LENGTH_SHORT).show();
//				
//				if(isChecked){
////					addLikeSuggestion();
//				} else {
////					removeLikeSuggestion();
//				}
//				
//			}
//		});
//
//		mListView.setOnNegativeBoxCheckedChangeListener(new OnCheckedChangeListener() {
//
//			@Override
//			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//				Toast.makeText(getApplicationContext(), "GRUUUUUUUUUUUUUUUNT -", Toast.LENGTH_SHORT).show();
//			}
//			
//		});

	}
	
	private void addSuggestion(MealTag tag, Vector<MealTag> tags){
		if(!tags.contains(tag)){
			tags.add(tag);
		}
	}

	private void addLikeSuggestion(){
		addSuggestion(mLikeTag, mLikes);
	}

	private void addDislikeSuggestion(){
		addSuggestion(dislikeTag_, dislikes_);
	}

	private void removeSuggestion(MealTag tag, Vector<MealTag> tags){
		if(tags.contains(tag)){
			tags.remove(tag);
		}
	}

	private void removeLikeSuggestion(){
		removeSuggestion(mLikeTag, mLikes);
	}

	private void removeDislikeSuggestion(){
		removeSuggestion(dislikeTag_, dislikes_);
	}

//	private String write(MealTag tag){
//		Resources r = activityContext_.getResources();
//
//		String string = "";
//
//		switch (tag){
//		case MEAT :
//			string = r.getString(R.string.food_suggestions_meat).concat("\n");
//			break;
//		case FISH :
//			string = r.getString(R.string.food_suggestions_fish).concat("\n");
//			break;
//		case VEGETARIAN :
//			string = r.getString(R.string.food_suggestions_vege).concat("\n");
//			break;
//		case PASTA :
//			string = r.getString(R.string.food_suggestions_pasta).concat("\n");
//			break;
//		case RICE :
//			string = r.getString(R.string.food_suggestions_rice).concat("\n");
//			break;
//		case PORC :
//			string = r.getString(R.string.food_suggestions_porc).concat("\n");
//			break;
//		case CHICKEN :
//			string = r.getString(R.string.food_suggestions_chicken).concat("\n");
//			break;
//		case BEEF :
//			string = r.getString(R.string.food_suggestions_beef).concat("\n");
//			break;
//		case HORSE :
//			string = r.getString(R.string.food_suggestions_horse).concat("\n");
//			break;
//		default :
//			string = r.getString(R.string.food_suggestions_notag).concat("\n");
//		}
//
//		return string;
//	}
	
}
