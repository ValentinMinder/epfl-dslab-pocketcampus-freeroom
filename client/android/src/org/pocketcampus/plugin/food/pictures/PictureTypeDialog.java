package org.pocketcampus.plugin.food.pictures;

import org.pocketcampus.R;
import org.pocketcampus.plugin.food.pictures.CameraCapture.PictureType;
import org.pocketcampus.shared.plugin.food.Meal;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;

/**
 * Class will represent a rating dialog, that will be opened when rating a meal.
 * 
 */
public class PictureTypeDialog extends Dialog {
	private Meal displayedMeal_;
	private Activity ctx_;
	private ImageButton mealButton_;
	private ImageButton queueButton_;

	public PictureTypeDialog(Activity context, Meal meal) {
		super(context);
		this.displayedMeal_ = meal;
		this.ctx_ = context;

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		/** Design the dialog in main.xml file */
		setContentView(R.layout.food_dialog_picture);
		
		// Dialog box is closed when we touch outside.
		setCanceledOnTouchOutside(true);

		mealButton_ = (ImageButton) findViewById(R.id.food_picturedialog_meal);
		mealButton_.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(ctx_, CameraCapture.class);
				intent.putExtra("Meal", displayedMeal_);  
				intent.putExtra("PictureType", PictureType.Meal);
				
				ctx_.startActivity(intent);
			}
		});
		
		queueButton_ = (ImageButton) findViewById(R.id.food_picturedialog_queue);
		queueButton_.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
//				PictureTaker taker = new PictureTaker(ctx_, displayedMeal_, PictureType.Queue);
//				taker.takePicture();
			}
		});
	}
}
