package org.pocketcampus.plugin.food.pictures;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.pocketcampus.R;
import org.pocketcampus.core.communication.FileUpload;
import org.pocketcampus.core.communication.FileUploadParameters;
import org.pocketcampus.plugin.food.FoodPlugin;
import org.pocketcampus.shared.plugin.food.Meal;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.util.Log;
import android.widget.Toast;

public class CameraCapture extends Activity {

	protected boolean _taken = true;
	File sdImageMainDirectory;

	protected static final String PHOTO_TAKEN = "photo_taken";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Meal m = (Meal) getIntent().getExtras().get("Meal");
		PictureType type = (PictureType) getIntent().getExtras().get(
				"PictureType");

		if (m == null || type == null) {
			finish();
		}

		Log.d("MealPictures", "Taking picture of: " + m.getName_());
		Log.d("MealPictures", "Picture type: " + type.toString());

		try {
			super.onCreate(savedInstanceState);
			File root = new File(Environment.getExternalStorageDirectory()
					+ File.separator + "PCPics" + File.separator);
			root.mkdirs();

			sdImageMainDirectory = new File(root, m.hashCode() + ".jpg");

			startCameraActivity();
		} catch (Exception e) {
			finish();
			Toast.makeText(this, "Error occured. Please try again later.",
					Toast.LENGTH_SHORT).show();
		}
	}

	protected void startCameraActivity() {
		Uri outputFileUri = Uri.fromFile(sdImageMainDirectory);

		Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
		intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

		startActivityForResult(intent, 0);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (resultCode) {
		case 0:
			finish();
			break;

		case -1:
			try {
				StoreImage(this, Uri.parse(data.toURI()), sdImageMainDirectory);
				uploadImage(this, sdImageMainDirectory);

				Log.d("MealPicture", sdImageMainDirectory.getAbsolutePath());
			} catch (Exception e) {
				e.printStackTrace();
			}
			finish();
			startActivity(new Intent(CameraCapture.this, FoodPlugin.class));
		}
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		if (savedInstanceState.getBoolean(CameraCapture.PHOTO_TAKEN)) {
			_taken = true;
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putBoolean(CameraCapture.PHOTO_TAKEN, _taken);
	}

	public static void StoreImage(Context mContext, Uri imageLoc, File imageDir) {
		Bitmap bm = null;
		try {
			bm = Media.getBitmap(mContext.getContentResolver(), imageLoc);
			FileOutputStream out = new FileOutputStream(imageDir);
			bm.compress(Bitmap.CompressFormat.JPEG, 100, out);
			bm.recycle();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void uploadImage(final Context mContext, File image) {
		FileUpload request = new FileUpload() {

			@Override
			protected void doInBackgroundThread(String result) {
				if (result != null) {
					if (result.contains("true")) {
						// Do something in the UI
						Log.d("MealPicture", "Submitted");
						Toast.makeText(
								mContext,
								mContext.getResources().getString(
										R.string.food_picture_submitted),
								Toast.LENGTH_SHORT).show();
					} else {
						Log.d("MealPicture", "Submitted");
						Toast.makeText(
								mContext,
								mContext.getResources().getString(
										R.string.food_picture_notsubmitted),
								Toast.LENGTH_SHORT).show();
					}
				} else {
					// An error occured, and the field 'exception_' should
					// contain something
					Log.d("MealPicture", "Not Submitted");
					Toast.makeText(
							mContext,
							mContext.getResources().getString(
									R.string.food_check_connection),
							Toast.LENGTH_SHORT).show();
				}
			}

			@Override
			protected void cancelled() {
				// Generally thrown when a timeout occured
				Log.d("MealPicture", "Cancelled");
			}
		};

		// We put the file into a FileUploadParameters
		FileUploadParameters params = new FileUploadParameters();
		params.addParameter("image", image);

		FoodPlugin.getFoodRequestHandler().execute(request, "uploadimage",
				params);
	}

	public enum PictureType {
		Meal, Queue
	}
}
