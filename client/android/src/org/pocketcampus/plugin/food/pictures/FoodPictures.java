package org.pocketcampus.plugin.food.pictures;

import org.pocketcampus.R;
import org.pocketcampus.core.communication.RequestHandler;
import org.pocketcampus.core.ui.ActionBar;
import org.pocketcampus.core.ui.ActionBar.Action;
import org.pocketcampus.plugin.food.FoodPlugin;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class FoodPictures extends Activity {
	private ActionBar actionBar_;

	private static RequestHandler foodRequestHandler_;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.food_picture_gallery);

		setupActionBar(true);

		// GalleryView

		// RequestHandler
		foodRequestHandler_ = FoodPlugin.getFoodRequestHandler();
	}

	protected void setupActionBar(boolean addHomeButton) {

		actionBar_ = (ActionBar) findViewById(R.id.actionbar);
		actionBar_.addAction(new Action() {
			@Override
			public void performAction(View view) {
				// foodDisplayHandler_.refreshView();
			}

			@Override
			public int getDrawable() {
				return R.drawable.refresh;
			}
		});
	}

	public void picturesRefreshing() {
		actionBar_.setProgressBarVisibility(View.VISIBLE);
	}

	public void picturesRefreshed(boolean successful) {
		if (!successful) {
			Toast.makeText(this,
					this.getResources().getString(R.string.food_menucancelled),
					Toast.LENGTH_SHORT).show();
		}
		actionBar_.setProgressBarVisibility(View.GONE);
	}

	/**
	 * Displays the current view, queue or meal pictures.
	 */
	public void displayView() {

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Resources res = getResources();
		menu.add(0, 1, 0, res.getText(R.string.food_show_restaurant)).setIcon(
				R.drawable.food_opt_icon_resto);
		menu.add(0, 2, 0, res.getText(R.string.food_show_ratings)).setIcon(
				R.drawable.food_opt_icon_ratings);
		menu.add(0, 3, 0, res.getText(R.string.food_show_sandwiches)).setIcon(
				R.drawable.food_opt_icon_sandwich);
		menu.add(0, 4, 0, res.getText(R.string.food_show_suggestions)).setIcon(
				R.drawable.food_opt_icon_suggestions);
		return true;
	}

	final int SUGGESTIONS_REQUEST_CODE = 1;

	public boolean onOptionsItemSelected(MenuItem item) {
		int selectedId = item.getItemId();

		switch (selectedId) {
		case 1: // Show meal pictures
			break;
		case 2: // Show queue pictures
			break;
		case 3: // Take picture
//			return takePicture();
			break;
		}

		return false;
	}
	
//	public boolean takePicture(){
//		
//	}

//	public void onActivityResult(int requestCode, int resultCode, Intent data) {
//		super.onActivityResult(requestCode, resultCode, data);
//
//		switch (requestCode) {
//		case SUGGESTIONS_REQUEST_CODE: // Result from the Suggestions class
//			if (resultCode == Activity.RESULT_OK) {
//				Bundle extras = data.getExtras();
//				if (extras != null) {
//					@SuppressWarnings("unchecked")
//					ArrayList<Meal> list = (ArrayList<Meal>) extras
//							.getSerializable("org.pocketcampus.suggestions.meals");
//
//					foodDisplayHandler_.updateSuggestions(list);
//					foodDisplayHandler_.setDisplayType(4);
//					displaySuggestions();
//				} else {
//					Log.d("SUGGESTIONS", "Pas d'extras !");
//				}
//			} else {
//				Log.d("SUGGESTIONS", "RESULT_PAS_OK !");
//			}
//			break;
//		}
//	}
}
