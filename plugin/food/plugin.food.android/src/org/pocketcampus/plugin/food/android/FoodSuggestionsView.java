package org.pocketcampus.plugin.food.android;

import java.util.List;

import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginView;
import org.pocketcampus.android.platform.sdk.ui.element.ListViewElement;
import org.pocketcampus.android.platform.sdk.ui.layout.StandardLayout;
import org.pocketcampus.plugin.food.android.iface.IFoodModel;
import org.pocketcampus.plugin.food.android.iface.IFoodView;
import org.pocketcampus.plugin.food.android.utils.MealTag;

import android.app.Service;
import android.os.Bundle;
import android.widget.Toast;

public class FoodSuggestionsView extends PluginView implements IFoodView {
	private FoodController mController;
	private IFoodModel mModel;
	
	private StandardLayout mLayout;
	private ListViewElement mListView;
	
	private List<MealTag> mTagsList;
	
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
		mListView = new ListViewElement(this, mTagsList);
		
		//Set the layout
		mLayout.addView(mListView);
		
		// The ActionBar is added automatically when you call setContentView
		setContentView(mLayout);
	}
	
	@Override
	public void networkErrorHappened() {
		Toast toast = Toast.makeText(getApplicationContext(), "Network error!",
				Toast.LENGTH_SHORT);
		toast.show();
	}

	@Override
	public void menusUpdated() {
		// TODO Auto-generated method stub
		
	}
	
}
