package org.pocketcampus.plugin.food.pictures;

import org.pocketcampus.R;
import org.pocketcampus.shared.plugin.food.Meal;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;

/**
 * Class will represent a rating dialog, that will be opened when rating a meal.
 * 
 */
public class PictureTypeDialog extends Dialog {
	private Meal meal;
	private Context ctx_;

	public PictureTypeDialog(Context context, Meal meal) {
		super(context);
		this.meal = meal;
		this.ctx_ = context;

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		/** Design the dialog in main.xml file */
		setContentView(R.layout.food_dialog_picture);
	}

}
