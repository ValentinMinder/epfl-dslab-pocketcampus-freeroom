package org.pocketcampus.plugin.food.pictures;

/**
 * Represents the meal pictures gallery.
 * @author Elodie
 */
import org.pocketcampus.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class FoodGalleryView extends Activity {
	// Test pictures.
	private Drawable[] pictures_ = {
			getResources().getDrawable(R.drawable.antartica1),
			getResources().getDrawable(R.drawable.antartica1),
			getResources().getDrawable(R.drawable.antartica3),
			getResources().getDrawable(R.drawable.antartica4),
			getResources().getDrawable(R.drawable.antartica5),
			getResources().getDrawable(R.drawable.antartica6),
			getResources().getDrawable(R.drawable.antartica7),
			getResources().getDrawable(R.drawable.antartica8),
			getResources().getDrawable(R.drawable.antartica9),
			getResources().getDrawable(R.drawable.antartica10) };
	private ImageView imageView_;

	private PictureFetcher pictureFetcher_;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.food_picture_gallery);

		Gallery ga = (Gallery) findViewById(R.id.MealGallery);
		ga.setAdapter(new ImageAdapter(this));

		/*
		 * The gallery used to print the pictures, we'll add the adapter when we
		 * get all url string
		 */

		imageView_ = (ImageView) findViewById(R.id.MealPicture);

		Bundle bundle = getIntent().getExtras();

//		pictureFetcher_ = new PictureFetcher(PictureType.Meal, new Meal());

		// pictures_ = pictureFetcher_.getPictures();

		imageView_.setImageDrawable(pictures_[0]);

		ga.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View clickedView,
					int position, long arg3) {
				Toast.makeText(
						getBaseContext(),
						"You have selected picture " + (position + 1)
								+ " of Antartica", Toast.LENGTH_SHORT).show();
				imageView_.setImageDrawable(pictures_[position]);
			}
		});

	}

	// Box that inform user there was no picture
	private void printNoPictureFound(final Activity activity) {
		Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.food_gallery_nopic_title);

		builder.setMessage(R.string.food_gallery_nopic_msg);
		builder.setPositiveButton("OK", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				activity.finish();

			}
		});
		builder.show();
	}

	public class ImageAdapter extends BaseAdapter {

		private Context ctx;
		int imageBackground;

		public ImageAdapter(Context c) {
			ctx = c;
			TypedArray ta = obtainStyledAttributes(R.styleable.MealGallery);
			imageBackground = ta.getResourceId(
					R.styleable.MealGallery_android_galleryItemBackground, 1);
			ta.recycle();
		}

		@Override
		public int getCount() {
			return pictures_.length;
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			ImageView iv = new ImageView(ctx);
			iv.setImageDrawable(pictures_[arg0]);
			iv.setScaleType(ImageView.ScaleType.FIT_XY);
			iv.setLayoutParams(new Gallery.LayoutParams(500, 500));
			iv.setBackgroundResource(imageBackground);
			return iv;
		}

	}
}